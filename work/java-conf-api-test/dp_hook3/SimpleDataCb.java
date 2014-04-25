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

import java.io.IOException;
import com.tailf.conf.*;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import com.tailf.maapi.Maapi;

import com.tailf.conf.Conf;

public class SimpleDataCb {

    SimpleDataCb(Maapi m, Maapi m2) {
        this.maapi = m;
        this.maapi2 = m2;
    }

    public Maapi maapi;
    public Maapi maapi2;

    @DataCallback(callPoint="simplecp", callType=DataCBType.WRITE_ALL)
    public int dispatch(DpTrans trans, ConfObject[] kp)
        throws IOException, ConfException {
        if (trans.getDBName()!=Conf.DB_RUNNING)
            return Conf.REPLY_OK;

        try {

            int usid = trans.getUserInfo().getUserId();

            ConfValue flagVal = maapi.getElem(trans.getTransaction(), "/servers/flag");
            System.out.println("flagval  = " + flagVal);

            if (flagVal.toString().compareTo("1") == 0) {
                ConfBuf diffSet =  (ConfBuf)maapi.getElem(trans.getTransaction(), "/diff-set");
                maapi.setElem(trans.getTransaction(),new ConfInt32(888),"/servers/flag");
                maapi.ncsApplyReverseDiffSet(trans.getTransaction(), diffSet);
                return Conf.REPLY_OK;
            }


            int th = maapi2.startTransInTrans(Conf.MODE_READ_WRITE,
                                              usid,
                                              trans.getTransaction());

            maapi2.create(th, "/servers/server{bar}");

            String bb = "/servers/server{bar}/top/bb";
            String bbb = "/servers/server{bar}/top/bbb";

            maapi2.setElem(th, new ConfBuf("zapper"), bb);
            maapi2.setElem(th, new ConfBuf("zapper"), bbb);


            maapi2.ncsCommitSaveTrans(th, trans.getTransaction(),
                                       usid, "/diff-set");
            //maapi2.finishTrans(th);
        }
        catch (Exception ee) {
            System.out.println("ERR" + ee);
            ee.printStackTrace();
        }
        return Conf.REPLY_OK;
    }
}
