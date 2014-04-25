package com.tailf.test.dphook1;
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

import com.tailf.conf.Conf;
import com.tailf.conf.ConfInt32;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfValue;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import com.tailf.maapi.Maapi;
import java.util.Arrays;
import com.tailf.test.dphook1.namespaces.*;

import org.apache.log4j.Logger;


public class SimpleDataCb {

    private static Logger LOGGER = Logger.getLogger(SimpleDataCb.class);
    public Maapi maapi;

    SimpleDataCb(Maapi m) {
        this.maapi = m;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        LOGGER.info("setElem()");
        try {
            LOGGER.info("kp:"+ Arrays.toString(kp));
            ConfTag leaf = (ConfTag) kp[0];
            switch (leaf.getTagHash()) {
            case smp._ip:
                maapi.setElem(trans.getTransaction(), new ConfInt32(1), "/hval");
                break;
            case smp._port:
                maapi.setElem(trans.getTransaction(), new ConfInt32(2), "/hval");
                break;
            }
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
        try {
            maapi.setElem(trans.getTransaction(), new ConfInt32(3), "/hval");
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
        try {
            maapi.setElem(trans.getTransaction(), new ConfInt32(4), "/hval");
            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }

}
