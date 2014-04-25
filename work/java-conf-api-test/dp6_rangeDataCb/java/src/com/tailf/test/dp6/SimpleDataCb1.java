package com.tailf.test.dp6;

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

import com.tailf.conf.Conf;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfHexList;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfOID;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfOctetList;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfUInt32;
import com.tailf.conf.ConfValue;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import org.apache.log4j.Logger;
import com.tailf.test.dp6.namespaces.*;
import org.apache.log4j.Logger;

public class SimpleDataCb1 {


    public static final int type = 1;
    private static Logger LOGGER =
        Logger.getLogger(SimpleDataCb1.class);

    @DataCallback(callPoint="simplecp", callType=DataCBType.ITERATOR)
        public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        return SimpleWithTrans1.iterator();
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT)
        public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj)
        throws DpCallbackException {
        LOGGER.debug("getKey()");
        Server s = (Server) obj;
        return new ConfKey( new ConfObject[] { new ConfBuf(s.name) });
    }



    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
        public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("getElem()");
        String name = ((ConfKey) kp[1]).elementAt(0).toString();
        Server s = SimpleWithTrans1.findServer( name );
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
        case smp._macaddr:
            return new ConfHexList( s.macaddr );
        case smp._snmpref:
            return new ConfOID( s.snmpref );
        case smp._type:
            return new ConfUInt32( type );
        case smp._prefixmask:
            return new ConfOctetList( s.prefixmask );
        default:
            throw new DpCallbackException("xml tag not handled");
        }
    }



    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
        public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        LOGGER.debug("setElem()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
        public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("create()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.REMOVE)
        public int remove(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("remove()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.NUM_INSTANCES)
        public int numInstances(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("numInstances()");
        return SimpleWithTrans1.numServers();
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_OBJECT)
        public ConfValue[] getObject(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("getObject()");
        LOGGER.debug(kp);
        String name = ((ConfKey) kp[0]).elementAt(0).toString();
        Server s = SimpleWithTrans1.findServer( name );
        if (s == null) return null; /* not found */
        return getObject(trans,kp,s);
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT_OBJECT)
        public ConfValue[] getObject(DpTrans trans, ConfObject[] kp, Object obj)
        throws DpCallbackException {
        Server s = (Server) obj;
        return new ConfValue[] {
            new ConfBuf(s.name),
            new ConfIPv4( s.addr),
            new ConfUInt16( s.port),
            new ConfHexList( s.macaddr ),
            new ConfOID( s.snmpref ),
            new ConfOctetList( s.prefixmask ),
            new ConfUInt32( type )
        };
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.EXISTS_OPTIONAL)
        public boolean existsOptional(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("existsOptional()");
        return false;
    }

}