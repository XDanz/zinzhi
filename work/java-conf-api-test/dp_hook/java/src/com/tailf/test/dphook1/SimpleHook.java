package com.tailf.test.dphook1;

/*    -*- Java -*-
 *
 *  Copyright 2007 Tail-F Systems AB. All rights reserved.
 *
 *  This software is the confidential and proprietary
 *  information of Tail-F Systems AB.
 *
 *  $Id$
 *
 */

import java.net.Socket;

import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpTransCallback;
import com.tailf.maapi.Maapi;
import com.tailf.test.dphook1.namespaces.*;

import org.apache.log4j.Logger;

public class SimpleHook {
    private static Logger LOGGER = Logger.getLogger(SimpleHook.class);

    /** ------------------------------------------------------------
     *   main
     *
     */
    static public void main(String args[]) {

        Dp dp = null;
        try {

            Socket s = new Socket("localhost", Conf.PORT);
            Maapi m = new Maapi(s);
            m.addNamespace(new smp());

            /* create the callbacks */
            SimpleTransCb trans_cb = new SimpleTransCb(m);
            SimpleDataCb data_cb = new SimpleDataCb(m);

            /* create new control socket */
            Socket ctrl_socket= new Socket("127.0.0.1",Conf.PORT);

            /* init and connect the control socket */
            dp = new Dp("server_daemon",ctrl_socket);
            // Dp dp = new Dp("server_daemon",ctrl_socket);

            /* register the callbacks */
            dp.registerAnnotatedCallbacks( trans_cb );
            dp.registerAnnotatedCallbacks( data_cb );
            dp.registerDone();

            /* also need to provide namespace for the smp.cs
             * this is generated with --emit-java
             */
            dp.addNamespace( new smp() );

            /* read input from control socket */
            while (true) dp.read();

        } catch (Exception e) {
            LOGGER.error("Error", e);

        }finally{
            dp.shutDownThreadPoolNow();
        }
    }


}
