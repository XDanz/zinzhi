package com.tailf.controller;

import java.net.InetAddress;

import com.tailf.conf.ConfIP;

public class HANode {
    private String name;
    private ConfIP address;
    private boolean isLocal;
    private boolean preferredMaster;
    private int port;

    public HANode (String name,ConfIP address,boolean preferredMaster ,
                   int port  ) {
        this.name = name;
        this.address = address;
        this.preferredMaster = preferredMaster;
        this.port = port;
    }

    public  ConfIP getAddress () {
        return address;
    }

    public void setLocal () {
        this.isLocal = isLocal;
    }

    public boolean isPreferredMaster() {
        return this.preferredMaster;
    }
    public int getPort() {
        return this.port;
    }

    public boolean isLocal() {
        return this.isLocal;
    }

    public String getName() {
        return this.name;
    }
}