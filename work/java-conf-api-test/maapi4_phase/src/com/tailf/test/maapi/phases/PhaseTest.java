
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

package com.tailf.test.maapi.phases;

import java.io.*;
import java.net.*;
import com.tailf.cdb.*;


import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.tailf.conf.*;
import com.tailf.maapi.*;

import org.junit.runner.*;

import org.apache.log4j.Logger;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;

//import com.tailf.test.maapi.phases.namespaces.*;

public class PhaseTest {
    private static Logger LOGGER = Logger.getLogger(PhaseTest.class);
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;
    public Socket s;
    public Cdb cdb;
    public static String host;
    public Maapi maapi;
    public static final int port = 4565;


    /***********************************************************************
     * Init and close
     */
    @Before public void init1() throws ConfException,Exception {
        try{
            s = new Socket(host, port);
            maapi = new Maapi(s);
            //maapi.addNamespace(new mtest());
            //maapi.addNamespace(new cs());
        }catch(ConfException e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");
            ae.initCause(e);
        }catch(Exception e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");
            ae.initCause(e);
            throw ae;
        }
    }


    public void initOper() throws ConfException, Exception {
        maapi.startUserSession("jb", InetAddress.getLocalHost(),
                               "maapi", new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);
    }

    public void initAdmin() throws ConfException, Exception {

        maapi.startUserSession("admin", InetAddress.getLocalHost(),
                               "maapi", new String[] {"admin"},
                               MaapiUserSessionFlag.PROTO_TCP);
    }


    @After
        public void close1() throws IOException {
        if(s != null)
            s.close();
    }

    public void close2() throws ConfException, IOException {
        if(maapi != null)
            maapi.endUserSession();

        close1();
    }





    /************************************************************
     * Test 0
     * start to phase 1
     */
    @Test
        public void TestCase0() {
        LOGGER.info("** Test Case 0: STARt --> ");
        try {


            maapi.startPhase(1,true);


        } catch (MaapiException e) {
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");
            ae.initCause(e);
            throw ae;
        }catch(ConfException e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");
            ae.initCause(e);
            throw ae;


        } catch(IOException e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");

            ae.initCause(e);
            throw ae;
            //failed(e);
        }catch(Exception e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");

            ae.initCause(e);
            throw ae;

        }
        LOGGER.info("** Test Case 0: --> PASS");

    }

    /************************************************************
     * Test 2
     * start to phase 2
     */
    @Test
        public void  TestCase1() {

        try {
            LOGGER.info("** Test Case 1: Start Conf to Phase 2 --> ");
            maapi.startPhase(2,true);

        } catch (MaapiException e) {
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 2");

            ae.initCause(e);
            throw ae;


        }catch (ConfException e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 2");

            ae.initCause(e);
            throw ae;
            //failed(e);

        }catch(Exception e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");

            ae.initCause(e);
            throw ae;

        }
        LOGGER.info("** Test Case 1: Start Conf to Phase 2 --> PASS");
    }


    /************************************************************
     * Test 3
     * stop
     */
    @Test
        public void TestCase2() {

        try {
            LOGGER.info("** Test Case 2: Stopping ConfD -->");

            maapi.stop(true);
            LOGGER.info("** Test Case 2: Stoping ConfD --> OK");

        } catch (ConfException e) {
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 2");

            ae.initCause(e);
            throw ae;


        }catch(Exception e){
            AssertionError ae =
                new AssertionError(
                                   "Could not start to phase 1");

            ae.initCause(e);
            throw ae;

        }

    }
}
