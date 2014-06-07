package com.tailf.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbSession;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfIP;
import com.tailf.conf.ConfList;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfValue;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamLeaf;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.ncs.NcsMain;

/**
 * Instance of this Class will try to read configuration
 * from tailf-ha-controller.yang. This class also contains
 * method for determine the Local/Remote HA node. Before
 * running recognizeNodeByInterfaces() the 2 HANodes is unknown
 * for usage. If the call succeded the 2 HANode's is determined
 *
 **/
public class HAConfiguration {
    private static final Logger log =
        Logger.getLogger ( HAConfiguration.class);

    // Contains configuread HANode's if the
    // recognizeNodeByInterfaces()
    // method has been invoked successfully otherwise it
    // contians unconfigured nodes.
    private List<HANode> haNodes;

    // The secret token used for connecting HA Socket.
    private String secretToken;

    // List of configured VIP Addresses
    private List<InetAddress> virtualIPAddresses;

    // Flag indicates if the HANodes has been configured
    private boolean determined = false;

    // The current path for the running NCS Package
    private File currPkgDir;

    // Some convenience fields
    private static final String VIP_PATH =
        "/thc:ha-controller/virtual-ip-addresses/ip-address";

    private static final String TOKEN_PATH =
        "/thc:ha-controller/secret-token";

    private static final String HA_NODE_PATH =
        "/thc:ha-controller/ha-nodes/ha-node";

    private static final String HA_NODE_PATH_VAR =
        "/thc:ha-controller/ha-nodes/ha-node[%d]";

    private static final String PREF_MASTER_PATH_VAR =
        "/thc:ha-controller/ha-nodes/ha-node[%d]/preferred-master";

    // holds locks on lock while reading configuration by
    // HAController initialization procedure
    public HAConfiguration() throws Exception {
        log.debug ( " readInitData() => ");
        Socket sock = Sock2Ncs.get();
        Cdb cdb = new Cdb("HAController", sock);

        CdbSession session =
            cdb.startSession(CdbDBType.CDB_RUNNING);

        // reads the list of virtual addresses
        this.virtualIPAddresses =
            toList ((ConfList)session.getElem (VIP_PATH));
        if ( this.virtualIPAddresses.size() > 0 ) {
            log.info(" found VIP's " + this.virtualIPAddresses );
        }
        
        // reads the secret token
        ConfBuf secretToken =
            (ConfBuf)session.getElem (TOKEN_PATH);

        if ( secretToken == null )
            throw new HAControllerException( "Secret Token is mandatory!");


        this.secretToken = secretToken.toString();

        this.haNodes = readHANodes ( session );

        session.endSession();
        sock.close();

        this.currPkgDir = readPakageDirectory ();
        if ( this.currPkgDir != null )
            log.info(" using package directory " + this.currPkgDir );

        recognizeNodeByInterfaces();
        log.debug ( " readInitData() => ok");
    }


    List<InetAddress> toList( ConfList list ) {
        List<InetAddress> addresses =
            new ArrayList<InetAddress>();
        // can occure when no VIPS is configured
        if ( list != null ) {
            for ( ConfObject addr : list.elements() ) {
                ConfIP ip = (ConfIP)addr;
                addresses.add ( ip.getAddress() );
            }
        }
        return addresses;
    }


    List<HANode> readHANodes (CdbSession session ) throws Exception {
        int n = session.getNumberOfInstances(HA_NODE_PATH);


        int indx = 0;
        List<HANode> rawNodes = new  ArrayList<HANode> ();

        List<ConfXMLParam> inParam = new ArrayList<ConfXMLParam>();
        for  (int i = 0; i < n; i++ ) {
            inParam.add  ( new ConfXMLParamLeaf("thc", "name"));
            inParam.add  ( new ConfXMLParamLeaf("thc", "ip-address"));
            inParam.add  ( new ConfXMLParamLeaf("thc", "port"));

            ConfXMLParam[] outParam =
                session
                .getValues(inParam.<ConfXMLParam>toArray( new ConfXMLParam[0])
                           , new ConfPath(HA_NODE_PATH_VAR,i));

            inParam.clear();
            // read the name of the node
            ConfXMLParamValue valueName =
                (ConfXMLParamValue)outParam[indx++];

            // read the name of the IP Address
            ConfXMLParamValue valueIPAddress =
                (ConfXMLParamValue)outParam[indx++];

            // read the Listener port for this HANode
            ConfXMLParamValue valuePort =
                (ConfXMLParamValue)outParam[indx++];

            ConfIP ipAddress = (ConfIP)valueIPAddress.getValue();

            // is this node the preferred master
            boolean preferredMaster = false;

            preferredMaster =
                session.exists(new ConfPath (PREF_MASTER_PATH_VAR, i));

            int port = (int)((ConfUInt16)valuePort.getValue()).longValue();

            rawNodes.add ( new HAUndefNode ( valueName.getValue().toString(),
                                             ipAddress ,
                                             preferredMaster,
                                             port));
            indx = 0;
        }
        return rawNodes;
    }

    File readPakageDirectory () {
        Socket sock = null;
        try {
            sock  = Sock2Ncs.get();

            Maapi maapiSock =
                new Maapi (  sock ) ;
            maapiSock.startUserSession ("admin",
                                        InetAddress
                                        .getByName(NcsMain.getInstance()
                                                   .getNcsHost()),
                                        "maapi",
                                        new String[] {"admin"},
                                        MaapiUserSessionFlag.PROTO_TCP);
            ConfPath path =
                new ConfPath("/ncs:packages/package{HA}/directory" );
            int th = maapiSock.startTrans ( Conf.DB_RUNNING,
                                            Conf.MODE_READ);
            ConfValue pkgPath = maapiSock.getElem( th, path ) ;

            return new File(pkgPath.toString());
        } catch ( Exception e ) {
            log.error("",e);
            return null;
        } finally {
            try {
                if ( sock != null )
                    sock.close();
            } catch ( IOException ignore ) { };
        }
    }

    List<InetAddress> getVips ( ) {
        return this.virtualIPAddresses;
    }

    String getSecretToken ( ) {
        return secretToken;
    }

    File getPackageDirectory() {
        return this.currPkgDir;
    }

    public HANode getLocalHANode () throws HAControllerException {
        if (!determined) {
            throw new HAControllerDeterminationException ();
        }
        HANode haNode = null;

        for ( HANode node : haNodes ) {
            if ( node.isLocal() ) {
                haNode = node;
            }
        }
        return haNode;
    }

    public HANode getRemoteHANode () throws HAControllerException {
        if (!determined) {
            throw new HAControllerDeterminationException ();
        }

        HANode haNode = null;
        for ( HANode node : haNodes ) {
            if ( !node.isLocal() ) {
                haNode = node;
            }
        }
        return haNode;
    }


    /**
     *  For the current configuration the method tries to find
     *  a matching NetworkInterface on the executing host on which
     *  there exists a IP address.
     *
     */
    public void recognizeNodeByInterfaces () throws HAControllerException,
                                                    SocketException {
        log.info ( " recognizeNodeByInterfaces =>");
        Enumeration<NetworkInterface> allInterfaces =
            NetworkInterface.getNetworkInterfaces();
        // for each interface on this host
        while ( allInterfaces.hasMoreElements() ) {

            NetworkInterface networkInterface =
                allInterfaces.nextElement();

            // Get the list of network interface address
            List<InterfaceAddress> interfaceAddresses =
                networkInterface.getInterfaceAddresses();

            for ( InterfaceAddress interfaceAddress : interfaceAddresses ) {

                for ( HANode haNode : haNodes ) {
                    InetAddress haNodeAddr =
                        haNode.getAddress().getAddress();

                    InetAddress localAddress =
                        interfaceAddress.getAddress();

                    if ( inetAddressEquals (haNodeAddr, localAddress) ) {

                        log.info(" The interface " + networkInterface +
                                 " match the address " +
                                 localAddress  + " of the node " +
                                 haNode.getName());

                        if ( System.getenv("NCS_HA_NODE") == null) {

                            HALocalNode localNode =
                                setLocalHANode ( haNode );
                            this.determined = true;
                            localNode.setNetworkInterface ( networkInterface );
                            log.info ( " recognizeNodeByInterfaces => ok");
                            return;

                        } else {
                            // NCS_HA_NODE was set check that it
                            // matches name of the node
                            if ( checkEnv(haNode) ) {
                                setLocalHANode ( haNode );
                                this.determined = true;
                                log.info ( " recognizeNodeByInterfaces => ok");
                                return;
                            }
                            log.warn(" Env NCS_HA_NODE missmatch! expected " +
                                     "\"" + System.getenv("NCS_HA_NODE") +
                                     "\" was \"" + haNode.getName() + "\"" );
                                      
                        }
                    }
                }
            }
        }
        log.info ( " recognizeNodeByInterfaces => ok");
    }

    boolean checkEnv ( HANode node ) {
        if  (System.getenv("NCS_HA_NODE").equals (node.getName())) {
            return true;
        }
        return false;
    }

    /**
     *  Creates a local node from a configuread node
     *
     *
     **/
    HALocalNode  setLocalHANode ( HANode node ) {
        log.info ( " setLocalHANode() =>");
        List<HANode> list = new ArrayList<HANode>();

        HALocalNode local = new HALocalNode ( node.getName(),
                                              node.getAddress(),
                                              node.isPreferredMaster() ,
                                              node.getPort());
        local.setLocal();
        log.info(node.getName() + " is LOCAL!");
        haNodes.remove ( node );

        HANode remoteUndef = haNodes.remove(0);
        HARemoteNode remote =
            new HARemoteNode ( remoteUndef.getName(),
                               remoteUndef.getAddress(),
                               remoteUndef.isPreferredMaster(),
                               remoteUndef.getPort());
        list.add ( local );
        list.add ( remote );

        haNodes = list;
        log.info ( " setLocalHANode() => ok");
        return local;

    }
    /**
     * check the equallity of two InetAddresses
     * *
     * @param a InetAddress
     * @param b
     * @return true/false wether
     * */
    public boolean inetAddressEquals ( InetAddress a , InetAddress b ) {

        boolean isEquals = true;
        byte ba[] = a.getAddress();
        byte bb[] = b.getAddress();

        if ( ba.length == bb.length ) {
            for ( int i = 0; i <  ba.length; i++ ) {
                if ( ba[i] != bb[i] ) {
                    return false;
                }
            }
        } else {
            isEquals = false;
        }

        return isEquals;
    }
}
