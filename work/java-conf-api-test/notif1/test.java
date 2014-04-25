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
import java.util.*;

import com.tailf.cdb.CdbDiffIterate;
import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfValue;
import com.tailf.notif.*;

public class test {
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;

    static public void main(String args[]) {
        int i;

        System.err.println("---------------------------------------");
        System.err.println("Java NOTIF tests");
        System.err.println("---------------------------------------");

        TestCase[] tests = new TestCase[] {

                    /************************************************************
                     * Test 0
                     * audit notif
                     */
                    new TestCase() {
                        public void test() throws Exception {
                            Socket sock = new Socket(host, Conf.PORT);
                            Notif notif = new Notif( sock,
                                        EnumSet.of(NotificationType.NOTIF_AUDIT));

                            /* now start a thread that sends an event ... */
                            TestMaapi maapi = new TestMaapi();
                            ConfNamespace ns = new cs();
                            maapi.startTrans();
                            maapi.setNamespace( ns.uri());
                            maapi.create("/system/computer{fred0}");
                            maapi.applyTrans();
                            maapi.endTrans();
                            maapi.endSession();
                            maapi.start();

                            /* now do the blocking read.
                             */
                            Notification n = notif.read();
                            trace("--> "+n);
                            if (! (n instanceof AuditNotification)) {
                                failed("expected AuditNotification, got:"+n);
                            }

                            sock.close();
                            Thread.sleep(1000);
                        }
                    },

                    /************************************************************
                     * Test 1
                     * commit simple notif
                     */
                    new TestCase() {
                        public void test() throws Exception {
                            Socket sock = new Socket(host, Conf.PORT);
                            Notif notif = new Notif( sock,
                                        EnumSet.of(NotificationType.NOTIF_COMMIT_SIMPLE));

                            /* now start a thread that sends an event ... */
                            TestMaapi maapi = new TestMaapi();
                            ConfNamespace ns = new cs();
                            maapi.startTrans();
                            maapi.setNamespace( ns.uri());
                            maapi.create("/system/computer{fred1}");
                            maapi.applyTrans();
                            maapi.endTrans();
                            maapi.endSession();
                            maapi.start();

                            /* now do the blocking read.
                             */
                            Notification n = notif.read();
                            trace("--> "+n);
                            if (! (n instanceof CommitNotification)) {
                                failed("expected CommitNotification, got:"+n);
                            }

                            sock.close();
                            Thread.sleep(1000);
                        }
                    },


            /************************************************************
             * Test 2
             * commit diff notif
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket sock = new Socket(host, Conf.PORT);
                    Notif notif = new Notif( sock,
                                EnumSet.of(NotificationType.NOTIF_COMMIT_DIFF));

                    /* now start a thread that sends an event ... */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace ns = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( ns.uri());
                    maapi.create("/system/computer{fred2}");
                    maapi.applyTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read.
                     */
                    Notification n = notif.read();
                    trace("--> "+n);
                    if (! (n instanceof CommitDiffNotification)) {
                        failed("expected CommitDiffNotification, got:"+n);
                    }

                    int thandle = ((CommitDiffNotification) n).thandle;
                    notif.diffNotificationDone(thandle);
                    sock.close();
                    Thread.sleep(1000);
                }
            },


            /************************************************************
             * Test 3
             * user session notif
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket sock = new Socket(host, Conf.PORT);
                    Notif notif = new Notif( sock,
                                EnumSet.of(NotificationType.NOTIF_USER_SESSION));

                    /* now start a thread that sends an event ... */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace ns = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( ns.uri());
                    maapi.create("/system/computer{fred3}");
                    maapi.applyTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read.
                     */
                    Notification n = notif.read();
                    trace("--> "+n);
                    if (! (n instanceof UserSessNotification))
                        failed("expected UserSessNotification got "+n);

                    sock.close();
                    Thread.sleep(1000);
                }
            },


            /************************************************************
             * Test 4
             * Heartbeat notif
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket sock = new Socket(host, Conf.PORT);
                    Notif notif = new Notif( sock,
                                EnumSet.of(NotificationType.NOTIF_HEARTBEAT), 1);


                    /* now do the blocking read.
                     */
                    Notification n = notif.read();
                    trace("--> "+n);
                    if (! (n instanceof HeartbeatNotification )) {
                        failed("expected UpgradeNotification, got:"+n);
                    }
                    System.out.println(n);

                    sock.close();
                    Thread.sleep(1000);
                }
            },

            /************************************************************
             * Test 5
             * commit progress notif
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket sock = new Socket(host, Conf.PORT);
                    Notif notif = new Notif( sock,
                                EnumSet.of(NotificationType.NOTIF_COMMIT_PROGRESS));

                    /* now start a thread that sends an event ... */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace ns = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( ns.uri());
                    maapi.create("/system/computer{fred5}");
                    maapi.applyTrans();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read.
                     */
                    for (int i=0; i<4;i++) {
                            Notification n = notif.read();
                            trace("--> "+n);
                            if (! (n instanceof CommitProgressNotification )) {
                                failed("expected CommitProgressNotification, got:"+n);
                            }
                            System.out.println(n);
                    }

                    sock.close();
                    Thread.sleep(1000);
                }
            },

            /************************************************************
             * Test 6
             *  performUpgrade notif
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket sock = new Socket(host, Conf.PORT);
                    Notif notif = new Notif( sock,
                                EnumSet.of(NotificationType.NOTIF_UPGRADE_EVENT));

                    /* now start a thread that sends an event ... */
                    TestMaapi maapi = new TestMaapi();
                    ConfNamespace ns = new cs();
                    maapi.startTrans();
                    maapi.setNamespace( ns.uri());
                    maapi.initUpgrade(60, 0);
                    maapi.performUpgrade(new String[] {});
                    maapi.commitUpgrade();
//                  maapi.abortUpgrade();
                    maapi.endTrans();
                    maapi.endSession();
                    maapi.start();

                    /* now do the blocking read.
                     */
                    for (int i=0; i<4;i++) {
                            Notification n = notif.read();
                            trace("--> "+n);
                            if (! (n instanceof UpgradeNotification )) {
                                failed("expected UpgradeNotification, got:"+n);
                            }
                            System.out.println(n);
                    }

                    sock.close();
                    Thread.sleep(1000);
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
        System.err.println("---------------------------------------");
        System.err.println("  Passed:  " + test.pass);
        System.err.println("  Failed:  " + test.fail);
        System.err.println("  Skipped: " + test.skip);
        System.err.println("---------------------------------------");
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
            System.err.println("passed");
        }
    }

    public DiffIterateResultFlag iterate(ConfObject[] kp, DiffIterateOperFlag op, ConfValue old_value, ConfValue new_value) {
        trace("diffIterate: kp= "+kpToString(kp)+", OP="+op+
              ", old_value="+old_value+", new_value="+new_value);
        return DiffIterateResultFlag.ITER_CONTINUE;
    }


    private static void trace(String str) {
        System.err.println("*test: "+str);
    }


    private static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            System.err.println("skipped");
        }
    }


    private static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            System.err.println("failed");
            System.err.println("    '"+e.toString()+"'");
            e.printStackTrace();
        }
    }

    private static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            System.err.println("failed");
            System.err.println("    '"+reason+"'");
        }
    }

    /**
     * for debugging
     */
    protected String kpToString(ConfObject[] kp) {
        String s= new String("");
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        return s;
    }

}

