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
package com.tailf.test.maapi2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpTransCallback;

import com.tailf.test.maapi2.namespaces.*;
import org.apache.log4j.Logger;

public class SimpleProvider {


    private static final Logger log =
        Logger.getLogger(SimpleProvider.class);

    /** ------------------------------------------------------------
     *   main
     *
     */
    static public void main(String args[]) {
        Dp dp = null;

        try {
            SimpleDataCb data_cb = new SimpleDataCb();
            SimpleTransCb trans_cb = new SimpleTransCb();
            SimpleDBCb    db_cb    = new SimpleDBCb();

            /* create new control socket */
            Socket ctrl_socket= new Socket("127.0.0.1",Conf.PORT);

            /* init and connect the control socket */
            dp = new Dp("server_daemon",ctrl_socket);

            /* register the callbacks */
            dp.registerAnnotatedCallbacks( data_cb );
            dp.registerAnnotatedCallbacks( trans_cb );
            dp.registerAnnotatedCallbacks( db_cb );
            dp.registerDone();

            /* also need to provide namespace for the smp.cs
             * this is generated with --emit-java
             */

            /* read input from control socket */
            while (true) dp.read();

        } catch (Exception e) {
            log.error("(closing) "+e.getMessage());
        }finally{
            dp.shutDownThreadPoolNow();
        }
    }


}