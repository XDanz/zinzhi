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

import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.DpTransValidateCallback;
import com.tailf.dp.annotations.TransValidateCallback;
import com.tailf.dp.proto.TransValidateCBType;
import com.tailf.maapi.Maapi;

import com.tailf.test.dp2.namespaces.smp;

import org.apache.log4j.Logger;

public class SimpleTransValidateCb implements DpTransValidateCallback {
    private static Logger LOGGER =
        Logger.getLogger(SimpleTransValidateCb.class);
    public Maapi maapi;

    SimpleTransValidateCb(Maapi maapi) {
        this.maapi = maapi;
    }

    @TransValidateCallback(callType=TransValidateCBType.INIT)
    public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.info("init(): userinfo= "+trans.getUserInfo());
        // attach to the transaction
        try {
            int th= trans.getTransaction();
            maapi.attach(th, new smp().hash(), trans.getUserInfo().getUserId());
            maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");
        } catch (Exception e) { // IOException, MaapiException
            throw new DpCallbackException("failed to attach via maapi: "+e.getMessage());
        }

    }


    @TransValidateCallback(callType=TransValidateCBType.STOP)
    public void stop(DpTrans trans) throws DpCallbackException {
        LOGGER.info("stop()");
        try {
            maapi.detach(trans.getTransaction());
        } catch (Exception e) { // IOException, MaapiException
            /* never mind */
        }
    }



}
