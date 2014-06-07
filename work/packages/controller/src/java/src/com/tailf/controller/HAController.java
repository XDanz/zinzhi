package com.tailf.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;

import com.tailf.ha.HaException;
import com.tailf.ha.HaStateType;
import com.tailf.notif.HaNotification;

public class HAController {
    private static final Logger log = Logger.getLogger ( HAController.class );

    // The singleton instance
    private static HAController haControllerInstance = null;

    // The current configuration data
    private HAConfiguration configurationData;

    // The current lock which is used by when writing
    // the singleton instance.
    private static Object lock = new Object();

    public HAConfiguration getHAConfiguration () {
        return this.configurationData;
    }

    /**
     *  Returns the Current initialized singleton instance
     *  of this HAController. At initialization read the config
     *  from tail-ha-controller.yang. Subsequently call to this
     *  method will return the current instance of this
     *  HAController.
     *
     *  @return Instance of HAController. On first invocation
     *  the instance will read configuration from CDB.
     */
    public static HAController getController()
        throws Exception {
        synchronized ( lock ) {
            if ( haControllerInstance == null ) {
                haControllerInstance = new HAController();
            }
        }
        return haControllerInstance;
    }
    // private constructor should only be invoked once
    // it will read configuration from CDB.
    private HAController () throws Exception {
        log.debug ( " xxx initializing HAController xxx ");

        // reads inital configuration
        this.configurationData = new HAConfiguration ();

        // determines who is local node and who is the remote node
        this.configurationData.recognizeNodeByInterfaces();

        // start the HA Acceptor thread
        log.info ( " starting acceptor ");
        HAControllerAcceptor.execute();
    }

    // Callback method will be called on certain HA events
    // This is invoked by the Applications run thread.
    public void haEvent ( HaNotification haEvent ) throws Exception {

        switch ( haEvent.getHAInfoType() ) {
        case HaNotification.HA_INFO_NOMASTER:
            {
                log.info(" => MASTER DIED !!!");
                masterDown();
            }
            break;
        case HaNotification.HA_INFO_SLAVE_DIED:
            {
                log.info(" => SLAVE DIED !!!" );
                slaveDown();
            }
            break;
        case HaNotification.HA_INFO_SLAVE_ARRIVED:
            {
                log.info ( " => SLAVE " + haEvent.getHANode() +
                           " ARRIVED !!!");
            }
            break;
        case HaNotification.HA_INFO_SLAVE_INITIALIZED:
            {
                // Local Node has been initialized to slave
                // and CdbTxId should be the same as the master
                // remote the eventTxId
                log.info ( " => SLAVE INITIALIZED !!!");
                HALocalNode local =
                    (HALocalNode)getLocalHANode();
                local.saveTxId();
            }
            break;
        case HaNotification.HA_INFO_IS_MASTER:
            {
                log.info ( " => IS MASTER !!!");
                // log
            }
            break;
        case HaNotification.HA_INFO_IS_NONE:
            {
                log.info ( " => IS NONE !!!");
            }
            break;
        }
    }


    /**
     *  Command the local node to be master.
     *  and auto- initialize the VIPs.
     *
     *  @throws HAControllerException
     *  where cause could be a ConfException or
     *  a IOException.
     */
    void nodeBeMaster (HANode node) throws Exception {

        log.info( "nodeBeMaster() =>");

        try {
            node.beMaster();
        } catch ( HaException e ) {
            throw new HAControllerException ( e );
        } catch ( IOException e ) {
            throw new HAControllerException ( e );
        }

        // ok bring up vips
        try {
            HAControllerVipManager vipManager =
                HAControllerVipManager.getManager();

            vipManager.initializeAvailableVips();

        } catch ( HAControllerException e ) {
            log.warn("VIPS not initialized!", e );
        }
        log.info( " localBeMaster() => ok");
    }

    //  On event when master went down
    void masterDown () throws Exception {
        // master down
        haNodeDown ();
        getLocalHANode().beMaster();
    }

    void slaveDown () throws Exception {
        haNodeDown();
    }

    // A node went down save the transaction ID to disk
    void haNodeDown () throws Exception {
        HANode node = getLocalHANode();
        node.saveTxId();

        // start executing the reconnector only if the local
        // node is the Preferred Master.
        if ( node.isPreferredMaster() ) {
            HAControllerReConnector.execute();
        }
    }

    // Callback method will be called when HA Node has been
    // reconnected i.e when HAControllerReConnector has done
    // its job
    public void reConnected ( ) throws Exception {
        log.info("reConnected() =>");
        reConciliation();
        log.info("reConnected() => ok");
    }

    void reConciliation () throws Exception {
        log.info ( "reconciliation() =>");
        HANode remote = getRemoteHANode();
        HANode local = getLocalHANode();

        if (local.txDiff() && remote.txDiff()) {
            log.info(" CASE 1 TX DIFF ON BOTH NODES");
            //   ERROR need manual intervention!
        }
        else if (local.txDiff() && !remote.txDiff()) {
            log.info(" CASE 2 LOCAL HAS TX DIFF");
            local.beMaster();
            remote.beSlave( local );
        }
        else if (!local.txDiff() && remote.txDiff()) {
            log.info(" CASE 3 REMOTE HAS TX DIFF");
            remote.beMaster();
            local.beNone();
            local.beSlave( remote );
        }
        else if (!local.txDiff() && !remote.txDiff()) {
            log.info(" CASE 4 NO TX DIFF");
            preferredMaster().beMaster();
            notPreferredMaster().beSlave( preferredMaster() );
        }
        log.info( "reConciliation() => ok");
    }

    void determinationByTxId () throws Exception {
        log.info(" determinationByTxId () => ");

        if ( getRemoteHANode().isReachable () ) {
            reConciliation ();
        } else {
            getLocalHANode().beMaster();
        }
        log.info(" determinationByTxId () => ok");
    }

    /**
     * Return the HANode that is the preferred master.
     *
     * @return The HANode which is configured as preferred master
     */
    public HANode preferredMaster() throws Exception {
        return ((getLocalHANode().isPreferredMaster())? getLocalHANode() :
                getRemoteHANode() );
    }

    /**
     * Return the HANode that is NOT the preferred master.
     *
     * @return The HANode which is NOT configured as the preferred master
     */
    public HANode notPreferredMaster() throws Exception {
        return (getLocalHANode().isPreferredMaster())? getRemoteHANode() :
            getLocalHANode();
    }

    /**
     *  Returns the remote HANode
     *  @return The remote HANode
     *  @throws
     */
    public HANode getRemoteHANode () throws Exception {
        return this.configurationData.getRemoteHANode();
    }

    public List<InetAddress> getVips () {
        return this.configurationData.getVips();
    }

    /**
     *  Returns the local HANode
     *
     *  @return The remote HANode
     *  @throws
     */
    public HANode getLocalHANode() throws Exception {
        return this.configurationData.getLocalHANode();
    }

    public String getSecretToken() throws Exception {
        return this.configurationData.getSecretToken();
    }

    void determinationByNoTxDiff() throws Exception {

        HANode remote = getRemoteHANode();
        switch ( remote.getHaStatus() ) {
        case NONE:
            {
                log.info(" NONE ");
                if ( getLocalHANode().isPreferredMaster() ) {
                    getLocalHANode().beMaster();

                } else {
                    try {
                        Thread.sleep(1000);
                    } catch ( InterruptedException e ) {
                        log.error("",e);
                    }
                    initialDetermination ();
                }
                break;
            }

        case MASTER:
            getLocalHANode().beSlave( getRemoteHANode() );
            break;
        case SLAVE:
            {

            }
            break;
        case SLAVE_RELAY:
            {} break;
        }
    }

    // is called first by the init-x thread to deterimine who should
    // be master.
    void initialDetermination () throws Exception {
        log.info(" initialDetermination () =>");
        HANode remote = getRemoteHANode();
        HANode local = getLocalHANode();

        //try {
        if ( remote.isReachable() ) {

            if (  local.txDiff() || remote.txDiff() ) {
                determinationByTxId ();
            } else {
                log.warn (" NO TXDIFF ON ANY NODES!");
                switch ( remote.getHaStatus() ) {
                case NONE:
                    {
                        log.info(" NONE ");
                        if ( getLocalHANode().isPreferredMaster() ) {
                            getLocalHANode().beMaster();

                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch ( InterruptedException e ) {
                                log.error("",e);
                            }
                            initialDetermination ();
                        }
                        break;
                    }

                case MASTER:
                    {
                        log.info(" --remote was MASTER!--");
                        // The remote HA node was MASTER
                        // and there was no transaction diff
                        // on either nodes so we determine
                        // the local node to be slave
                        // maybe we need to check HAStatus of the
                        // local node before promoting the local
                        // node to be slave.
                        log.info(" CALL (" + getLocalHANode() +
                                 ").beSlave =>");
                        HaStateType haState =
                            getLocalHANode().getHaStatus();
                        switch ( haState ) {
                        case MASTER:
                            {
                                // no txdiff has occured
                                // so we promote the local
                                // node tx be slave by first
                                // calling beNone();
                                getLocalHANode().beNone();
                                // and now call beSlave()
                                log.info(" CALL (" + getLocalHANode()
                                         + ").beSlave() =>");
                                getLocalHANode()
                                    .beSlave( getRemoteHANode());

                                log.info(" CALL (" + getLocalHANode()
                                         + ").beSlave() => ok");

                            }
                            break;
                        case NONE:
                            {
                                // no txdiff has occured
                                // so we promote the local
                                // node to be slave
                                log.info(" CALL (" + getLocalHANode()
                                         + ").beSlave() =>");
                                getLocalHANode()
                                    .beSlave(getRemoteHANode());
                                log.info(" CALL (" + getLocalHANode()
                                         + ").beSlave() => ok");
                            }
                            break;
                        case SLAVE:
                            log.info (getLocalHANode() + " already slave!");
                            break;
                        case SLAVE_RELAY:
                            log.info (" slave relay --");
                        }
                    }
                    break;
                case SLAVE:
                    {
                        log.info (" remote already slave! --");
                    }
                    break;
                case SLAVE_RELAY:
                    {
                        log.info(" slave relay --");
                    } break;
                }
            }
        } else {
            log.info ( remote + " IS DOWN !" );
            log.info( " SAVING TXID! ");
            getLocalHANode().saveTxId();
            log.info ("COMMAND LOCAL => MASTER");
            getLocalHANode().beMaster();
        }
        log.info ("initialDetermination() => ok");
        return ;
        // }  catch ( Exception e ) {
        //     log.error("",e);
        //     // TODO
        // } 
    }
}
