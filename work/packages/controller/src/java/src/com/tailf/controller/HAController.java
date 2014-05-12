package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;

import java.net.Socket;
import java.net.InetAddress;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbSession;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbTxId;


import com.tailf.conf.ConfIP;
import com.tailf.conf.ConfNoExists;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfXMLParamLeaf;
import com.tailf.conf.ConfXMLParamCdbStart;
import com.tailf.conf.ConfXMLParamStop;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfList;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfHaNode;
import com.tailf.conf.ConfException;

import com.tailf.notif.HaNotification;
import com.tailf.notif.NotificationType;

import com.tailf.ha.Ha;
import com.tailf.ncs.NcsMain;

import org.apache.log4j.Logger;

public class HAController {
    private static final Logger log = Logger.getLogger ( HAController.class );

    private static HAController haControllerInstance = null;
    
    private HAConfiguration configurationData;

    private ExecutorService pool = 
        Executors.newSingleThreadExecutor();

    private CdbTxId eventTxId; 
    private static Object lock = new Object();
    

    public static HAController getController()
        throws Exception {
        synchronized ( lock ) {
            if ( haControllerInstance == null ) {
                haControllerInstance = new HAController();
            }
        }
        return haControllerInstance;
    }

    private HAController () throws Exception {
        log.debug ( " xxx initializing HAController xxx ");

        this.configurationData = this.readInitData();
        this.configurationData.recognizeNodeByInterfaces();

        // start the HA Acceptor thread 
        log.info ( " starting acceptor ");
        HAControllerAcceptor.execute();
        
    }

    // Callback method will be called on certain HA events 
    //
    public void haEvent ( HaNotification haEvent ) throws Exception {

        switch ( haEvent.getHAInfoType() ) {
        case HaNotification.HA_INFO_NOMASTER:
            {
                try {
                    // master down
                    HANode node = getLocalHANode();
                    ((HALocalNode)node).saveTxId();
                    HAControllerReConnector.execute();
                    node.beMaster();

                } catch ( Exception e ) {
                    log.error("",e);
                }

            }
            break;
        case HaNotification.HA_INFO_SLAVE_DIED:
            {
                log.info(" slave died !!!" );
                HANode node = getLocalHANode ();
                CdbTxId id = node.getTxId();
                eventTxId = id;
                HAControllerReConnector.execute();
            }
            break;
        case HaNotification.HA_INFO_SLAVE_ARRIVED:
            {
                log.info ( " slave arrived ");
            }
            break;
        case HaNotification.HA_INFO_SLAVE_INITIALIZED:
            {
                log.info ( " slave initialized ");
                /// log

            }
            break;
        case HaNotification.HA_INFO_IS_MASTER:
            {
                log.info ( " is master ");
                // log
            }
            break;
        case HaNotification.HA_INFO_IS_NONE:
            {
                log.info ( " is none ");
            }
            break;
        }
    }
    // Callback method will be called when HA Node has been 
    // reconnected i.e when HAControllerReConnector has done 
    // its job
    public void reConnected ( ) throws Exception {
        log.info("reConnected() =>");
        HANode remote = getRemoteHANode();
        HANode local = getLocalHANode();
              
        if (local.txDiff() && remote.txDiff()) {
            log.info(" case 1");
            //   ERROR need manual intervention!
        }
        else if (local.txDiff() && !remote.txDiff()) {
            log.info(" case 2");
            local.beMaster();
            remote.beSlave( local );
        }
        else if (!local.txDiff() && remote.txDiff()) {
            log.info(" case 3");
            remote.beMaster();
            
            local.beNone();
            local.beSlave( remote );
        }
        else if (!local.txDiff() && !remote.txDiff()) {
            log.info(" case 4");
            preferredMaster().beMaster();
            notPreferredMaster().beSlave( preferredMaster() );
        }
        log.info("reConnected() => ok");
    }
    
    void determinationByTxId () throws Exception {
        log.info(" determinationByTxId () => ");
        HANode remote = getRemoteHANode();
        HANode local = getLocalHANode();

        if ( remote.isReachable () ) {
              
            if (local.txDiff() && remote.txDiff()) {
                //   ERROR need manual intervention!
            }
            else if (local.txDiff() && !remote.txDiff()) {
                local.beMaster();
                remote.beSlave( local );
            }
            else if (!local.txDiff() && remote.txDiff()) {
                remote.beMaster();
                local.beSlave( remote );
            }
            else if (!local.txDiff() && !remote.txDiff()) {
                preferredMaster().beMaster();
                notPreferredMaster().beSlave( preferredMaster() );
            }
        } else {
            local.beMaster();
        }
        log.info(" determinationByTxId () => ok");
    }

    public HANode preferredMaster() throws Exception {
        return ((getLocalHANode().isPreferredMaster())? getLocalHANode() :
                getRemoteHANode() );
    }

    public HANode notPreferredMaster() throws Exception {
        return (getLocalHANode().isPreferredMaster())? getRemoteHANode() :
            getLocalHANode();
    }

    public HAConfiguration readInitData() throws Exception {
        log.debug ( " readInitData() => ");
        NcsMain ncsMain = NcsMain.getInstance();
        Socket sock = new Socket (ncsMain.getNcsHost(),
                                  ncsMain.getNcsPort());

        Cdb cdb = new Cdb("HAController", sock ) ;

        CdbSession session =
            cdb.startSession(CdbDBType.CDB_RUNNING);

        ConfList confVirtualIPs =
            (ConfList)session.getElem ("/thc:ha-controller/" + 
                                       "virtual-ip-addresses/ip-address");

        List<InetAddress> virtualIPs =
            toList ( confVirtualIPs );

        ConfBuf secretToken =
            (ConfBuf)session.getElem ( "/thc:ha-controller/secret-token" );


        int n = session.getNumberOfInstances("/thc:ha-controller/ha-nodes/" +
                                             "ha-node");

        List<ConfXMLParam> inParam =
            new ArrayList<ConfXMLParam> ();


        List<HANode> haNodes =
            new ArrayList<HANode> ();
        int indx = 0;
        for  (int i = 0; i < n; i++ ) {

            inParam.add  ( new ConfXMLParamLeaf("thc", "name"));
            // inParam.add  ( new ConfXMLParamLeaf("thc", "preferred-master"));
            inParam.add  ( new ConfXMLParamLeaf("thc", "ip-address"));
            inParam.add  ( new ConfXMLParamLeaf("thc", "port"));

            ConfXMLParam[] outParam =
                session
                .getValues(inParam
                           .<ConfXMLParam>toArray(
                                                  new ConfXMLParam[0])
                           ,
                           new ConfPath("/thc:ha-controller/" +
                                        "ha-nodes/ha-node[%d]",
                                        i)) ;
            inParam.clear();

            ConfXMLParamValue valueName =
                (ConfXMLParamValue)outParam[indx++];

            ConfXMLParamValue valueIPAddress =
                (ConfXMLParamValue)outParam[indx++];

            ConfXMLParamValue valuePort = 
                (ConfXMLParamValue)outParam[indx++];

            ConfIP ipAddress = (ConfIP)valueIPAddress.getValue();

            boolean preferredMaster = false;
            ConfPath prefMasterPath = 
                new ConfPath ("/thc:ha-controller/ha-nodes/ha-node[" + i + 
                              "]/preferred-master");
                
            log.info ( " path :" + prefMasterPath );
            if ( session.exists(prefMasterPath ) ) {
                    preferredMaster = true;
                }
            int port = (int)((ConfUInt16)valuePort.getValue()).longValue();

            haNodes.add ( new HAUndefNode ( valueName.getValue().toString(),
                                            ipAddress ,
                                            preferredMaster,
                                            port));
            indx = 0;
        }

        session.endSession();
        sock.close();
        log.debug ( " readInitData() => ok");
        return new HAConfiguration ( haNodes , secretToken.toString(),
                                     virtualIPs );
    }

    private List<InetAddress> toList( ConfList list ) {
        log.info ( " list :" + list );
        List<InetAddress> addresses =
            new ArrayList<InetAddress>();

        for ( ConfObject addr : list.elements() ) {
            ConfIP ip = (ConfIP)addr;
            addresses.add ( ip.getAddress() );
        }
        return addresses;
    }



    public HANode getRemoteHANode () throws Exception {
        return this.configurationData.getRemoteHANode();
    }

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
                        Thread.currentThread().sleep(1000);
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

        }
    }
    

    void initialDetermination () throws Exception {
        log.info(" initialDetermination () =>");
        try {
            HANode remote = getRemoteHANode();
            HANode local = getLocalHANode();
            if ( remote.isReachable() ) {
                   
                if (  local.txDiff() || remote.txDiff() ) {
                    determinationByTxId ();
                } else {
                    switch ( remote.getHaStatus() ) {
                    case NONE: 
                        {
                            log.info(" NONE ");
                            if ( getLocalHANode().isPreferredMaster() ) {
                                getLocalHANode().beMaster();
                                
                            } else {
                                try {
                                    Thread.currentThread().sleep(1000);
                                } catch ( InterruptedException e ) {
                                    log.error("",e);
                                }
                                initialDetermination ();
                            }
                            break;
                        }

                    case MASTER:
                        {
                            log.info(" remote was MASTER ");
                            getLocalHANode().beSlave( getRemoteHANode() );
                        }
                        break;
                    case SLAVE:
                        {

                        }
                        break;
                    }
                }
            } else {
                log.info (" The remote node " +  remote + 
                          " is not reachable !" );
                log.info( " saving txid ");
                ((HALocalNode) getLocalHANode()).saveTxId();
                log.info(" Call " + getLocalHANode() + ". beMaster () =>");
                getLocalHANode().beMaster();
                log.info(" Call " + getLocalHANode() + ". beMaster () => ok");
                
            }
            log.info ("initialDetermination() => ok");
            return ;
        } catch ( Exception e ) {
            log.error("",e);
            // TODO
        }
    }

}
