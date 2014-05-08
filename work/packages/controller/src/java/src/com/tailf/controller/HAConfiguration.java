package com.tailf.controller;

import java.util.List;
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

        Enumeration<NetworkInterface> allInterfaces =
            NetworkInterface.getNetworkInterfaces();

        while ( allInterfaces.hasMoreElements() ) {

            NetworkInterface networkInterface =
                allInterfaces.nextElement();

            List<InterfaceAddress> interfaceAddresses =
                networkInterface.getInterfaceAddresses();

            for ( InterfaceAddress interfaceAddress : interfaceAddresses ) {
                log.info( " interfaceAddresses:" + 
                          interfaceAddress.getAddress() );

                for ( HANode haNode : haNodes ) {
                    InetAddress haNodeAddr = 
                        haNode.getAddress().getAddress();
                    log.info  ( "haNodeAddr:" + haNodeAddr );

                    InetAddress localAddress = 
                        interfaceAddress.getAddress();
                    log.info  ( "localAddress:" + localAddress );
                    
                    if ( inetAddressEquals (haNodeAddr, localAddress) ) {
                        haNode.setLocal();
                        this.determined = true;
                        return;
                    }
                    log.info (" not equal ");
                    
                }
            }
        }
    }

    public boolean inetAddressEquals ( InetAddress a , InetAddress b ) {
        
        boolean isEquals = true;
        byte ba[] = a.getAddress();
        byte bb[] = b.getAddress();
        log.info ( "ba:" + java.util.Arrays.toString ( ba ) ) ;
        log.info ( "bb:" + java.util.Arrays.toString ( bb ) ) ;
        
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
