package com.tailf.test.cdb2;

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

import java.net.Socket;

import com.tailf.cdb.Cdb;

import com.tailf.cdb.CdbCLIDiffIterate;
import com.tailf.cdb.CdbDiffIterate;

import com.tailf.cdb.CdbExtendedException;
import com.tailf.cdb.CdbNotificationType;
import com.tailf.cdb.CdbSubscription;
import com.tailf.cdb.CdbSubscriptionSyncType;
import com.tailf.cdb.CdbSubscriptionType;

import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.test.cdb2.namespaces.*;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfCLIToken;
import com.tailf.conf.ConfInternal;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfValue;
import com.tailf.maapi.MaapiException;
import org.apache.log4j.Logger;

public class test implements CdbDiffIterate,
                             CdbCLIDiffIterate
{
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;
    private static Logger LOGGER = Logger.getLogger(test.class);
    static public void main(String args[]) {
        int i;

        LOGGER.info("---------------------------------------");
        LOGGER.info("Java CDB Subscription tests");
        LOGGER.info("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * new CdbSubscription
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSubscription sub = new CdbSubscription(cdb);

                    CdbSubscription sub2 = cdb.newSubscription();
                    s.close();
                }
            },

            /************************************************************
             * Test 1
             * subscribe
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSubscription sub = cdb.newSubscription();

                    ConfPath p = new ConfPath("/system/computer");
                    // LOGGER.info("Path erlang:"+ p.getPath());

                    int subid = sub.subscribe(1,new cs(), "/system/computer");
                    // LOGGER.info("subscribed: subid= "+subid);

                    s.close();
                }
            },

            /************************************************************
             * Test 2
             * subscribe / read
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSubscription sub= cdb.newSubscription();
                    int subid = sub.subscribe(1,new cs(), "/system/computer/");
                    sub.subscribeDone();

                    // LOGGER.info("subscribed: subid= "+subid);

                    /* add new entry from maapi
                     * cant run this in the same thread since it
                     * will deadlock the system
                     */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace n = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( n.uri());
                    //      try { maapi.delete("/system/computer{fred}"); }
                    //catch (Exception ignore) {}
                    //   try { maapi.commitTrans();} catch (Exception ignore) {}
                    //     try { maapi.endTrans();} catch (Exception ignore) {}
                    //    try { maapi.startTrans();} catch (Exception ignore) {}
                    maapi.create("/system/computer{fred}");
                    // maapi.validateTrans();
                    // maapi.prepareTrans();
                    // maapi.commitTrans();
                    maapi.applyTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read */
                    int[] points= sub.read();
                    if (points.length != 1)
                        failed("expected only one point ("+points.length+")");
                    if (points[0] != subid)
                        failed("expeced subid="+subid+" in list of points");
                    sub.sync(CdbSubscriptionSyncType.DONE_SOCKET);
                    s.close();
                }
            },

            /************************************************************
             * Test 3
             * diffIterate
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSubscription sub = cdb.newSubscription();
                    int subid = sub.subscribe(1,new cs(),
                                              "/system/computer");
                    sub.subscribeDone();
                    LOGGER.info("subscribed: subid= "+subid);

                    /* add new entry from maapi
                     * cant run this in the same thread since it
                     * will deadlock the system
                     */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace n = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( n.uri());
                    //    try { maapi.delete("/system/computer{ola}");} c
                    //atch (Exception ignore) {}
                    //     try { maapi.delete("/system/computer{kalle}");}
                    ///catch (Exception ignore) {}
                    //                      try {
                    //maapi.delete("/system/computer{kaka}");}
                    //catch (Exception ignore) {}
                    maapi.create("/system/computer{ola}");
                    maapi.create("/system/computer{kalle}");
                    maapi.create("/system/computer{kaka}");
                    maapi.validateTrans();
                    maapi.prepareTrans();
                    maapi.commitTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read */
                    int[] points= sub.read();
                    if (points.length != 1)
                        failed("expected only one point ("+points.length+")");
                    if (points[0] != subid)
                        failed("expeced subid="+subid+" in list of points");
                    /* diff iterate ! */
                    sub.diffIterate(subid, new test());
                    s.close();
                }
            },

             /************************************************************
              * Test 4
              * CLIdiffIterate
              */
            new TestCase() {
                public void test() throws Exception {

                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test4",s);

                    CdbSubscription sub = cdb.newSubscription();
                    int subid = sub.subscribe(1,new cs(),
                                              "/system/computer");
                    sub.subscribeDone();

                    LOGGER.info("subscribed: subid= "+subid);

                    /* add new entry from maapi
                     * cant run this in the same thread since it
                     * will deadlock the system
                     */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace n = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( n.uri());
                    maapi.create("/system/computer{ola2}");
                    maapi.create("/system/computer{kalle2}");
                    maapi.create("/system/computer{kaka2}");
                    maapi.validateTrans();
                    maapi.prepareTrans();
                    maapi.commitTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read */
                    int[] points = sub.read();
                    String str = "[";
                    for(int i = 0; i < points.length; i++){
                        str += points[i] + " ";
                    }
                    str += "]";
                    if (points.length != 1)
                        failed("expected only one point ("+points.length+")");
                    if (points[0] != subid)
                        failed("expeced subid="+subid+" in list of points");

                    /* diff iterate ! */
                    sub.CLIdiffIterate(subid,
                                       new CdbCLIDiffIterate() {
                                           public DiffIterateResultFlag
                                               iterate(ConfObject[] kp,
                                                       DiffIterateOperFlag op,
                                                       ConfObject old_value,
                                                       ConfObject new_value,
                                                       ConfObject clistr,
                                                       ConfCLIToken[] tokens,
                                                       Object state)
                                           {

                                               ConfPath kpString =
                                                   new ConfPath(kp);

                                               return DiffIterateResultFlag
                                                   .ITER_RECURSE;
                                           }

                                       });
                    s.close();

                }
            },
            /************************************************************
             * Test 5
             * subscribePrepare
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    ConfNamespace cns = new cs();
                    CdbSubscription sub= cdb.newSubscription();
                    int subid = sub.subscribe(CdbSubscriptionType
                                              .SUB_RUNNING_TWOPHASE,
                                              1,cns, "/system/computer");
                    sub.subscribeDone();
                    LOGGER.info("subscribed: subid= "+subid);

                    /* add new entry from maapi
                     * cant run this in the same thread since it
                     * will deadlock the system
                     */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace n = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( n.uri());
                    maapi.create("/system/computer{prep1}");
                    maapi.create("/system/computer{prep2}");
                    maapi.create("/system/computer{prep3}");
                    maapi.validateTrans();
                    maapi.prepareTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    for(int ii=0; ii<1; ii++) {
                        try {
                            /* now do the blocking read */
                            int[] points= sub.read();
                            CdbNotificationType notifType =
                                sub.getLatestNotificationType();
                            String[] nTypes = new String[] {"unused",
                                                            "Prepare",
                                                            "Commit",
                                                            "Abort", "Oper"};
                            LOGGER.info("----- Notification Type : " +
                                               nTypes[notifType.getValue()]);
                            if (points.length != 1)
                                failed("expected only one point ("+
                                       points.length+")");
                            if (points[0] != subid)
                                failed("expected subid="+subid+
                                       " in list of points");
                            /* diff iterate ! */
                            sub.diffIterate(subid, new test());
                            if (ii == 0) {
                                sub
               .abortTransaction(
                                 new CdbExtendedException(CdbExtendedException
                         .ERRCODE_APPLICATION,  null, null,
                                               "T E S T   A B O R T !"));
                                } else {
                                sub.sync(CdbSubscriptionSyncType.DONE_PRIORITY);
                                }
                        } catch (MaapiException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    s.close();
                }
            },

            /************************************************************
             * Test 6
             * trigger subscription
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSubscription sub= cdb.newSubscription();
                    int subid = sub.subscribe(1,new cs(), "/system/computer/");
                    sub.subscribeDone();

                    Socket s2 = new Socket(host, Conf.PORT);
                    Cdb cdb2 = new Cdb("test2",s2);
                    //                  Thread trigger =
                    //new Thread(new TriggerThread(cdb2, new int[] {subid}));
                    Thread trigger = new Thread(new TriggerThread(cdb2, null));
                    trigger.start();

                    /* now do the blocking read */
                    int[] points= sub.read();
                    if (points.length != 1)
                        failed("expected only one point ("+points.length+")");
                    if (points[0] != subid)
                        failed("expeced subid="+subid+" in list of points");
                    sub.sync(CdbSubscriptionSyncType.DONE_SOCKET);
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

        if(args.length > 0){

            String sfrom = args[0];
            try{
                from = Integer.parseInt(sfrom);

            }catch(NumberFormatException e){

            }
        }

        if(args.length > 1){
            String sto = args[1];
            try{
                to = Integer.parseInt(sto);
            }catch(NumberFormatException e){

            }
        }


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

    public DiffIterateResultFlag iterate(ConfObject[] kp,
                                         DiffIterateOperFlag op,
                                         ConfObject old_value,
                                         ConfObject new_value,
                                         Object state) {

        ConfPath kpString = new ConfPath(kp);
        LOGGER.debug("diffIterate: kp= "+kpString+", OP="+op+
              ", old_value="+old_value+", new_value="+new_value);

        return DiffIterateResultFlag.ITER_RECURSE;
    }


    public DiffIterateResultFlag iterate(ConfObject[] kp,
                                         DiffIterateOperFlag op,
                                         ConfObject old_value,
                                         ConfObject new_value,
                                         ConfObject clistr,
                                         ConfCLIToken[] tokens,
                                         Object state) {

        ConfPath kpString = new ConfPath(kp);
        LOGGER.debug("CLIdiffIterate: \n kp='" +
                    kpString+"',\n OP='" +
                    op + "',\n old_value='" +
                    old_value + "',\n new_value='"
                    + new_value  + "',\n cli_str='" + clistr + "'" );

        return DiffIterateResultFlag.ITER_RECURSE;
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

    // /**
    //  * for debugging
    //  */
    // protected String kpToString(ConfObject[] kp) {
    //     String s= new String("");
    //     for (int i=0;i< kp.length; i++)
    //         s= s + kp[i].toString() + "/" ;
    //     return s;
    // }

    public static class TriggerThread implements Runnable {
        Cdb cdb1 = null;
        int[] sps1 = null;

        public TriggerThread(Cdb cdb, int[] sps) {
            cdb1 = cdb;
            sps1 = sps;
        }

        public void run() {
            try {
                Thread.sleep(3000);
                cdb1.triggerSubscriptions(sps1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

