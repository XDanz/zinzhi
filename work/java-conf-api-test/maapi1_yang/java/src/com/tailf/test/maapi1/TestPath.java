package com.tailf.test.maapi1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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


public class TestPath{
    private Logger LOGGER = Logger.getLogger(Test17.class);
    public static final int port = 4565;

    public static void main(String[] arg)throws Exception{
        new TestPath().test();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception{
        //String str = "/mtest/servers/server{%s}/foo";
        String str = "/ncs:ncs/sm/java-vm-startup/java-logging/logger";
        ConfPath path = new ConfPath(str);
        LOGGER.info(path.toString());
        //ConfPath path = new ConfPath(str,new Object[] {
        //      new ConfBuf(new String("www").getBytes())
        //  });

        // Socket s =
        //     new Socket("127.0.0.1", port);

        // Maapi maapi = new Maapi(s);
        // maapi.addNamespace(new mtest());
        // maapi.addNamespace(new cs());
        // maapi.startUserSession("admin",
        //                        InetAddress.getLocalHost(),
        //                        "maapi",
        //                        new String[] {"admin"},
        //                        MaapiUserSessionFlag.PROTO_TCP);

        // int th   = maapi.startTrans(Conf.DB_RUNNING,
        //                             Conf.MODE_READ_WRITE);

        // maapi.setNamespace(th, "http://tail-f.com/test/maapi1/mtest");

        // LOGGER.info("cd /mtest/servers/server{%s}/foo -->");

        // maapi.cd(th, "/mtest/servers/server{%s}/foo",
        //          new Object[] {
        //              new String("www")
        //          });
        //  LOGGER.info("PWD:" +maapi.getCwd(th));

        //  LOGGER.info("cd /mtest/servers/server{%s}/foo --> OK");


         //  LOGGER.info("cd /mtest/servers/server{%x}/foo -->");
         //  maapi.cd(th, "/mtest/servers/server{%x}/foo",
         //                      new Object[] {
         //               new ConfBuf(new String("www").getBytes())
         //            });

         //  LOGGER.info("PWD2:" +maapi.getCwd(th));

         //  LOGGER.info("cd /mtest/servers/server{%x}/foo --> OK");

           // try {

        //     maapi.cd(th, "/mtest/servers/server{%d}/foo",
        //              new Object[] {
        //                  new ConfBuf(new String("www").getBytes())
        //              });
        //     LOGGER.error("should have thrown an exception");
        // } catch (ConfException e) {
        //     LOGGER.info("exception thrown:" + e.getMessage());
        //     ;
        // }

        // //this does not work
        //   maapi.cd(th, "/mtest/servers/server{x%d}/foo",
        //            new Object[] {
        //                new Integer(100)
        //            });

          // maapi.cd(th, "/mtest/servers/server{www}/foo");
          // maapi.cd(th, "../interface{eth0}");
          // maapi.cd(th, "/mtest/servers/server");
          // maapi.cd(th, "{smtp}");
          // maapi.cd(th, "../../dks");
          // maapi.cd(th, "..");
          // maapi.cd(th, "..");
          // try {
          //     maapi.cd(th, "..");
          //     LOGGER.error("should throw cd exception");
          // } catch (Exception e) {
          //     LOGGER.info("got exception: "+ e.getMessage());
          //     if (!e.getMessage().equals("bad '..' path: /"))
          //         LOGGER.error("should have thrown a 'bad .. path'" +
          //                      "MaapiException. Got: " + e.getMessage());
          // }

        // maapi.finishTrans(th);

        // s.close();
         }

}

