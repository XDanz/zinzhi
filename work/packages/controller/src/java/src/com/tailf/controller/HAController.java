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

    private static HAController haControllerInstance;
    private HAConfiguration configurationData;
    private ExecutorService pool = 
        Executors.newSingleThreadExecutor();
    


    public static HAController getController()
        throws Exception {

        if ( haControllerInstance == null ) {
            haControllerInstance = new HAController();
        }


        return haControllerInstance;
    }

    private HAController () throws Exception {
        log.debug ( " xxx initializing HAController xxx ");

        this.configurationData = this.readInitData();
        this.configurationData.recognizeNodeByInterfaces();

        // start the HA Acceptor thread 
        final HANode localHaNode = this.configurationData.getLocalHANode ( );
        log.info ( " localHaNode:" + localHaNode );
        log.info ( " starting acceptor ");
        pool.execute (  new Runnable () {
                HAControllerAcceptor acceptor = null;
                public void run () {
                    try { 
                        acceptor = 
                            new HAControllerAcceptor ( localHaNode.getPort() );
                    } catch ( Exception e ) {
                        log.error("",e );
                        acceptor.executorService().shutdownNow();
                    }
                }
            }
            );


        // query the remote node
        HANode remoteHaNode = this.configurationData.getRemoteHANode();
        
        HAControllerSender sender = 
            new HAControllerSender ( remoteHaNode.getAddress().
                                     getAddress(), 
                                     remoteHaNode.getPort());

        boolean exception = false;
        try {
            sender.sendRequest("hastatus");

        } catch ( Exception e ) {
            CdbTxId txId = getCurrentTxId ();
            
            exception = true;
        }

        if ( exception ) {
            // write TxID 
            // start pinger 

        } else {
            String haResponse = (String)sender.readResponse();

            if ( haResponse.equals("master") ) {
                // split brain case

            } else if ( haResponse.equals("slave")) {

            } else if ( haResponse.equals("none")) {

                if ( localHaNode.isPreferredMaster() ) {
                    try {
                        localBeMaster() ;
                    } catch ( Exception e ) {

                    }
                } else {

                }
            }
            
        }

    }

    public void haEvent ( HaNotification haEvent ) {

        switch ( haEvent.getHAInfoType() ) {
        case HaNotification.HA_INFO_NOMASTER:
            {
                try {
                    // master down
                    localBeMaster();
                } catch ( Exception e ) {
                    log.error("",e);
                }

            }
            break;
        case HaNotification.HA_INFO_SLAVE_DIED:
            {

            }
            break;
        case HaNotification.HA_INFO_SLAVE_ARRIVED:
            {
                ///
            }
            break;
        case HaNotification.HA_INFO_SLAVE_INITIALIZED:
            {


            }
            break;
        case HaNotification.HA_INFO_IS_MASTER:
            {

            }
            break;
        case HaNotification.HA_INFO_IS_NONE:
            {

            }
            break;
        }
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

            haNodes.add ( new HANode ( valueName.getValue().toString(),
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


    public void localBeSlave () throws Exception {
        Socket sock = getSocket2Ncs();
        Ha ha = getHASocket2Ncs(sock);
        HANode remoteHaNode = configurationData.getRemoteHANode();

        ConfHaNode masterConfHaNode =
            new ConfHaNode ( new ConfBuf(remoteHaNode.getName()),
                             remoteHaNode.getAddress());

        HANode localHaNode = configurationData.getLocalHANode();
        ha.beSlave(new ConfBuf( localHaNode.getName()),
                   masterConfHaNode,true) ;

        sock.close();
    }

    public void localBeMaster () throws Exception {
        Socket sock = getSocket2Ncs();
        Ha ha = getHASocket2Ncs(sock);

        HANode localHaNode = configurationData.getLocalHANode();

        ha.beMaster( new ConfBuf ( localHaNode.getName()));
        sock.close();


    }

    public void localBeNone () {

    }

    private Socket getSocket2Ncs () throws Exception {
        NcsMain ncsMain = NcsMain.getInstance();
        return new Socket (ncsMain.getNcsHost(),
                           ncsMain.getNcsPort());
    }


    private Ha getHASocket2Ncs (Socket sock) throws Exception {
        Ha ha = new Ha (sock, configurationData.getSecretToken() );

        return ha;
    }

    private CdbTxId getCurrentTxId () throws Exception {
        Cdb cdb = new Cdb ( "Get-TX", getSocket2Ncs() );
        CdbTxId txId = cdb.getTxId();
        return txId;
    }


    void startHAControllerAcceptor () {
    }

    void queryAndCommand () {
      
        

    }

}
