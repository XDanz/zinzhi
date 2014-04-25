package com.tailf.test.dp9;

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

import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.DpTransValidateCallback;
import com.tailf.dp.annotations.TransValidateCallback;
import com.tailf.dp.proto.TransValidateCBType;
import com.tailf.maapi.Maapi;
import org.apache.log4j.Logger;


public class SimpleTransValidateCb implements DpTransValidateCallback {

    private static Logger LOGGER =
        Logger.getLogger(SimpleTransValidateCb.class);

    SimpleTransValidateCb() {
    }

    @TransValidateCallback(callType=TransValidateCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("init(): userinfo= "+trans.getUserInfo());
        // attach to the transaction

    }


    @TransValidateCallback(callType=TransValidateCBType.STOP)
        public void stop(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("stop()");
    }


}
