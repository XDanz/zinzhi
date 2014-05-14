package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.InterfaceAddress;
import java.net.SocketException;

import com.tailf.conf.ConfBuf;
import org.apache.log4j.Logger;

public class HAConfiguration {
    private static final Logger log = 
        Logger.getLogger ( Logger.class);
    private List<HANode> haNodes;
    private String secretToken;
    private List<InetAddress> virutalIPAddresses;
    private boolean determined = false;


    public HAConfiguration ( List<HANode> haNodes, String secretToken,
                             List<InetAddress> virtualIPAddresses ) {

        this.haNodes = haNodes;
        this.secretToken = secretToken;
        this.virutalIPAddresses = virtualIPAddresses;
    }

    String getSecretToken ( ) {
        return secretToken;
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



    public void recognizeNodeByInterfaces () throws HAControllerException,
                                                    SocketException {
        log.info ( " recognizeNodeByInterfaces =>");
        Enumeration<NetworkInterface> allInterfaces =
            NetworkInterface.getNetworkInterfaces();

        while ( allInterfaces.hasMoreElements() ) {

            NetworkInterface networkInterface =
                allInterfaces.nextElement();

            List<InterfaceAddress> interfaceAddresses =
                networkInterface.getInterfaceAddresses();

            for ( InterfaceAddress interfaceAddress : interfaceAddresses ) {

                for ( HANode haNode : haNodes ) {
                    InetAddress haNodeAddr = 
                        haNode.getAddress().getAddress();

                    InetAddress localAddress = 
                        interfaceAddress.getAddress();
                    
                    if ( inetAddressEquals (haNodeAddr, localAddress) ) {
                     
                        if ( System.getenv("NCS_HA_NODE") == null) {

                            setLocalHANode ( haNode );
                            this.determined = true;
                            log.info ( " recognizeNodeByInterfaces => ok");
                            return;

                        } else {

                            if ( checkEnv(haNode) ) {
                                setLocalHANode ( haNode );
                                this.determined = true;
                                log.info ( " recognizeNodeByInterfaces => ok");
                                return;
                            }
                        }
                         log.info (" not equal ");
                    }
                }
            }
        }
        log.info ( " recognizeNodeByInterfaces => ok");
    }
    
    boolean checkEnv ( HANode node ) {
        if  (System.getenv("NCS_HA_NODE") != null
             && System.getenv("NCS_HA_NODE")
             .equals (node.getName())) {
            return true;
        }
        return false;
    }
    
    void setLocalHANode ( HANode node ) {
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
    }

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
