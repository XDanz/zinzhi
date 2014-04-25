package com.tailf.test.dp8;

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

import java.net.InetAddress;
import java.net.Socket;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfObject;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import org.apache.log4j.Logger;
import com.tailf.test.dp8.namespaces.*;

public class test {
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;
    private static Logger LOGGER = Logger.getLogger(test.class);

    static public void main(String args[]) {
        int i;

        LOGGER.info("---------------------------------------");
        LOGGER.info("Java DP tests");
        LOGGER.info("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * Abort trans
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

                    ConfObject x = maapi.getElem(th, "/servers/oref");
                    LOGGER.debug("OBJ " + x.toString());
                    x = maapi.getElem(th, "/servers/oref");
                    LOGGER.debug("OBJ2 " + x.toString());

                    ConfObject x2 = maapi.getElem(th, "/servers/lst");
                    LOGGER.debug("LIST " + x2.toString());


                    maapi.endUserSession();
                    s.close();
                }
            }

        };


        /************************************************************
         * Run the tests
         */

        int from = 0;
        int to   = tests.length;

        // from = 21;
        // to = 22;

        test.testnr = from;

        for(i = from ; i < to ; i++)
            tests[i].doit();

        /************************************************************
         * Summary
         */
        LOGGER.info("---------------------------------------");
        LOGGER.info("  Passed:  " + test.pass);
        LOGGER.info("  Failed:  " + test.fail);
        LOGGER.info("  Skipped: " + test.skip);
        LOGGER.info("---------------------------------------");
        if (test.fail == 0)
            System.exit(0);
        else
            System.exit(1);
    }


    private static void testStart() {
        test.testnr++;
        test.processed=false;
        System.err.print("Test "+test.testnr+": ");
    }

    private static void passed() {
        if (!test.processed) {
            test.processed = true;
            test.pass++;
            LOGGER.info("passed");
        }
    }

    private static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            LOGGER.info("skipped");
        }
    }


    private static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.info("failed");
            LOGGER.info("    '"+e.toString()+"'");
            e.printStackTrace();
        }
    }

    private static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.info("failed");
            LOGGER.info("    '"+reason+"'");
        }
    }
}

