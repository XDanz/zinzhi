package com.tailf.test.ha1;

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EnumSet;

import javax.naming.ConfigurationException;

import com.tailf.conf.*;
import com.tailf.ha.*;
import com.tailf.proto.*;
import org.apache.log4j.Logger;
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
        LOGGER.info("Java HA tests");
        LOGGER.info("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * test ha.connect()
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s0 = new Socket("localhost", 4565);
                    Socket s1 = new Socket("localhost", 4575);
                    Socket s2 = new Socket("localhost", 4585);

                    Ha ha0 = new Ha(s0, "node0");
                    Ha ha1 = new Ha(s1, "node1");
                    Ha ha2 = new Ha(s2, "node2");

                    ha0.beNone();
                    ha1.beNone();
                    ha2.beNone();

                    HaStatus status0 = ha0.status();
                    HaStatus status1 = ha1.status();
                    HaStatus status2 = ha2.status();

                    LOGGER.debug("status 0 : " + status0.getHaState());
                    LOGGER.debug("status 1 : " + status1.getHaState());
                    LOGGER.debug("status 2 : " + status2.getHaState());

                    if (!status0.getHaState().equals(HaStateType.NONE)) {
                        failed("expected status0 state NONE but got " +
                               status0.getHaState());
                    }
                    if (!status1.getHaState().equals(HaStateType.NONE)) {
                        failed("expected status1 state NONE but got " +
                               status1.getHaState());
                    }
                    if (!status2.getHaState().equals(HaStateType.NONE)) {
                        failed("expected status2 state NONE but got " +
                               status2.getHaState());
                    }

                    s0.close();
                    s1.close();
                    s2.close();
                }
            },

            /************************************************************
             * Test 1
             * test master and slaves
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s0 = new Socket("localhost", 4565);
                    Socket s1 = new Socket("localhost", 4575);
                    Socket s2 = new Socket("localhost", 4585);

                    Ha ha0 = new Ha(s0, "clus0");
                    Ha ha1 = new Ha(s1, "clus0");
                    Ha ha2 = new Ha(s2, "clus0");

                    ConfHaNode master =
                        new ConfHaNode(new ConfBuf("node0"),
                          new ConfIPv4(InetAddress.getByName("localhost")));
                    ha0.beMaster(master.getNodeId());
                    Thread.sleep(500);
                    ha1.beSlave(new ConfBuf("node1"), master, true);
                    Thread.sleep(500);
                    ha2.beSlave(new ConfBuf("node2"), master, true);
                    Thread.sleep(500);

                    HaStatus status0 = ha0.status();
                    HaStatus status1 = ha1.status();
                    HaStatus status2 = ha2.status();

                    LOGGER.debug("status 0 : " + status0.getHaState());
                    for (ConfHaNode n : status0.getNodes()) {
                        LOGGER.debug("Master for node : " +
                                           n.toString());
                    }
                    if (status0.getNodes().length != 2) {
                        failed("expected 2 slaves but got : " +
                               status0.getNodes().length);
                    }
                    LOGGER.debug("status 1 : " + status1.getHaState());
                    for (ConfHaNode n : status1.getNodes()) {
                        LOGGER.debug("Slave for node : " +
                                           n.toString());
                    }
                    if (status1.getNodes().length != 1) {
                        failed("expected 1 master but got : " +
                               status1.getNodes().length);
                    }
                    LOGGER.debug("status 2 : " + status2.getHaState());
                    for (ConfHaNode n : status2.getNodes()) {
                        LOGGER.debug("Slave for node : " +
                                           n.toString());
                    }
                    if (status2.getNodes().length != 1) {
                        failed("expected 1 master but got : " +
                               status1.getNodes().length);
                    }

                    if (!status0.getHaState().equals(HaStateType.MASTER)) {
                        failed("expected status0 state MASTER but got " +
                               status0.getHaState());
                    }
                    if (!status1.getHaState().equals(HaStateType.SLAVE)) {
                        failed("expected status1 state SLAVE but got " +
                               status1.getHaState());
                    }
                    if (!status2.getHaState().equals(HaStateType.SLAVE)) {
                        failed("expected status2 state SLAVE but got " +
                               status2.getHaState());
                    }

                    ha0.slaveDead(new ConfBuf("node2"));
                    Thread.sleep(1000);

                    status0 = ha0.status();
                    status1 = ha1.status();
                    status2 = ha2.status();

                    LOGGER.debug("status 0 : " + status0.getHaState());
                    for (ConfHaNode n : status0.getNodes()) {
                        LOGGER.debug("Master for node : " + n.toString());
                    }
                    if (status0.getNodes().length != 1) {
                        failed("expected 1 slave but got : " +
                               status0.getNodes().length);
                    }
                    LOGGER.debug("status 1 : " + status1.getHaState());
                    for (ConfHaNode n : status1.getNodes()) {
                        LOGGER.debug("Slave for node : " +
                                     n.toString());
                    }
                    if (status1.getNodes().length != 1) {
                        failed("expected 1 master but got : " +
                               status1.getNodes().length);
                    }
                    LOGGER.debug("status 2 : " + status2.getHaState());
                    if (status2.getNodes() == null) {
                        LOGGER.debug("node2 not part of cluster");
                    } else {
                        for (ConfHaNode n : status2.getNodes()) {
                            LOGGER.debug("Slave for node : " +
                                         n.toString());
                        }
                        failed("expected node2 to be none HA but got " +
                               status2);
                    }

                    if (!status0.getHaState().equals(HaStateType.MASTER)) {
                        failed("expected status0 state MASTER but got " +
                               status0.getHaState());
                    }
                    if (!status1.getHaState().equals(HaStateType.SLAVE)) {
                        failed("expected status1 state SLAVE but got " +
                               status1.getHaState());
                    }
                    if (!status2.getHaState().equals(HaStateType.NONE)) {
                        failed("expected status2 state NONE but got " +
                               status2.getHaState());
                    }

                    s0.close();
                    s1.close();
                    s2.close();
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



    // private static void testStart() {
    //     test.testnr++;
    //     test.processed=false;
    //     System.err.print("Test "+test.testnr+": ");
    // }

    // private static void passed() {
    //     if (!test.processed) {
    //         test.processed = true;
    //         test.pass++;
    //         LOGGER.info("passed");
    //     }
    // }

    // private static void skipped() {
    //     if (!test.processed) {
    //         test.processed = true;
    //         test.skip++;
    //         LOGGER.info("skipped");
    //     }
    // }


    // private static void failed(Exception e) {
    //     if (!test.processed) {
    //         test.fail++;
    //         test.processed = true;
    //         LOGGER.info("failed");
    //         LOGGER.info("    '"+e.toString()+"'");
    //         e.printStackTrace();
    //     }
    // }

    // private static void failed(String reason) {
    //     if (!test.processed) {
    //         test.fail++;
    //         test.processed = true;
    //         LOGGER.info("failed");
    //         LOGGER.info("    '"+reason+"'");
    //     }
    // }
}

