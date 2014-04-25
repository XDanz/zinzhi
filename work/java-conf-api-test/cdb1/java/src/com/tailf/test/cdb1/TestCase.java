package com.tailf.test.cdb1;

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
import com.tailf.cdb.*;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.tailf.test.cdb1.namespaces.*;

public abstract class TestCase {
    public Socket s;
    public Cdb cdb;
    public static String host;
    private static Logger LOGGER =
        Logger.getLogger(TestCase.class);

    public static List<Integer> failedtests =
        new ArrayList<Integer>();
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
        s = new Socket(host, Conf.PORT);
        cdb = new Cdb("myname",s);
    }


    public void close1() throws IOException {
        s.close();
    }


    /**********************************************************************
     * Reporting functions
     */

    public static void testStart() {
        host = new String("localhost");
        test.processed=false;
        LOGGER.debug("Test "+test.testnr+": ");
        test.testnr++;
    }

    public static void passed() {
        if (!test.processed) {
            test.processed = true;
            test.pass++;
            LOGGER.info("TEST " + test.testnr + " PASS!!");
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
            test.processed = true;
            failedtests.add(test.testnr-1);

            LOGGER.info("failed");
            LOGGER.info("    '"+e.toString()+"'");
            e.printStackTrace();
        }
    }

    public static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            failedtests.add(test.testnr-1);

            test.processed = true;
            LOGGER.info("failed");
            LOGGER.info("    '"+reason+"'");
        }
    }

}
