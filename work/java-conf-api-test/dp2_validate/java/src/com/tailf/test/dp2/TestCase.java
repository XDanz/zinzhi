package com.tailf.test.dp2;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfValue;
import com.tailf.maapi.MaapiDiffIterate;
import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.DiffIterateResultFlag;
import org.apache.log4j.Logger;

public abstract class TestCase implements MaapiDiffIterate {
    private static Logger LOGGER = Logger.getLogger(TestCase.class);

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
     * Reporting functions
     */



  public static void testStart() {

      test.processed=false;
      LOGGER.debug("XXXXXXXXXXX  START OF - TEST "+test.testnr + " XXXXXXX");
      test.testnr++;
    }


    public static void passed() {
        if (!test.processed) {
            test.processed = true;
            test.pass++;
            LOGGER.info("Test " + (test.testnr-1) +" PASS!!");
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
            failedtests.add(test.testnr-1);
            test.processed = true;
            LOGGER.error("TEST " + (test.testnr-1) +" FAILED!!!! **************");
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