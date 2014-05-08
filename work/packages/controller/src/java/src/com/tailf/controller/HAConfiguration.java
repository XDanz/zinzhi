package com.tailf.controller;

import java.util.List;
import java.util.Enumeration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.InterfaceAddress;
import java.net.SocketException;

import com.tailf.conf.ConfBuf;

public class HAConfiguration {

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
            if ( haNode.isLocal() ) {
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
            if ( !haNode.isLocal() ) {
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

                for ( HANode haNode : haNodes ) {
                    if ( interfaceAddress.getAddress()
                         .equals ( haNode.getAddress() )) {
                        haNode.setLocal();
                        this.determined = true;
                    }
                }
            }
        }
    }
}