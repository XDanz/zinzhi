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
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import com.tailf.test.maapi2.namespaces.*;

public class SimpleDataCb  {

    private static Logger log = Logger.getLogger(SimpleDataCb.class);

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
        public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        log.info("CALLED getElem(th:" + trans.getTransaction() + ",kp: " +
                    Arrays.toString(kp) + ")");
        log.info("hideInactive:" + trans.isHideInactive());
        final smp ns = new smp();


        if ( ((ConfTag)kp[0]).getTagHash() == ns._active &&
             trans.isHideInactive() ) {

            return null;
        }
        return new ConfUInt32(123);
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
        public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        log.info("---setElem");
        return Conf.REPLY_OK;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
        public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        log.info("---create");
        return Conf.REPLY_OK;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.EXISTS_OPTIONAL)
        public boolean existsOptional(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        log.info("---exists_optional");
        if (((ConfTag) kp[0]).getTag().equals("active")) {
            if (trans.isHideInactive()) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }





}
