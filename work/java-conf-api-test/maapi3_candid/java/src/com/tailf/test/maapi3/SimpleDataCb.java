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
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class SimpleDataCb  {

    private static Logger LOGGER = Logger.getLogger(SimpleDataCb.class);

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
        public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("---getElem");
        return new ConfUInt32(123);
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
        public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        LOGGER.info("---setElem");
        return Conf.REPLY_OK;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
        public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("---create");
        return Conf.REPLY_OK;
    }




}
