package com.tailf.controller;

import com.tailf.conf.ConfIP;
import com.tailf.ha.HaStateType;

public class HAUndefNode extends AbstractHANode {

    public HAUndefNode (String name,ConfIP address,boolean preferredMaster ,
                        int port  ) {
        super( name, address, preferredMaster, port );
    }

    public  HaStateType getHaStatus() throws Exception {
        throw new  HAControllerDeterminationException();
    }


    public  void beMaster () throws Exception {
        throw new  HAControllerDeterminationException();
    }

    public  void beNone () throws Exception {
        throw new  HAControllerDeterminationException();
    }

     public  void beSlave ( HANode master ) throws Exception {
        throw new  HAControllerDeterminationException();
    }

    public void saveTxId() throws Exception {
        throw new HAControllerDeterminationException ();
    }

    public boolean isLocal () {
        return false;
    }

}
