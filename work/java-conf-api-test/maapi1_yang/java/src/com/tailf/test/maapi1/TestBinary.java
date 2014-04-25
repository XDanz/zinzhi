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

public class TestBinary{

    private static Logger LOGGER = Logger.getLogger(TestBinary.class);
    public static final int port = 4565;


    public static void main(String[] arg) throws Exception{
        new TestBinary().test();
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

        int th =  maapi.startTrans(Conf.DB_RUNNING,
                                   Conf.MODE_READ_WRITE);


        MaapiCursor c =
            maapi.newCursor(th,"/bintest/bindata");

        LOGGER.info("c:" + c);

        List<ConfKey> keys = new ArrayList<ConfKey>();


        ConfKey k = maapi.getNext(c);
        LOGGER.info("key:" +k);
        keys.add(k);

        while(k != null){
            k = maapi.getNext(c);
            LOGGER.info("key:" + k);

            if(k != null)  keys.add(k);
        }

        for(ConfKey key : keys){
            //LOGGER.info("key:" + key);
            //fmtTest("/bintest/bindata{%x}", key);
            maapi.exists(th,"/bintest/bindata{%x}",
                         key);
        }




        maapi.validateTrans(th, false, false);
        maapi.prepareTrans(th);
        maapi.finishTrans(th);

        s.close();

    }

    public void fmtTest(String fmt,Object... args) {
        LOGGER.info("**************************'");
        LOGGER.info("fmt:" + fmt);
        LOGGER.info("args.length:" + args.length);
        LOGGER.info("elems:" + Arrays.toString(args));

    }



}