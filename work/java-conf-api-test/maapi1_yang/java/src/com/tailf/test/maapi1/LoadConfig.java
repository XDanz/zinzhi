package com.tailf.test.maapi1;

import com.tailf.maapi.*;
import com.tailf.conf.*;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;
import java.net.*;



public class LoadConfig {
    private static Logger LOGGER = Logger.getLogger(LoadConfig.class);
    private Maapi maapi;
    private int th;
    private Socket socket;

    public static void main(String[] arg){
        String filename = arg[0];
        new LoadConfig().loadConfig(filename);

    }
    public LoadConfig(){
        try{
            socket = new Socket("localhost", 4565);
            maapi = new Maapi(socket);

            maapi.startUserSession("admin",
                               InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"admin"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            th = maapi.startTrans(Conf.DB_RUNNING,
                                   Conf.MODE_READ_WRITE);
        }catch(Exception e){
            LOGGER.error("",e);
        }
    }


    void loadConfig(String filename){
        //XML_FORMAT
        //XML_PRETTY
        //CISCO_XR_FORMAT
        //JUNIPER_CLI_FORMAT
        //CISCO_IOS_FORMAT
        EnumSet<MaapiConfigFlag> flags =
            EnumSet.of(MaapiConfigFlag.MAAPI_CONFIG_J,
                       MaapiConfigFlag.MAAPI_CONFIG_REPLACE);

        File f = new File(filename);

        long start = 0;
        MaapiOutputStream out = null;
        FileInputStream in = null;
        boolean ret = true;
        try {

            in = new FileInputStream(f);
            out = maapi.loadConfigStream(th, flags);

            int n = -1;
            byte[] buf = new byte[256];
            //while(!outstream.hasWriteAll() ){

            while((n = in.read(buf,0,buf.length)) > 0)
                out.write(buf,0,n);

            out.getLocalSocket().close();

            LOGGER.info("OK:" + out.hasWriteAll());
            maapi.validateTrans(th,false,true);
            maapi.prepareTrans(th);
            maapi.commitTrans(th);
        }catch(ConfException e){
            LOGGER.error("",e);
        }catch(IOException e){
            LOGGER.error("Some IOExceptin occurred",e);

        }finally{
            try{
                out.close();
                in.close();
            }catch(IOException e){
                LOGGER.error("",e);
            }
        }




        LOGGER.info("mills: " +
                    (System.currentTimeMillis() - start));

    }

}