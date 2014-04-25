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
import java.io.IOException;
import com.tailf.navu.*;
import com.tailf.dp.Dp;

import com.tailf.conf.*;
import com.tailf.proto.*;

import com.tailf.maapi.*;
import com.tailf.test.maapi1.namespaces.*;
import org.apache.log4j.Logger;

public class TestObjRef {
    private static Logger log = Logger.getLogger(TestObjRef.class);
    public static final int port = 4565;

    public static void main(String[] arg) {
        new TestObjRef().test();
    }

    public void test() {
        try{
            ConfObject r,r2;

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

            maapi.setNamespace(th,new mtest().uri());

            ConfPath path =
                new ConfPath("/mtest:mtest/movables/movable{1\\ 2}");


            log.info("PP:" + Arrays.toString(path.getKP()));

            s.close();
        } catch(Exception e) {
            log.error("", e);
            System.exit(1);
        }
    }
}