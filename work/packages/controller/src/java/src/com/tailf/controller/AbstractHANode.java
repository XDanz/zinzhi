package com.tailf.controller;

import java.net.Socket;

import com.tailf.cdb.CdbTxId;
import com.tailf.conf.ConfIP;
import com.tailf.ha.Ha;
import com.tailf.ha.HaStateType;
import com.tailf.ncs.NcsMain;

public abstract class AbstractHANode implements HANode {
    public static final long serialVersionUID = 42L;
    
    protected String name;
    protected ConfIP address;
    protected boolean isLocal;
    protected boolean preferredMaster;
    protected int port;
    protected CdbTxId eventTxId;

    public AbstractHANode (String name,ConfIP address,boolean preferredMaster ,
                           int port ) {
        this.name = name;
        this.address = address;
        this.preferredMaster = preferredMaster;
        this.port = port;
    }


    public  ConfIP getAddress () {
        return address;
    }
    
    public  boolean isPreferredMaster() {
        return preferredMaster;
    }

    public  int getPort() {
        return port;
    }

    public boolean isReachable() {
        return false;
    }

    public boolean txDiff() throws Exception {
        throw new HAControllerDeterminationException();
    }

    public CdbTxId getTxId() throws Exception {
        throw new HAControllerDeterminationException();
    }

    public abstract boolean isLocal();

    public  String getName() {
        return this.name;
    }

    public abstract HaStateType getHaStatus() throws Exception;


    public abstract void beMaster () throws Exception;

    public abstract void beNone () throws Exception;

    
    public abstract void beSlave ( HANode master ) throws Exception;

    protected Socket getSocket2Ncs () throws Exception {
        NcsMain ncsMain = NcsMain.getInstance();
        return new Socket (ncsMain.getNcsHost(),
                           ncsMain.getNcsPort());
    }


    protected Ha getHASocket2Ncs (Socket sock) throws Exception {
        HAController ctrl = HAController.getController();
        Ha ha = new Ha (sock, ctrl.getSecretToken() );
        return ha;
    }

    protected HAControllerConnector connector() throws Exception {
        return HAControllerConnector.connector();
    }
    

}
