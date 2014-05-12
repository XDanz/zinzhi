package com.tailf.controller;

import java.net.InetAddress;
import java.net.Socket;

import com.tailf.conf.ConfIP;
import com.tailf.conf.ConfHaNode;
import com.tailf.ha.HaStateType;
import com.tailf.cdb.CdbTxId;

public interface  HANode {

    public ConfIP getAddress ();

    public boolean isPreferredMaster();
    
    public int getPort();

    public boolean isLocal();

    public String getName();

    public HaStateType getHaStatus() throws Exception;

    public void beMaster () throws Exception;

    public void beNone () throws Exception;
    
    public void beSlave ( HANode master ) throws Exception ;

    public CdbTxId getTxId() throws Exception;

    public boolean txDiff () throws Exception ;

    public boolean isReachable();

}
