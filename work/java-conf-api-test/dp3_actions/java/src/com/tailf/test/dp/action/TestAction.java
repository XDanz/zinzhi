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

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuContext;
import com.tailf.navu.NavuException;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamStart;
import com.tailf.conf.ConfXMLParamLeaf;
import com.tailf.conf.ConfEnumeration;

import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfXMLParamStop;
import com.tailf.util.*;

import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfNamespace;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.conf.ConfException;



import org.w3c.dom.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import org.apache.log4j.Logger;

import com.tailf.test.dp.action.namespaces.*;

public class TestAction {
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;
    public static Thread t;
    public Maapi maapi;
    public Socket s;
    public int th;

    public ConfNamespace ns;
    public ConfNamespace ns2;
    public ConfNamespace ns3;

    public ConfXMLParamToXML transf;

    private static Logger LOGGER =
        Logger.getLogger(TestAction.class);



    static public void main(String args[]) {
        LOGGER.info("---------------------------------------");
        LOGGER.info("Java DP Action tests");
        LOGGER.info("---------------------------------------");
        org.junit.runner.JUnitCore.main(TestAction.class.getName());
    }

    @BeforeClass
        public static void startActioncb(){

    }

    @Before
        public void init(){
        try{
            s = new Socket("localhost", Conf.PORT);
            maapi = new Maapi(s);
            maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                   "maapi", new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            maapi.loadSchemas();
            th = maapi.startTrans(Conf.DB_RUNNING,
                                  Conf.MODE_READ_WRITE);
            ns = new config();
            ns2 = new testaction();

            transf = new ConfXMLParamToXML();

        }catch(ConfException e){
            AssertionError ae =
                new AssertionError("Could not create connection  ");
            ae.initCause(e);
            throw ae;

        }catch(UnknownHostException e){
            AssertionError ae =
                new AssertionError( "Could not create connection  ");
            ae.initCause(e);
            throw ae;

        }catch(IOException e){
            AssertionError ae =
                new AssertionError( "Could not create connection  ");
            ae.initCause(e);
            throw ae;
        }
    }

    @After
        public void end(){
        try{
            maapi.endUserSession();
            s.close();

        }catch(ConfException e){
            AssertionError ae =
                new AssertionError("Could not close connection  ");
            ae.initCause(e);
            throw ae;

        }catch(IOException e){
            AssertionError ae =
                new AssertionError("Could not close connection  ");
            ae.initCause(e);
            throw ae;
        }
    }


    /************************************************************
     * Test 0
     * Action "reboot"
     */

    @Test
        public void test0(){
        LOGGER.info("---------------------------------------");
        LOGGER.info("Start of test 0 Action: 'reboot'");
        LOGGER.info("---------------------------------------");

        try{
            ConfXMLParam[] params =
                new ConfXMLParam[] {};

            ConfXMLParam[] res =
                maapi.requestAction(params,
                                    ns.hash(),
                                    "/config/system/reboot");

        }catch(IOException e) {
            fail(e,"could not execute action Test fail!!!");
        }catch(ConfException e){
            fail(e,"could not execute action Test fail!!!");
        }

        LOGGER.info("End of test 0 Action: 'reboot' ");

    }

    @Test
        public void test01(){
        NavuContainer rebootAction = null;
        try{
            NavuContainer root =
                new NavuContainer(new NavuContext(maapi,th));


            NavuContainer cfg =
                root.container(new config().hash())
                .container(config._config_);

            NavuContainer system = cfg.container(config._system);


            rebootAction = system.action(config._reboot);
        }catch(NavuException e){
            fail(e,"Could not navigate to action Test Fail!!");
        }


        ConfXMLParam[] result = null;
        try{
            ConfXMLParam[] param =
                new ConfXMLParam[]{};

            result =
                rebootAction.call(param);

        }catch(NavuException e){
            fail(e,"Could not invoke action Test fail!!!");
        }

        ConfXMLParam[] expected =
            new ConfXMLParam[]{};

        assertArrayEquals(result ,expected);
    }


    /************************************************************
     * Test 1
     * Action "Restart"
     */

    @Test
        public void test1() throws Exception {
        LOGGER.info("Start of test 1 Action: 'Restart'");

        /* no parameters */
        ArrayList<ConfNamespace> ns_list = maapi.addNamespace( ns );
        ConfXMLParam[] params =
            new ConfXMLParam[] {
            new ConfXMLParamValue(ns.hash(),
                                  config._mode,
                                  new ConfBuf("forced"),
                                  ns_list),

            new ConfXMLParamLeaf(ns.hash(),
                                 config._debug),

            new ConfXMLParamStart(ns.hash(),
                                  config._foo),

            new ConfXMLParamLeaf(ns.hash(),
                                 config._debug),

            new ConfXMLParamStop(ns.hash(),
                                 config._foo)

        };

        Document doc =
            transf.toXML(params);

        transf.serialize(doc,System.out);
        maapi.addNamespace(new testaction());

        ConfXMLParam[] res =
            maapi.requestAction(params,
                                ns.hash(),
                                "/config/system/restart");


        if ( ! (res[0].getValue() instanceof ConfBuf)){
            String str = "  Request action restart should return time";
            AssertionError ae =
                new AssertionError(str);
            throw ae;
        }
    }

    @Ignore
        public void test11(){
        NavuContainer restartAction = null;
        try{
            NavuContainer root =
                new NavuContainer(new NavuContext(maapi,th));

            NavuContainer cfg =
                root.container(new config().hash())
                .container(config._config_);

            NavuContainer system = cfg.container(config._system);


            restartAction = system
                .action(config._restart);
        }catch(NavuException e){
            fail(e,"Could not navigate to action Test Fail!!");
        }


        ConfXMLParam[] result = null;
        try{
            ConfXMLParam[] param =
                new ConfXMLParam[]{};

            result =
                restartAction.call(param);

        }catch(NavuException e){
            fail(e,"Could not invoke action Test fail!!!");
        }

        ConfXMLParam[] expected =
            new ConfXMLParam[]{};

        assertArrayEquals(result ,expected);
    }


    /************************************************************
     * Test 2
     * Action "server reset"
     */

    @Test public void test2() throws Exception {
        LOGGER.info("---------------------------------------");
        LOGGER.info("Start of test 2 Action: 'server reset'");
        LOGGER.info("---------------------------------------");

        /* no parameters */
        ArrayList<ConfNamespace> ns_list = maapi.addNamespace( ns );

        ConfXMLParam[] params =
            new ConfXMLParam[] {
            new ConfXMLParamValue(ns.hash(),
                                  config._when,
                                  new ConfBuf("now"),
                                  ns_list) };

        ConfXMLParam[] res =
            maapi.requestAction(params, ns.hash(),
                                "/config/server{iolo}/reset");


        if ( ! (res[0].getValue() instanceof ConfBuf)){
            AssertionError ae =
                new AssertionError("Request action restart should return time");
            throw ae;
        }

    }


    /************************************************************
     * Test 2
     * Action "purge alarm test"
     */

    @Test public void test3() throws Exception {
        LOGGER.info("---------------------------------------");
        LOGGER.info("Start of test 2 Action: ''");
        LOGGER.info("---------------------------------------");

        ArrayList<ConfNamespace> ns_list = maapi.addNamespace( ns );
        //new ConfXMLParamStart(ns2 , testaction._purge_alarms_),

        maapi.loadSchemas();

        ConfEnumeration cenum =
            ConfEnumeration.getEnumByLabel(
                     "/ta:testalarms/purge-alarms/alarm-status", "any");

        ConfXMLParam[] params= new ConfXMLParam[] {
            new ConfXMLParamValue(ns2,testaction._alarm_status_,
                                  cenum),
        };
        //new ConfXMLParamStop(ns2,testaction._purge_alarms_)
        Document doc = transf.toXML(params);

        transf.serialize(doc,System.out);

        ConfXMLParam[] res =
            maapi.requestAction(params, ns2.hash(), "/testalarms/purge-alarms");


        if ( ! (res[0].getValue() instanceof ConfBuf)){
            AssertionError ae =
                new AssertionError("Request action restart should return time");
            throw ae;
        }

    }

    /************************************************************
     * Test 3
     * Action "purge alarm test"
     */

    @Test
        public void test4() throws Exception {
        LOGGER.info("---------------------------------------");
        LOGGER.info("Start of test 2 Action: ''");
        LOGGER.info("---------------------------------------");

        ArrayList<ConfNamespace> ns_list =
            maapi.addNamespace( ns );
        //new ConfXMLParamStart(ns2 , testaction._purge_alarms_),
        maapi.loadSchemas();

        ConfEnumeration cenum =
            ConfEnumeration.getEnumByLabel(
                     "/ta:testalarms/purge-alarms/alarm-status", "any");

        ConfXMLParam[] params =
            new ConfXMLParam[] {
            new ConfXMLParamValue(ns2,
                                  testaction._alarm_status_,
                                  cenum),
        };
        //new ConfXMLParamStop(ns2,testaction._purge_alarms_)
        Document doc = transf.toXML(params);

        transf.serialize(doc,System.out);

        ConfXMLParam[] res =
            maapi.requestAction(params, ns2.hash(),
                                "/testalarms/purge-alarms");


        if ( ! (res[0].getValue() instanceof ConfBuf)){
            AssertionError ae =
                new AssertionError("Request action restart should return time");
            throw ae;
        }

    }

    void fail(Throwable t, String msg){
        throwException(t,msg);
    }

    AssertionError throwException(Throwable t,String msg){
        AssertionError ae =
            new AssertionError(msg);
        ae.initCause(t);
        return ae;

    }

}

