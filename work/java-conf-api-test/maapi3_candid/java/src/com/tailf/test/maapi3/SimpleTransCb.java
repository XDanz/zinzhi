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

package com.tailf.test.maapi3;

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.TransCallback;
import com.tailf.dp.proto.TransCBType;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.io.*;

import com.tailf.test.maapi3.namespaces.*;

public class SimpleTransCb {

    private static Logger LOGGER =
        Logger.getLogger(SimpleTransCb.class);

    @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("init(): userinfo= "+trans.getUserInfo());
    }

    @TransCallback(callType=TransCBType.TRANS_LOCK)
        public void transLock(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("transLock()");
    }

    @TransCallback(callType=TransCBType.TRANS_UNLOCK)
        public void transUnlock(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("transUnlock()");
    }

    @TransCallback(callType=TransCBType.WRITE_START)
        public void writeStart(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("writeStart()");
    }

    @TransCallback(callType=TransCBType.PREPARE)
        public void prepare(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("prepare()");
    }

    @TransCallback(callType=TransCBType.ABORT)
        public void abort(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("abort()");
    }

    @TransCallback(callType=TransCBType.COMMIT)
        public void commit(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("commit()");
    }

    @TransCallback(callType=TransCBType.FINISH)
        public void finish(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("finish()");
    }


}
