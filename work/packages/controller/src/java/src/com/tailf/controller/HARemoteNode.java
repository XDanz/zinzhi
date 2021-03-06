package com.tailf.controller;


import java.net.ConnectException;

import org.apache.log4j.Logger;

import com.tailf.cdb.CdbTxId;
import com.tailf.conf.ConfIP;
import com.tailf.ha.HaStateType;

public class HARemoteNode extends AbstractHANode {
    private static final Logger log = 
        Logger.getLogger ( HARemoteNode.class );
    public HARemoteNode (String name,ConfIP address,boolean preferredMaster ,
                         int port  ) {
        super( name, address, preferredMaster, port );
    }

    public ConfIP getAddress () {
        return address;
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
    
    public void saveTxId () throws Exception {
        HAControllerConnector con = null;
        try {
            con = connector();
            con.send ("savetxid");
            
        } finally {
            if ( con != null ) 
                con.close ();
        }
    }

    public boolean txDiff () throws Exception {
        HAControllerConnector con = null;
        try { 
            con = connector();
            con.send("eventtxid");
            CdbTxId  eventTxId = (CdbTxId)con.recv();
            if ( eventTxId == null ) {
                return false;
            }
            return (eventTxId.equals (getTxId()))? false : true;
        } finally {
            if ( con != null ) {
                con.close();
            }
        }
    }

    public HaStateType getHaStatus() throws Exception {
        HAControllerConnector con = null;
        try {
            con = connector();
            con.send("status");
            
            String state = (String)con.recv();
        
            if ( state.equals("master")) {
                return HaStateType.MASTER;
            } else if ( state.equals ("slave")) {
                return HaStateType.SLAVE;
            } else if ( state.equals ("none")) {
                return HaStateType.NONE;
            } else {
                return HaStateType.SLAVE_RELAY;
            }
        } finally {
            if ( con != null) {
                con.close();
            }
        }
    }

    public void beMaster () throws Exception {
        String reply = be ("master");
    }

    public void beNone () throws Exception {
        String reply = be ( "none");
    }

    public CdbTxId getTxId ( ) throws Exception {
        HAControllerConnector con = null;
        try { 
            con = connector();
            con.send("txid");
            CdbTxId txid = (CdbTxId)con.recv();
            return  txid;
        } finally {
            if ( con != null ) {
                con.close();
            }
        }
    }

    /**
     * Tries to connect to the remote node ip and listening port
     * and sends a ping request. 
     *
     * @returns true wether the remote node response with the
     * string "pong" which indicates that the remote HAController
     * listener is accepting connection.
     */
    public boolean isReachable ( )  {
        HAControllerConnector con = null;
        boolean reachable = false;
        try { 
            con = connector();
            con.send("ping");
            String repl = (String)con.recv();

            if ( repl != null && repl.equals("pong") ) {
                reachable = true;
            }
        } catch ( ConnectException e) {
            // The remote node is down this is common case
            // i.e the remote node has not been started yet
            // log.warn ( "Remote node not available!");
        } catch ( Exception e ) {
            log.warn (e);
        } finally {
            try {           
            if ( con != null ) {
                con.close();
            }
            } catch ( Exception ee ) { }
                
        }
        return reachable;
    }
    
    public void beSlave ( HANode master ) throws Exception {
        String reply  =  be ("slave");
    }

    String be ( String cmd ) throws Exception {
        
        HAControllerConnector con =  null;
        try {
            con = connector();
            con.send(cmd);

            String rec = (String)con.recv();
            return rec;
        } finally {
            con.close();
        }
    }
    
    public String toString () {
        String str = "RemoteNode[name:" + getName() + ",addr:" + 
            getAddress() + 
            ",port=" + getPort() + 
            "]";
        return str;
    }
}
