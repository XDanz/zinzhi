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
import org.apache.log4j.Level;





public class Test51 implements MaapiXPathEvalResult ,
                               MaapiXPathEvalTrace {

    private Logger LOGGER =
        Logger.getLogger(Test51.class);

    Maapi maapi = null;
    //FindInSchema f;
    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        String expr = arg[0];
        String ctx = arg[1];

        new Test51().test(ctx,expr);


    }

    public void test(String ctx,String expr) throws Exception{

        LOGGER.info("ctx:" + ctx);
        LOGGER.info("expr:" + expr);

        try{

            Socket s = new Socket("127.0.0.1", port);
            maapi = new Maapi(s);

            maapi.startUserSession("admin",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"admin"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            int usid = maapi.getMyUserSession();
            int th   = maapi.startTrans(Conf.DB_RUNNING,
                                        Conf.MODE_READ_WRITE);

            //f  = new FindInSchema();


            maapi.xpathEval(th,this,this, expr,     new Object(),
                            ctx);
            //"/mtest/servers/server/*",
        }catch(Exception e){

            LOGGER.error("some error occured:",e);
        }

    }

    public XPathNodeIterateResultFlag result(ConfObject[] kp,
                                             ConfValue value,
                                             java.lang.Object state) {

        LOGGER.info("result() ---> START");
        LOGGER.info("supplied-kp:" + Arrays.toString(kp));
        LOGGER.info("value:" + value);
        LOGGER.info("state:" + state);
        LOGGER.info("result() ---> END");

        return XPathNodeIterateResultFlag.ITER_CONTINUE;
    }

    public XPathNodeIterateResultFlag trace(java.lang.String str) {
        //LOGGER.info("trace:" + str);

        return XPathNodeIterateResultFlag.ITER_CONTINUE;

    }



}