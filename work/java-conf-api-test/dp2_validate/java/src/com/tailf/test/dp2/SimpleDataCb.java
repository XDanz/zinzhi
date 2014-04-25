package com.tailf.test.dp2;
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
import org.apache.log4j.Logger;
import com.tailf.test.dp2.namespaces.smp;

import java.util.Iterator;

public class SimpleDataCb {

    private static Logger LOGGER = Logger.getLogger(SimpleDataCb.class);

    @DataCallback(callPoint="simplecp", callType=DataCBType.ITERATOR)
        public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        return SimpleWithTrans.iterator();
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT)
    public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj)
        throws DpCallbackException {
        LOGGER.info("getKey()");
        Server s = (Server) obj;
        return new ConfKey( new ConfObject[] { new ConfBuf(s.name) });
    }



    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
    public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("getElem()");
        String name = ((ConfKey) kp[1]).elementAt(0).toString();
        Server s = SimpleWithTrans.findServer( name );
        if (s == null) return null; /* not found */

        /* switch on xml elem tag */
        ConfTag leaf = (ConfTag) kp[0];
        switch (leaf.getTagHash()) {
        case smp._name:
            return new ConfBuf( s.name );
        case smp._ip:
            return new ConfIPv4( s.addr );
        case smp._port:
            return new ConfUInt16( s.port );
        default:
            throw new DpCallbackException("xml tag not handled");
        }
    }



    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        LOGGER.info("setElem()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
        public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("create()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.REMOVE)
        public int remove(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        LOGGER.info("remove()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.NUM_INSTANCES)
        public int numInstances(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        LOGGER.info("numInstances()");
        return SimpleWithTrans.numServers();
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_OBJECT)
        public ConfValue[] getObject(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        LOGGER.info("getObject()");
        LOGGER.info(kp);
        String name = ((ConfKey) kp[0]).elementAt(0).toString();
        Server s = SimpleWithTrans.findServer( name );
        if (s == null) return null; /* not found */
        return getObject(trans,kp,s);
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT_OBJECT)
        public ConfValue[] getObject(DpTrans trans, ConfObject[] kp, Object obj) throws DpCallbackException {
        Server s = (Server) obj;
        return new ConfValue[] {
            new ConfBuf(s.name),
            new ConfIPv4( s.addr),
            new ConfUInt16( s.port)};
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.EXISTS_OPTIONAL)
        public boolean existsOptional(DpTrans trans, ConfObject[] kp) throws DpCallbackException {
        LOGGER.info("existsOptional()");
        return false;
    }




}