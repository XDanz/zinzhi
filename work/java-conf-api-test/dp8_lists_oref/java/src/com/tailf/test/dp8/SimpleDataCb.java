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

import java.util.ArrayList;

import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfInt32;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfList;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfObjectRef;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfValue;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import org.apache.log4j.Logger;
import com.tailf.test.dp8.namespaces.*;


public class SimpleDataCb {

    private static Logger LOGGER = Logger.getLogger(SimpleDataCb.class);
    private int counter = 0;


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
        public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.debug("getElem()");
        LOGGER.debug("XXXX " + kp);
        /* switch on xml elem tag */
        ConfTag leaf = (ConfTag) kp[0];
        ArrayList<ConfNamespace> ns_list = new ArrayList<ConfNamespace>();
        smp ns =  new smp();
        ns_list.add(ns);

        switch (leaf.getTagHash()) {
        case smp._oref:
            if ((counter++ % 2) == 0) {
                ConfObject[] elems = new ConfObject[1];
                elems[0] = new ConfTag(ns.hash(), smp._foo, ns_list);
                return new ConfObjectRef(elems);
            }
            else {
                ConfObject[] elems = new ConfObject[]
                    {
                        new ConfTag(ns.hash(),
                                    smp._bi,
                                    ns_list),
                        new ConfKey(new ConfObject[] {new ConfInt32(13)}),
                        new ConfTag(ns.hash(),
                                    smp._b,
                                    ns_list),

                        new ConfTag(ns.hash(), smp._bs, ns_list),
                        new ConfKey(new ConfObject[] {new ConfInt32(12)}),

                        new ConfTag(ns.hash(), smp._a,
                                    ns_list),
                        new ConfTag(ns.hash(), smp._as,
                                    ns_list)
                    };

                return new ConfObjectRef(elems);
            }
        case smp._i:
            return new ConfInt32(8888);
        case smp._lst: {
            ConfObject[]  es = new ConfObject[2];
            es[0] = new ConfBuf("abc"); es[1] = new ConfBuf("def");
            return new ConfList(es);
        }
        default:
            throw new DpCallbackException("xml tag not handled");
        }
    }


}
