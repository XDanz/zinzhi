package com.tailf.test.dp9;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import javax.naming.ConfigurationException;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfBinary;
import com.tailf.conf.ConfHexList;
import com.tailf.conf.ConfList;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfOID;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfOctetList;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfAttributeValue;
import com.tailf.conf.ConfAttributeType;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiCursor;
import com.tailf.maapi.MoveWhereFlag;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.test.dp9.namespaces.*;
import org.apache.log4j.Logger;
public class test {
    private static Logger LOGGER = Logger.getLogger(test.class);
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;

    public static ConfKey lastkey = null;
    public static ConfKey firstkey = null;

    static public void main(String args[]) {
        int i;

        LOGGER.info("---------------------------------------");
        LOGGER.info("Java DP tests");
        LOGGER.info("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * order-by user
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
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    checkOrder(new String[] {"s1","s2","s3","s4","s5","s6"},
                               th, maapi);

                    maapi.moveOrdered(th, MoveWhereFlag.MOVE_AFTER,
                                      lastkey, "/servers/server{s2}");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ_WRITE);

                    checkOrder(new String[] {"s1","s3","s4","s5","s6","s2"}, th, maapi);

                    maapi.moveOrdered(th, MoveWhereFlag.MOVE_BEFORE,  lastkey, "/servers/server{s4}");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING, Conf.MODE_READ_WRITE);

                    checkOrder(new String[] {"s1","s3","s5","s6","s4","s2"}, th, maapi);

                    maapi.moveOrdered(th, MoveWhereFlag.MOVE_LAST,  null, "/servers/server{s5}");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING, Conf.MODE_READ_WRITE);

                    checkOrder(new String[] {"s1","s3","s6","s4","s2","s5"}, th, maapi);

                    maapi.moveOrdered(th, MoveWhereFlag.MOVE_FIRST,  null, "/servers/server{s6}");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING, Conf.MODE_READ_WRITE);

                    checkOrder(new String[] {"s6","s1","s3","s4","s2","s5"}, th, maapi);

                    maapi.moveOrdered(th, MoveWhereFlag.MOVE_BEFORE,  firstkey, "/servers/server{s3}");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING, Conf.MODE_READ_WRITE);

                    checkOrder(new String[] {"s3","s6","s1","s4","s2","s5"}, th, maapi);

                    maapi.finishTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },
            /************************************************************
             * Test 1
             * opaque callback data
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {


                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    smp mysmp = new smp();
                    maapi.addNamespace(mysmp);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING, Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    maapi.getElem(th, "/servers/server{s1}/ip");
                    maapi.finishTrans(th);
                    /* no parameters */
                            ConfXMLParam[] params= new ConfXMLParam[] {};
                            ConfXMLParam[] res= maapi.requestAction(params, mysmp.hash()
                                                                    , "/servers/server{s1}/reboot");

                            maapi.endUserSession();
                            s.close();

                            File f = new File("opaque.prop");
                            Properties testprop = new Properties();
                            testprop.load(new FileInputStream(f));
                            if (!"OPAQUE_TEST_DATA_CB".equals(testprop.get("DATA"))) {
                                failed("did not receive correct opaque info for data callback");
                            }
                            if (!"OPAQUE_TEST_VALPOINT_CB".equals(testprop.get("VALPOINT"))) {
                    failed("did not receive correct opaque info for valpoint callback");
                }
                if (!"OPAQUE_TEST_ACTION_CB".equals(testprop.get("ACTION"))) {
                    failed("did not receive correct opaque info for action callback");
                }

                //                              f.delete();

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


    private static void checkOrder(String[] sarr, int th, Maapi maapi)
        throws IOException, ConfException {
        MaapiCursor cursor = maapi.newCursor(th,"/smp:servers/server");
        ConfKey key = null;
        firstkey = null;
        int j=0;
        while ((key = maapi.getNext(cursor)) != null) {
            if (firstkey == null) {
                firstkey = key;
            }
            lastkey=key;

            if(!key.toString().equals("{" + sarr[j] + "}")) {
                failed("expected value at index "+j+" to be " + sarr[j] + " but found " + key.toString());
            }
            j++;
        }

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

