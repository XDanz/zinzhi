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

public class TestMoved{

    private static Logger LOGGER =
        Logger.getLogger(TestMoved.class);
    public static final int port = 4565;


    public static void main(String[] arg) throws Exception{
        new TestMoved().test();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception {
        Socket s =
            new Socket("127.0.0.1", port);
        Maapi maapi = new Maapi(s);
        // maapi.addNamespace(new mtest());
        // maapi.addNamespace(new cs());


        maapi.startUserSession("jb",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int th =   maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);

        LOGGER.info("OK");


        maapi.create(th, "/servers/server{1}");
        maapi.create(th, "/servers/server{2}");

        MaapiCursor c0 = maapi.newCursor(th,"/servers/server");

        ConfKey x0 = maapi.getNext(c0);

        while(x0 != null){
            LOGGER.info("x0:" + x0);
            x0 = maapi.getNext(c0);
        }

        maapi.moveOrdered(th,MoveWhereFlag.MOVE_FIRST,
                          new ConfKey(new ConfInt32(2)),
                          "/servers/server{2}");


        MaapiCursor c1 = maapi.newCursor(th,"/servers/server");

        ConfKey x1 = maapi.getNext(c1);

        while(x1 != null){
            LOGGER.info("x1:" + x1);
            x1 = maapi.getNext(c1);
        }



        // maapi.validateTrans(th, false, false);
        // maapi.prepareTrans(th);
        s.close();
    }



}