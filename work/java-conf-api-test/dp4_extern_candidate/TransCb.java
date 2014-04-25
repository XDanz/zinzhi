/*    -*- Java -*-
 *
 *  Copyright 2007 Tail-F Systems AB. All rights reserved.
 *
 *  This software is the confidential and proprietary
 *  information of Tail-F Systems AB.
 *
 *  $Id$
 *
 */

import java.util.Iterator;

import com.tailf.conf.ConfBool;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfTag;
import com.tailf.dp.DpAccumulate;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.DpTransCallback;
import com.tailf.dp.annotations.TransCallback;
import com.tailf.dp.proto.TransCBType;

public class TransCb {


        @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        trace("init(): userinfo= "+trans.getUserInfo());
    }

        @TransCallback(callType=TransCBType.TRANS_LOCK)
    public void transLock(DpTrans trans) throws DpCallbackException {
        trace("transLock()");
        DbServer.lock(trans.getDBName());
    }

        @TransCallback(callType=TransCBType.TRANS_UNLOCK)
    public void transUnlock(DpTrans trans) throws DpCallbackException {
        trace("transUnlock()");
        DbServer.unlock(trans.getDBName());
    }

        @TransCallback(callType=TransCBType.WRITE_START)
    public void writeStart(DpTrans trans) throws DpCallbackException {
        trace("writeStart()");
    }

        @TransCallback(callType=TransCBType.PREPARE)
    public void prepare(DpTrans trans) throws DpCallbackException {
        trace("prepare()");
    }

        @TransCallback(callType=TransCBType.ABORT)
    public void abort(DpTrans trans) throws DpCallbackException {
        trace("abort()");
    }


        @TransCallback(callType=TransCBType.COMMIT)
    public void commit(DpTrans trans) throws DpCallbackException {
        trace("commit()");
        String name;
        String ifname;
        String hostname;
        int DBNAME = trans.getDBName();
        for (Iterator<DpAccumulate> it = trans.accumulated(); it.hasNext(); ) {
            DpAccumulate acc=it.next();
            // check callpoint
            trace("Acc= "+acc);
            if (acc.getCallPoint().equals("hcp")) {
                // Hosts
                switch (acc.getOperation()) {
                case DpAccumulate.SET_ELEM:
                    /* we're setting the elem of an already existing */
                    /* host entry */
                    /* keypath example: /hosts/host{hname}/defgw */
                    trace("SET_ELEM:");
                    trace(acc.getKP());
                    name = ((ConfKey) acc.getKP()[1]).elementAt(0).toString();
                    Host s = DbServer.findHost( DBNAME, name );
                    if (s==null) break;
                    // check leaf tag
                    ConfTag leaf = (ConfTag) acc.getKP()[0];
                    switch (leaf.getTagHash()) {
                    case hst.hst_name:
                        s.name = acc.getValue().toString();
                        break;
                    case hst.hst_domain:
                        s.domain = acc.getValue().toString();
                        break;
                    case hst.hst_defgw:
                        ConfIPv4 ip = (ConfIPv4) acc.getValue();
                        s.defgw = ip.getRawAddress();
                        break;
                    }
                    break;
                case DpAccumulate.CREATE:
                    /* we're creating a brand new host entry */
                    /* it will soon be populated with values */
                    /* keypath example: /hosts/host{hname}   */
                    trace("CREATE:");
                    name = ((ConfKey) acc.getKP()[0]).elementAt(0).toString();
                    s = new Host(name);
                    /* add to current db */
                    DbServer.addHost(DBNAME, s);
                    break;
                case DpAccumulate.REMOVE:
                    trace("REMOVE:");
                    trace(acc.getKP());
                    name = ((ConfKey) acc.getKP()[0]).elementAt(0).toString();
                    DbServer.removeHost(DBNAME, name);
                    break;
                }
            } else if (acc.getCallPoint().equals("icp")) {
                // Interfaces
                switch (acc.getOperation()) {
                case DpAccumulate.SET_ELEM:
                    /* we're setting an item in an already existing interface*/
                    /* keypath ex:  */
                    /* /hosts/host{hname}/interfaces/interface{eth0}/ip */
                    trace("SET_ELEM:");
                    trace(acc.getKP());
                    ifname = ((ConfKey) acc.getKP()[1]).elementAt(0).toString();
                    hostname = ((ConfKey) acc.getKP()[4]).elementAt(0).toString();
                    Iface s = DbServer.findIface( DBNAME, hostname, ifname );
                    if (s==null) break;
                    // check leaf tag
                    ConfTag leaf = (ConfTag) acc.getKP()[0];
                    switch (leaf.getTagHash()) {
                    case hst.hst_name:
                        s.name = acc.getValue().toString();
                        break;
                    case hst.hst_ip:
                        ConfIPv4 ip = (ConfIPv4) acc.getValue();
                        s.addr = ip.getRawAddress();
                        break;
                    case hst.hst_mask:
                        ip = (ConfIPv4) acc.getValue();
                        s.mask = ip.getRawAddress();
                        break;
                    case hst.hst_enabled:
                        ConfBool enabled = (ConfBool) acc.getValue();
                        s.enabled = enabled.booleanValue();
                        break;
                    }
                    break;
                case DpAccumulate.CREATE:
                    /* we're creating a brand new new interface */
                    /* keypath example is */
                    /* /hosts/host{hname}/interfaces/interface{eth0} */
                    trace("CREATE:");
                    trace(acc.getKP());
                    hostname=  ((ConfKey) acc.getKP()[3]).elementAt(0).toString();
                    Host h= DbServer.findHost(DBNAME, hostname);
                    ifname = ((ConfKey) acc.getKP()[0]).elementAt(0).toString();
                    h.ifaces.add( new Iface(ifname) );
                    break;
                case DpAccumulate.REMOVE:
                    /* we're deleting an interface */
                    /* keypath example */
                    /* /hosts/host{hname}/interfaces/interface{eth0} */
                    trace("REMOVE:");
                    trace(acc.getKP());
                    ifname = ((ConfKey) acc.getKP()[0]).elementAt(0).toString();
                    hostname = ((ConfKey) acc.getKP()[3]).elementAt(0).toString();
                    DbServer.removeIface( DBNAME, hostname, ifname );
                    break;
                }

            }
        }
    }


        @TransCallback(callType=TransCBType.FINISH)
    public void finish(DpTrans trans) throws DpCallbackException {
        trace("finish()");
    }


    public void trace(String str) {
        System.err.println("*TransCb: "+str);
    }

    private void trace(ConfObject[] kp) {
        String s= "";
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        trace("kp = "+s);
    }

}
