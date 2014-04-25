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

import java.net.InetAddress;
import java.net.Socket;

import java.util.Arrays;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfUInt16;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.MaapiWarningException;

import com.tailf.test.dp2.namespaces.smp;
import org.apache.log4j.Logger;

public class Test1{

    public static void main(String[] arg) throws Exception{
        new Test1().test();
    }

    public void test() throws Exception {
        Socket s = new Socket("localhost", Conf.PORT);
        Maapi maapi = new Maapi(s);
        maapi.addNamespace(new smp());
        maapi.startUserSession("ola", InetAddress.getLocalHost(),
                               "maapi", new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);
        // start transaction
        int th   = maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);

        maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");
        maapi.delete(th, "/servers/server{www}");
        maapi.setElem(th, new ConfIPv4(193,168,0,3),
                      "/servers/server{ssh}/ip");
        maapi.validateTrans(th, false, true);
        maapi.prepareTrans(th);
        maapi.abortTrans(th);
        maapi.endUserSession();
        s.close();
    }

}