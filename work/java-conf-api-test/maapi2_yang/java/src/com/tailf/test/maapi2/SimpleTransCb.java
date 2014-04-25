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

package com.tailf.test.maapi2;

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.TransCallback;
import com.tailf.dp.proto.TransCBType;

import java.util.Iterator;
import java.io.*;

import org.apache.log4j.Logger;

public class SimpleTransCb {

    private static Logger LOGGER =
        Logger.getLogger(SimpleTransCb.class);

    @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.info("init(): userinfo= "+ trans.getUserInfo());
    }

    @TransCallback(callType=TransCBType.TRANS_LOCK)
        public void transLock(DpTrans trans) throws DpCallbackException {
        LOGGER.info("transLock()");
    }

    @TransCallback(callType=TransCBType.TRANS_UNLOCK)
        public void transUnlock(DpTrans trans) throws DpCallbackException {
        LOGGER.info("transUnlock()");
    }

    @TransCallback(callType=TransCBType.WRITE_START)
    public void writeStart(DpTrans trans) throws DpCallbackException {
                LOGGER.info("writeStart()");
    }

    @TransCallback(callType=TransCBType.PREPARE)
    public void prepare(DpTrans trans) throws DpCallbackException {
        LOGGER.info("prepare()");
        }

    @TransCallback(callType=TransCBType.ABORT)
        public void abort(DpTrans trans) throws DpCallbackException {
        LOGGER.info("abort()");
    }

    @TransCallback(callType=TransCBType.COMMIT)
        public void commit(DpTrans trans) throws DpCallbackException {
        LOGGER.info("commit()");
    }

    @TransCallback(callType=TransCBType.FINISH)
        public void finish(DpTrans trans) throws DpCallbackException {
        LOGGER.info("finish()");
    }




}
