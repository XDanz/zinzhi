package com.tailf.test.maapi1;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

public class Test48{

    private Logger LOGGER = Logger.getLogger(Test48.class);

    Maapi maapi = null;
    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        new Test48().test();

    }

    public void test() throws Exception{

        try{

        Socket s = new Socket("127.0.0.1", port);
         maapi = new Maapi(s);

         maapi.startUserSession("admin", InetAddress.getLocalHost(),
                               "maapi", new String[] {"admin"},
                                MaapiUserSessionFlag.PROTO_TCP);
         int usid = maapi.getMyUserSession();
         int th   = maapi.startTrans(Conf.DB_RUNNING,
                                     Conf.MODE_READ_WRITE);

        File fp = new File("cfgtest48_v3.xml");


        saveConfig(th,
                   EnumSet.of(MaapiConfigFlag.MAAPI_CONFIG_XML_PRETTY,
                              MaapiConfigFlag.MAAPI_CONFIG_WITH_OPER),
                   "/mtest:mtest/servers",
                   fp);

        // maapi.loadConfig(th,
        //EnumSet.of(MaapiConfigFlag.XML_FORMAT),
        //                  "java/cfgtest48.out");

        //loadConfigStream(th,EnumSet.of(MaapiConfigFlag.XML_FORMAT));


        }catch(Exception e){

            LOGGER.error("some error occured:",e);
        }

    }


    private void loadConfigStream(int tid, EnumSet<MaapiConfigFlag> flags){


        try{
            MaapiOutputStream outstream =
                maapi.loadConfigStream(tid,flags);

            FileInputStream fis =
                new FileInputStream(
                                    new File("cfgtest48.xml"));

            int n = -1;
            byte[] buf = new byte[256];
            //while(!outstream.hasWriteAll() ){
            while((n = fis.read(buf,0,buf.length)) > 0){
                //LOGGER.info("WRITE>" + new String(buf,0,n));
                outstream.write(buf,0,n);
            }

        }catch(Exception e) {
            LOGGER.error("Error: ", e);
            System.exit(1);
        }
    }



    private void saveConfig(int tid,
                            EnumSet<MaapiConfigFlag> flags,
                            String path,
                            File f){

        try {
            LOGGER.info("SAVE_CONFIG -->");

            MaapiInputStream in =
                maapi.saveConfig(tid, flags, path);

            LOGGER.info("in:" + in);
            LOGGER.info("SAVE_CONFIG --> OK");

            FileOutputStream fout =
                new FileOutputStream(f);

            //fw = new FileWriter(f);

            int cnt = 0;
            int b;
            byte[] buf = new byte[1024];

            while((b = in.read(buf,0,buf.length)) != -1){
                LOGGER.info("b:" + b);
                fout.write(buf,0,b);
                cnt++;
            }


            if(in.hasReadAll())
                LOGGER.info("READ ALL WORKS");
            else
                LOGGER.info("READ ALL DONT WORKS");


            in.close();
            fout.flush();
            fout.close();


            if (cnt == 0) {
                LOGGER.info("cnt:" + cnt);
            }
            //      System.out.println("config size : "+cnt);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}