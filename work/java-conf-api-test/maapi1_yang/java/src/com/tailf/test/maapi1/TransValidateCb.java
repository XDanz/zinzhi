package com.tailf.test.maapi1;

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
import com.tailf.dp.annotations.*;
import com.tailf.dp.proto.*;
import com.tailf.maapi.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;

import com.tailf.test.maapi1.namespaces.*;
import org.apache.log4j.Logger;

public class TransValidateCb {
    private static Logger LOGGER = Logger.getLogger(TransValidateCb.class);
    public Maapi maapi;

    TransValidateCb(Maapi maapi) {
        this.maapi = maapi;
    }

    @TransValidateCallback(callType = { TransValidateCBType.INIT })
        public void init(DpTrans trans) throws DpCallbackException {
        // attach to the transaction
        try {
            int th = trans.getTransaction();
            //jdemo jd = new jdemo();

            mtest mt = new mtest();
            maapi.attach(th, mt.hash(),
                         trans.getUserInfo().getUserId());
            maapi.setNamespace(th, mt.uri());

        } catch (Exception e) { // IOException, MaapiException
            throw new DpCallbackException("failed to attach via maapi: "+
                                          e.getMessage());
        }
    }

    @TransValidateCallback(callType = { TransValidateCBType.STOP })
        public void stop(DpTrans trans) throws DpCallbackException {
        try {
            maapi.detach(trans.getTransaction());
        } catch (Exception e) { // IOException, MaapiException
            /* never mind */
        }
    }
}