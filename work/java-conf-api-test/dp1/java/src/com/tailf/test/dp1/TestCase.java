package com.tailf.test.dp1;

/*    -*- Java -*-
 *
 *  Copyright 2007 Tail-f Systems AB. All rights reserved.
 *
 *  This software is the confidential and proprietary
 *  information of Tail-f Systems AB.
 *
 *  $Id$
 *
 */


import java.io.*;
import java.net.*;
import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.test.dp1.namespaces.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public abstract class TestCase implements MaapiDiffIterate {
    public Socket s;
    public Maapi maapi;
    public static String host;
    public static final int port = 4565;

    public static List<Integer> failedtests =
        new ArrayList<Integer>();

    private static Logger LOGGER =
        Logger.getLogger(TestCase.class);

    /**********************************************************************
     * The test case goes here
     */

    public abstract void test() throws Exception;

    /**********************************************************************
     * Redefine to return true if the test should be skipped
     */
    public boolean skip() {
        return false;
    }

    public void doit() {
        if (this.skip()) {
            testStart();
            skipped();
            return;
        }

        try {
            testStart();
            test();
            passed();
        } catch (Exception e) {
            failed(e);
        }
    }

    /**********************************************************************
     * Init and close
     */
    public void init1() throws ConfException, Exception {
        s = new Socket(host, port);
        maapi = new Maapi(s);
        maapi.addNamespace(new smp());
        //maapi.addNamespace(new cs());
    }

    public void initOper() throws ConfException, Exception {
        init1();
        maapi.startUserSession("jb", InetAddress.getLocalHost(),
                               "maapi", new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);
    }

    public void initAdmin() throws ConfException, Exception {
        init1();
        maapi.startUserSession("admin", InetAddress.getLocalHost(),
                               "maapi", new String[] {"admin"},
                               MaapiUserSessionFlag.PROTO_TCP);
    }

    public void close1() throws IOException {
        s.close();
    }

    public void close2() throws ConfException, IOException {
        maapi.endUserSession();
        close1();
    }


    /**********************************************************************
     * Reporting functions
     */

    public static void testStart() {
        host = new String("localhost");
        test.processed=false;
        LOGGER.debug("XXXXXXXXXXX  START OF - TEST "+test.testnr + " XXXXXXX");
        test.testnr++;
    }

    public static void passed() {
        if (!test.processed) {
            test.processed = true;
            test.pass++;
            LOGGER.info("Test " + (test.testnr-1) +" PASS!");
        }
    }

    public static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            LOGGER.info("skipped");
        }
    }


    public static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            failedtests.add(test.testnr-1);
            test.processed = true;
            LOGGER.error("****************** TEST " + (test.testnr-1) +" FAILED!!!! **************");
            LOGGER.error("    '"+e.toString()+"'");
            e.printStackTrace();

        }
    }

    public static void failed(String reason) {
        if (!test.processed) {

            test.fail++;
            failedtests.add(test.testnr-1);

            test.processed = true;
            LOGGER.error("****************** TEST " +
                         (test.testnr-1) +" FAILED!!!! **************");
            LOGGER.info("failed");
            LOGGER.info("    '"+reason+"'");
        }
    }

    // heres the DiffIteration dummy
    // should be overridden in test class if used
    public DiffIterateResultFlag iterate(ConfObject[] kp,
                                         DiffIterateOperFlag op,
                                         ConfValue oldValue,
                                         ConfValue newValue,
                                         Object state) {
        return DiffIterateResultFlag.ITER_STOP;
    }

}
