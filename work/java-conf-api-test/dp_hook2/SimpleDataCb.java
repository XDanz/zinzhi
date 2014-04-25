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

import java.net.Socket;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfInt32;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfValue;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import com.tailf.maapi.Maapi;

public class SimpleDataCb {

    SimpleDataCb(Maapi m, Maapi m2) {
        this.maapi = m;
        this.maapi2 = m2;
    }

    public Maapi maapi;
    public Maapi maapi2;


    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        trace("setElem()");
        try {
            ConfTag leaf = (ConfTag) kp[0];
            switch (leaf.getTagHash()) {
            case smp.smp_ip:
                maapi.setElem(trans.getTransaction(), new ConfInt32(1), "/hval");
                break;
            case smp.smp_port:
                maapi.setElem(trans.getTransaction(), new ConfInt32(2), "/hval");
                if (newval.toString().compareTo("999") == 0) {
                    int usid = trans.getUserInfo().getUserId();

                    int th = maapi2.startTransInTrans(Conf.MODE_READ_WRITE,
                                                      usid,
                                                      trans.getTransaction());
                    maapi2.setElem(th, new ConfInt32(244), "/hval2");
                    maapi2.ncsCommitSaveTrans(th, trans.getTransaction(),
                                              usid, "/diff-set");
                    maapi2.finishTrans(th);

                }
                else if (newval.toString().compareTo("1000") == 0) {
                    int usid = trans.getUserInfo().getUserId();
                    maapi2.ncsApplyDiffSet(trans.getTransaction(), true, "/diff-set");
                }
                break;

            }
            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
    public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        trace("create()");
        try {
            maapi.setElem(trans.getTransaction(), new ConfInt32(3), "/hval");
            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.REMOVE)
    public int remove(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        trace("create()");
        try {
            maapi.setElem(trans.thandle, new ConfInt32(4), "/hval");
            return Conf.REPLY_OK;
        }
        catch (Exception e) {
            throw new DpCallbackException(e.toString());
        }
    }

    public void trace(String str) {
        System.err.println("*SimpleDataCb: "+str);
    }

    private void trace(ConfObject[] kp) {
        String s= "";
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        trace("kp = "+s);
    }

}
