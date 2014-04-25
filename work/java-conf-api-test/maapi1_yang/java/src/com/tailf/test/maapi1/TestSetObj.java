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

public class TestSetObj{

    private Logger LOGGER =
        Logger.getLogger(Test51.class);

    Maapi maapi = null;
    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        Level level = Level.INFO;

        if(arg.length > 0){
            System.out.println("inside..");
            if(arg[0].startsWith("DEBUG")){
                level = Level.DEBUG;
            }else if(arg[0].startsWith("INFO")){
                level = Level.INFO;
            }else if(arg[0].startsWith("OFF")){
                level = Level.OFF;
            }

        }
        new TestSetObj().test(level);


    }

    public void test(Level level) throws Exception{

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


            Logger CONFINTERNAL_LOGGER =
                Logger.getLogger("com.tailf.conf");

            LOGGER.info("logger:" + CONFINTERNAL_LOGGER.getName());
            CONFINTERNAL_LOGGER.setLevel(level);
            LOGGER.setLevel(level);

            /*
            ConfBuf key = new ConfBuf("www");
            ConfObject[] objs =
                maapi.getObject(th,"/mtest/servers/server{%x}",key);

            for (int i = 0; i<objs.length; i++) {
                LOGGER.info("i = " + i + ",OBJ = " + objs[i]);
            }
            */


            ConfObject[] entry = new ConfObject[]{
                new ConfBuf("www2"),
                new ConfIPv4("129.168.0.1"),
                new ConfUInt16(8080),
                new ConfTag(new mtest().hash(),mtest._foo),
                new ConfInt64(667),
                new ConfInt64(668),
                new ConfNoExists()
            };


            maapi.setObject(th,entry,"/mtest/servers/server");

            ConfObject[] subentry = new ConfObject[]{
                new ConfBuf("eth0"),
                new ConfInt64(1400)

            };
            maapi.setObject(th,subentry,"/mtest/servers/server{%x}/interface",
                            new ConfBuf("www2"));


            maapi.validateTrans(th,false,false);
            maapi.prepareTrans(th);
            maapi.commitTrans(th);


        }catch(Exception e){

            LOGGER.error("some error occured:",e);
        }

    }




}