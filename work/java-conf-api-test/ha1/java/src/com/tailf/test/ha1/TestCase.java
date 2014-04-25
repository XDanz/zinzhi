package com.tailf.test.ha1;


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

import org.apache.log4j.Logger;



public abstract class TestCase  {

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
     * Reporting functions
     */

    public static void testStart() {
        test.processed=false;
        LOGGER.debug("TEST "+test.testnr+": ");
        test.testnr++;
    }

    public static void passed() {
        if (!test.processed) {
            test.processed = true;
            test.pass++;
            LOGGER.info("Test " + (test.testnr-1)  + " PASSED");
        }
    }

    public static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            LOGGER.info("SKIPPED");
        }
    }


    public static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.error("FAILED");
            LOGGER.error("    '"+e.toString()+"'", e);
        }
    }

    public static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.error("FAILED");
            LOGGER.error("    '"+reason+"'");
        }
    }

}
