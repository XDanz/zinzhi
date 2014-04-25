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


import java.net.Socket;

import com.tailf.cdb.Cdb;

public abstract class TestCase {
    public Socket s;
    public Cdb cdb;
    public static String host;

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
        host = new String("localhost");
        test.processed=false;
        System.err.print("Test "+test.testnr+": ");
        test.testnr++;
    }

    public static void passed() {
        if (!test.processed) {
            test.processed = true;
            test.pass++;
            System.err.println("passed");
        }
    }

    public static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            System.err.println("skipped");
        }
    }


    public static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            System.err.println("failed");
            System.err.println("    '"+e.toString()+"'");
            e.printStackTrace();
        }
    }

    public static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            System.err.println("failed");
            System.err.println("    '"+reason+"'");
        }
    }

}
