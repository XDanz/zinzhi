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

import org.apache.log4j.Logger;

public class TestGetCase{

    private static Logger LOGGER = Logger.getLogger(TestGetCase.class);

    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        new TestGetCase().test();
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

        maapi.setElem(th,new ConfBuf("budwiser"),"/mtest/food/case1/beer");

        ConfTag tag = maapi.getCase(th,mtest._snack_,
                                    "/mtest/food");

        LOGGER.info("tag:" + tag);
        //maapi.setElem(th, "200", "/mtest/types/c_int16");


        s.close();
    }

}