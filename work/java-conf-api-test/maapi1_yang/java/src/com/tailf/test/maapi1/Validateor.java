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

public class Validateor{

    private static Logger LOGGER = Logger.getLogger(Validateor.class);

    public static void main(String[] arg){
        /* create maapi instance to be used
           by the  validate callbacks */
        Dp dp = null;
        try{
            Maapi maapi =
                new Maapi( new Socket("localhost",
                                      Conf.PORT));
            // create new control socket
            Socket ctrlSocket =
                new Socket("127.0.0.1", Conf.PORT);

            // init and connect control socket
             dp =
                new Dp("validate",
                       ctrlSocket);


            // register the stats callbacks
            /* register the validation callbacks and valpoints */
            dp.registerAnnotatedCallbacks(new ValidationPointCb(maapi) );
            dp.registerAnnotatedCallbacks(new TransValidateCb(maapi));
            //dp.registerAnnotatedCallbacks(new Stats());

            dp.registerDone();

            //dp.addNamespace(new  mtest());
            LOGGER.info("started");
            // read input from the control socket

            while (true) dp.read();

        }
        catch (Exception e) {
            LOGGER.error("",e);
        }finally{
            dp.shutDownThreadPoolNow();
        }

    }
}
