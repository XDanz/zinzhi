package com.tailf.test.cdb3;
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
import org.apache.log4j.Logger;
import com.tailf.test.cdb3.namespaces.*;

public abstract class TestCase {
    public Socket s;
    public Cdb cdb;
    public static String host;
    private static Logger LOGGER = Logger.getLogger(TestCase.class);

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
        cdb.addNamespace(new bulk());
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
        LOGGER.debug("Test "+ test.testnr +": ");
        test.testnr++;
    }

    public static void passed() {
        if (!test.processed) {
            test.processed = true;
            LOGGER.info("Test " + test.testnr + " passed");
            test.pass++;

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
            LOGGER.error("Test " + test.testnr + " failed");
            LOGGER.error("    '"+e.toString()+"'");
            e.printStackTrace();
        }
    }

    public static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.error("failed");
            LOGGER.error("    '"+reason+"'");
        }
    }

}
