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

package com.tailf.test.dp.action;

import java.net.Socket;

import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpActionCallback;
import com.tailf.test.dp.action.namespaces.*;

import com.tailf.conf.ConfValue;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfPath;


import org.apache.log4j.Logger;

public class Actions{

    private static Logger LOGGER =
        Logger.getLogger(Actions.class);

    public Actions() {}

    static public void main(String arg[]){
        Dp       dp = null;
        try {

            //ActionCb action_cb;
            Socket   ctrl_socket;

            /* create the callbacks */
            ActionCb action_cb = new ActionCb();
            ActionCbTest acb = new ActionCbTest();
            MathAction mathaction = new MathAction();

            MathActionRange1 range1 = new MathActionRange1();
            MathActionRange2 range2 = new MathActionRange2();
            //ActionCbTest2 acb2 = new ActionCbTest2();
            /* create new control socket */
            ctrl_socket = new Socket("127.0.0.1",Conf.PORT);

            /* init and connect the control socket */
            dp = new Dp("ServerDemonDP3Test",ctrl_socket);
            // Dp dp = new Dp("server_daemon",ctrl_socket);

            /* register the callbacks */
            dp.registerAnnotatedCallbacks(action_cb);
            dp.registerAnnotatedCallbacks(acb);
            //dp.registerAnnotatedCallbacks(acb2);
            dp.registerAnnotatedCallbacks(mathaction);

            dp.registerAnnotatedRangeActionCallbacks(range1,
                                                     new ConfValue[]{
                                                         new ConfBuf("a")},
                                                     new ConfValue[]{

                                                         new ConfBuf("k")},
                                                     new ConfPath("/systemz/computerz")
                                                     );
            dp.registerAnnotatedRangeActionCallbacks(range2,
                                                     new ConfValue[]{
                                                         new ConfBuf("l")},
                                                     new ConfValue[]{

                                                         new ConfBuf("z")},
                                                     new ConfPath("/systemz/computerz")
                                                     );


            dp.registerDone();


            /* read input from control socket */
            while(true) dp.read();


        } catch (Exception e) {

            LOGGER.error("(closing) " +e.getMessage());
        }finally{
            dp.shutDownThreadPoolNow();
        }
    }



}