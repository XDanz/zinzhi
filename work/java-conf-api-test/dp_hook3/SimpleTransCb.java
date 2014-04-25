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

import com.tailf.conf.Conf;
public class SimpleTransCb {

    public Maapi maapi;
    SimpleTransCb(Maapi m) { maapi=m;}


    @TransCallback(callType=TransCBType.INIT)
    public void init(DpTrans trans) throws DpCallbackException {
        if (trans.getDBName()!=Conf.DB_RUNNING)
            return;
        try {
            maapi.attach(trans.getTransaction(), new smp().hash(),
                         trans.getUserInfo().getUserId() );
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
        trace("init(): userinfo= "+trans.getUserInfo());
    }

    @TransCallback(callType=TransCBType.FINISH)
    public void finish(DpTrans trans) throws DpCallbackException {
    }

    public void trace(String str) {
        System.err.println("*SimpleTransCb: "+str);
    }

    private void trace(ConfObject[] kp) {
        String s= "";
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        trace("kp = "+s);
    }

}
