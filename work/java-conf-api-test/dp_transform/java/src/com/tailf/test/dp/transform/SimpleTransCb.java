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

import com.tailf.conf.ConfObject;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.TransCallback;
import com.tailf.dp.proto.TransCBType;
import com.tailf.maapi.Maapi;
import org.apache.log4j.Logger;
import com.tailf.test.dp.transform.namespaces.*;


public class SimpleTransCb  {
    private static Logger LOGGER = Logger.getLogger(SimpleTransCb.class);
    public Maapi maapi;

    SimpleTransCb(Maapi m) {
        maapi=m;
    }

    @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        try {
            maapi.attach(trans.getTransaction(), new smp().hash(),
                         trans.getUserInfo().getUserId() );
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
        LOGGER.info("init(): userinfo= "+trans.getUserInfo());
    }

    @TransCallback(callType=TransCBType.FINISH)
        public void finish(DpTrans trans) throws DpCallbackException {
        LOGGER.info("finish() CALLED");
    }


}
