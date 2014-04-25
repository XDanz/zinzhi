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

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;

import java.util.Iterator;

public class IfaceDataCb {

    @DataCallback(callPoint="icp", callType=DataCBType.ITERATOR)
    public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("iterator()");
        trace(kp);
        /* keypath example:
         * /hosts/host{hname}/interfaces/interface */
        String name = ((ConfKey) kp[2]).elementAt(0).toString();
        Host s = DbServer.findHost( trans.getDBName(),  name );
        if (s == null) return null; /* not found */
        return s.ifaces.iterator();
    }


    @DataCallback(callPoint="icp", callType=DataCBType.GET_NEXT)
    public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj) throws DpCallbackException {
        trace("getKey()");
        Iface s = (Iface) obj;
        return new ConfKey( new ConfObject[] { new ConfBuf(s.name) });
    }



    @DataCallback(callPoint="icp", callType=DataCBType.GET_ELEM)
    public ConfValue getElem(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("getElem()");
        /* keypath example:
         * /hosts/host{hname}/interfaces/interface{eth0}/ip */
        String ifname = ((ConfKey) kp[1]).elementAt(0).toString();
        String hostname = ((ConfKey) kp[4]).elementAt(0).toString();
        Iface s = DbServer.findIface( trans.getDBName(), hostname, ifname );
        if (s == null) return null; /* not found */

        /* switch on xml elem tag */
        ConfTag leaf = (ConfTag) kp[0];
        switch (leaf.getTagHash()) {
        case hst.hst_name:
            return new ConfBuf( s.name );
        case hst.hst_ip:
            return new ConfIPv4( s.addr );
        case hst.hst_mask:
            return new ConfIPv4( s.mask );
        case hst.hst_enabled:
            return new ConfBool( s.enabled );
        default:
            throw new DpCallbackException("xml tag not handled");
        }
    }


    @DataCallback(callPoint="icp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval) throws DpCallbackException {
        trace("setElem()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="icp", callType=DataCBType.CREATE)
    public int create(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("create()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="icp", callType=DataCBType.REMOVE)
    public int remove(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("remove()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="icp", callType=DataCBType.NUM_INSTANCES)
    public int numInstances(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("numInstances()");
        String name = ((ConfKey) kp[2]).elementAt(0).toString();
        Host s = DbServer.findHost( trans.getDBName(),  name );
        if (s == null) return 0; /* not found */
        return s.ifaces.size();
    }


    @DataCallback(callPoint="icp", callType=DataCBType.GET_OBJECT)
    public ConfValue[] getObject(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("getObject()");
        trace(kp);
        String ifname = ((ConfKey) kp[0]).elementAt(0).toString();
        String hostname = ((ConfKey) kp[3]).elementAt(0).toString();
        Iface s = DbServer.findIface( trans.getDBName(), hostname, ifname );
        if (s == null) return null; /* not found */
        return getObject(trans,kp,s);
    }


    @DataCallback(callPoint="icp", callType=DataCBType.GET_NEXT_OBJECT)
    public ConfValue[] getObject(DpTrans trans, ConfObject[] kp, Object obj) throws DpCallbackException {
        Iface s = (Iface) obj;
        return new ConfValue[] {
            new ConfBuf(s.name),
            new ConfIPv4( s.addr ),
            new ConfIPv4( s.mask ),
            new ConfBool( s.enabled) };
    }


    @DataCallback(callPoint="icp", callType=DataCBType.EXISTS_OPTIONAL)
    public boolean existsOptional(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("existsOptional()");
        return false;
    }


    /**
     * trace
     */
    public void trace(String str) {
        System.err.println("*IfaceDataCb: "+str);
    }

    private void trace(ConfObject[] kp) {
        String s= "";
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        trace("kp = "+s);
    }

}