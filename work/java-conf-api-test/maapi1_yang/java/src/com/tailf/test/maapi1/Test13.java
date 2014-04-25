package com.tailf.test.maapi1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.tailf.dp.Dp;

import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.test.maapi1.namespaces.*;

public class Test13 {

    public static final int port = 4565;

    public static void main(String[] arg) throws Exception {
        new Test13().test();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception {

        Socket s =
            new Socket("127.0.0.1", port);

        Maapi maapi = new Maapi(s);
        maapi.startUserSession("jb",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int th   = maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);

        maapi.setNamespace(th, "http://tail-f.com/test/maapi1/mtest");

        /*
        maapi.delete(th, "/mtest/types/c_ipv4");

        try {
            System.out.println("VALIDATE TRANS --> ");
            maapi.validateTrans(th, true, true);
            System.out.println("VALIDATE TRANS --> OK");
        } catch (MaapiException e) {
            System.err.println(e.errorCode);
            //e.printStackTrace();
            String msg =
                "/mtest:mtest/types/c_ipv4 is not configured";

            if (!e.errorCode.equals(ErrorCode.ERR_NOTSET)){

                System.out.println("should have thrown "+
                                   "'/mtest:mtest/types/c_ipv4' is not "+
                                   "configured: "+
                                   e);
            }
        }


        maapi.setElem(th, "1.2.3.4",
                      "/mtest/types/c_ipv4");

        System.out.println("VALIDATE TRANS --> ");
        maapi.validateTrans(th, true, true);
        System.out.println("VALIDATE TRANS --> OK");
        */

        maapi.setElem(th, "200", "/mtest/types/c_int16");

        try {
            System.out.println("VALIDATE TRANS --> ");

            maapi.validateTrans(th, true, true);
            System.out.println("VALIDATE TRANS --> OK");

            System.err.println("should have thrown a validation error");

        } catch (MaapiWarningException e) {
            if (e.getWarnings() != null &&
                e.getWarnings().length == 1 &&
                e.getWarnings()[0].getPath().toString().
                equals("/mtest:mtest/types/c_int16") &&
                e.getWarnings()[0].getMessage().
                equals("must be less than 100")) {
                // ok
            }
            else {
                System.err.println("should have caused a validation warning: "+
                                   e.getWarnings()[0].getPath()+" "+
                                   e.getWarnings()[0].getMessage()+" "+
                                   e.getWarnings().length);
            }
        }

        s.close();
    }

}