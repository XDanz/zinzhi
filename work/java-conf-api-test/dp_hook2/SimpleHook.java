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

public class SimpleHook {


    /** ------------------------------------------------------------
     *   main
     *
     */
    static public void main(String args[]) {

        try {

            Socket s = new Socket("localhost", Conf.PORT);
            Maapi m = new Maapi(s);
            m.addNamespace(new smp());

            Socket s2 = new Socket("localhost", Conf.PORT);
            Maapi m2 = new Maapi(s2);
            m2.addNamespace(new smp());

            /* create the callbacks */
            SimpleTransCb trans_cb = new SimpleTransCb(m);
            SimpleDataCb data_cb = new SimpleDataCb(m, m2);

            /* create new control socket */
            Socket ctrl_socket= new Socket("127.0.0.1",Conf.PORT);

            /* init and connect the control socket */
            Dp dp = new Dp("server_daemon",ctrl_socket);
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
            System.err.println("(closing) "+e.getMessage());
        }
    }

    static public void trace(String str) {
        System.err.println("*SimpleTransform: "+str);
    }

}
