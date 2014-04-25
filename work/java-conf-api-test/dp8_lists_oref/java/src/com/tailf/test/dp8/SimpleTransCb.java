package com.tailf.test.dp8;
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
import org.apache.log4j.Logger;

public class SimpleTransCb {
    private static Logger LOGGER =
        Logger.getLogger(SimpleTransCb.class);

    @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("init(): userinfo= "+trans.getUserInfo());
    }


    @TransCallback(callType=TransCBType.FINISH)
        public void finish(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("finish()");
    }



}
