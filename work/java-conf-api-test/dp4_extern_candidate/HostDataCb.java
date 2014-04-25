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

public class HostDataCb {


    @DataCallback(callPoint="hcp", callType=DataCBType.ITERATOR)
    public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        return DbServer.iterator(trans.getDBName());
    }


    @DataCallback(callPoint="hcp", callType=DataCBType.GET_NEXT)
    public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj) throws DpCallbackException {
        trace("getKey()");
        Host s = (Host) obj;
        return new ConfKey( new ConfObject[] { new ConfBuf(s.name) });
    }



    @DataCallback(callPoint="hcp", callType=DataCBType.GET_ELEM)
    public ConfValue getElem(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("getElem()");
        String name = ((ConfKey) kp[1]).elementAt(0).toString();
        Host s = DbServer.findHost( trans.getDBName(),  name );
        if (s == null) return null; /* not found */

        /* switch on xml elem tag */
        ConfTag leaf = (ConfTag) kp[0];
        switch (leaf.getTagHash()) {
        case hst.hst_name:
            return new ConfBuf( s.name );
        case hst.hst_domain:
            return new ConfBuf( s.domain );
        case hst.hst_defgw:
            return new ConfIPv4( s.defgw );
        default:
            throw new DpCallbackException("xml tag not handled");
        }
    }



    @DataCallback(callPoint="hcp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval) throws DpCallbackException {
        trace("setElem()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="hcp", callType=DataCBType.CREATE)
    public int create(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("create()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="hcp", callType=DataCBType.REMOVE)
    public int remove(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("remove()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="hcp", callType=DataCBType.NUM_INSTANCES)
    public int numInstances(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("numInstances()");
        return DbServer.numInstances(trans.getDBName());
    }


    @DataCallback(callPoint="hcp", callType=DataCBType.GET_OBJECT)
    public ConfValue[] getObject(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("getObject()");
        trace(kp);
        String name = ((ConfKey) kp[0]).elementAt(0).toString();
        Host s = DbServer.findHost( trans.getDBName(), name );
        if (s == null) return null; /* not found */
        return getObject(trans,kp,s);
    }

    @DataCallback(callPoint="hcp", callType=DataCBType.GET_NEXT_OBJECT)
    public ConfValue[] getObject(DpTrans trans, ConfObject[] kp, Object obj) throws DpCallbackException {
        Host s = (Host) obj;
        return new ConfValue[] {
            new ConfBuf(s.name),
            new ConfBuf( s.domain ),
            new ConfIPv4( s.defgw ),
            null};
    }


    @DataCallback(callPoint="hcp", callType=DataCBType.EXISTS_OPTIONAL)
    public boolean existsOptional(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        trace("existsOptional()");
        return false;
    }


    /**
     * trace
     */
    public void trace(String str) {
        System.err.println("*HostDataCb: "+str);
    }

    private void trace(ConfObject[] kp) {
        String s= "";
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        trace("kp = "+s);
    }

}