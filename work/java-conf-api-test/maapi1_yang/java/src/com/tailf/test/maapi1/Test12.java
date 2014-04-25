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
import com.tailf.proto.*;
import com.tailf.maapi.*;
import com.tailf.test.maapi1.namespaces.*;
import org.apache.log4j.Logger;

public class Test12 {

    private static Logger Log =
        Logger.getLogger(Test12.class);

    public static final int port = 4565;


    public static void main(String[] arg) throws Exception{
        new Test12().test();
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

        int th =   maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);
        String ss =
        "/mtest:mtest/mtest:servers/mtest:server[srv-name=\"www2\"]/mtest:ip";

        ConfObjectRef ref =
            new ConfObjectRef(ss);
        // //new ConfObjectRef( new ConfEList(new ConfEBinary(ss.getBytes())));

        // //ConfEList list =
        // //new ConfEList(new ConfObjectRef(new ConfEBinary(ss.getBytes())));

        // LOGGER.info("  objectref: "+ref);
        // ///mtest:mtest/mtest:servers/mtest
        //:server{www2}/mtest:ip
         maapi.setElem(th, ref,
                       "/mtest/types/objectref-leaf-list");
         maapi.applyTrans(th, true);
        try {
            maapi.applyTrans(th, true);
        } catch (MaapiException e) {
            e.printStackTrace();

        }
        try {
            maapi.validateTrans(th, false, false);
        } catch (MaapiException e) {
            e.printStackTrace();
        }
        try {
            maapi.prepareTrans(th);
        } catch (MaapiException e) {
            e.printStackTrace();

        }
        s.close();
    }



}