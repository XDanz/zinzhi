package com.tailf.controller;

import java.net.InetAddress;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import com.tailf.ha.Ha;
import com.tailf.ha.HaStateType;
import com.tailf.ha.HaStatus;
import com.tailf.conf.ConfIP;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfHaNode;
import com.tailf.ncs.NcsMain;
import com.tailf.cdb.CdbTxId;
import com.tailf.cdb.Cdb;

import org.apache.log4j.Logger;



public class HALocalNode extends AbstractHANode {
    private static final Logger log =
        Logger.getLogger( HALocalNode.class );

    public HALocalNode (String name, ConfIP address,boolean preferredMaster ,
                        int port  ) {
        super ( name, address, preferredMaster, port );

        try {
            ObjectInputStream is =
                new ObjectInputStream ( new FileInputStream ( "txid" +
                                                              getName()));
            this.eventTxId = (CdbTxId) is.readObject();
            log.info("READING TXID "+ eventTxId);
        } catch ( Exception e ) {
            log.info("No eventTxId found!");
        }
    }

    public  ConfIP getAddress () {
        return address;
    }

    public void setLocal () {
        this.isLocal = true;
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

    public HaStateType getHaStatus() throws Exception {
        Socket sock = null;
        try {
            sock = getSocket2Ncs();
            Ha ha = getHASocket2Ncs(sock);

            HaStatus stat = ha.status();
            return stat.getHaState();

        } finally {
            if ( sock != null ) {
                sock.close();
            }
        }
    }

    public CdbTxId getTxId() throws Exception {
        Cdb cdb = new Cdb ( "cdb-txid", getSocket2Ncs() );
        CdbTxId txid =  cdb.getTxId();
        cdb.close();
        return txid;
    }

    public CdbTxId getEventTxId() throws Exception {
        return eventTxId;
    }

    public boolean txDiff() {
        try {
            if ( this.eventTxId == null ) {
                return false;
            }
            boolean equals = false;
            CdbTxId txid = getTxId();
            if ( txid.getS1() == eventTxId.getS1() &&
                 txid.getS2() == eventTxId.getS2() &&
                 txid.getS3() == eventTxId.getS3() ) {
                equals = true;
            }
            return !equals;
        } catch ( Exception e ) {
            log.error("",e );
            return false;
        }

    }

    public void beMaster () throws Exception {
        Socket sock = getSocket2Ncs();
        Ha ha = getHASocket2Ncs(sock);
        ha.beMaster ( new ConfBuf ( getName() ));
        sock.close();
    }

    public void beNone () throws Exception {
        Socket sock = getSocket2Ncs();
        Ha ha = getHASocket2Ncs(sock);
        ha.beNone();
        sock.close();
    }

    public void saveTxId () throws Exception {
        this.eventTxId = getTxId();
        ObjectOutputStream oo =
            new ObjectOutputStream (
                                    new FileOutputStream ("txid" +
                                                          getName()));
        oo.writeObject ( eventTxId );
        oo.flush();
        oo.close();
    }

    public boolean isReachable () {
        return true;
    }

    public void beSlave ( HANode master ) throws Exception {
        Socket sock = getSocket2Ncs();
        Ha ha = getHASocket2Ncs(sock);

        ConfHaNode masterConfHaNode =
            new ConfHaNode ( new ConfBuf( master.getName()),
                             master.getAddress());


        ha.beSlave(new ConfBuf( getName() ),
                   masterConfHaNode,true) ;

        sock.close();
    }

    public String toString () {
        String str = "LocalNode[name:" + getName() + ",addr:" +
            getAddress() +
            ",port=" + getPort() +
            "]";
        return str;
    }
}
