package com.tailf.test.dp.transform;
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
import java.util.Arrays;

import com.tailf.conf.*;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiCursor;
import com.tailf.maapi.MaapiException;
import com.tailf.test.dp.transform.namespaces.*;

import org.apache.log4j.Logger;


public class SimpleDataCb {

    private static Logger LOGGER =
        Logger.getLogger(SimpleDataCb.class);

    SimpleDataCb(Maapi m) {
        this.maapi = m;
    }

    public Maapi maapi;

    public String callpoint() {
        return "simplecp";
    }

    private static class MaapiIterator implements Iterator<Object> {
        private MaapiCursor c;
        private ConfKey next = null;
        private Maapi m;

        MaapiIterator(MaapiCursor cu, Maapi ma) {
            c = cu;
            m = ma;
        }

        public boolean hasNext() {
            if (next != null) return true;
            try {
                next = m.getNext(c);
                if (next != null) return true;
            } catch (Exception e) {
                LOGGER.error("",e);
            }
            return false;
        }

        public Object next() {
            if (next != null) {
                Object r = next; next = null;
                return r;
            }
            try {
                return m.getNext(c);
            } catch (Exception e) {
                LOGGER.error("",e);
                return null;
            }
        }
        public void remove() {}
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.ITERATOR)
    public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        try {
            MaapiCursor c = maapi.newCursor(trans.getTransaction(),
                                            "/servers_cdb/server");
            return new MaapiIterator(c, maapi);
        }
        catch (Exception e) {
            return null;
        }
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT)
    public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj)
        throws DpCallbackException {
        LOGGER.info("getKey()");
        return (ConfKey)obj;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
    public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("getElem()");
        String name = ((ConfKey) kp[1]).elementAt(0).toString();

        kp[3] = new ConfTag(new smp(), "servers_cdb");

        try {
            LOGGER.info(Arrays.toString(kp));
            ConfPath path = new ConfPath(kp);
            ConfValue v =  maapi.getElem(trans.getTransaction(), path);
            return (v);
        }
        catch (MaapiException m) {
            if (m.getErrorCode() == ErrorCode.ERR_NOEXISTS) {
                return null;
            }
            else {
                throw new DpCallbackException(m.toString());
            }
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        LOGGER.info("setElem()");
        kp[3] = new ConfTag(new smp(), "servers_cdb");
        ConfPath path = new ConfPath(kp);
        try {
            maapi.setElem(trans.getTransaction(), newval, path);
            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
    public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("create()");
        kp[2] = new ConfTag(new smp(), "servers_cdb");

        LOGGER.info("kp:" + Arrays.toString(kp));

        ConfPath path = new ConfPath(kp);
        try {
            maapi.create(trans.getTransaction(), path);
            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.REMOVE)
    public int remove(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("remove()");
        kp[2] = new ConfTag(new smp(), "servers_cdb");

        LOGGER.info("kp:" + Arrays.toString(kp));
        ConfPath path = new ConfPath(kp);
        try {
            maapi.create(trans.getTransaction(), path);

            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.EXISTS_OPTIONAL)
    public boolean existsOptional(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("existsOptional()");
        return false;
    }

}
