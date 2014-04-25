package com.tailf.test.dp7;
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
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfObject;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiCursor;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.test.dp7.namespaces.*;

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
        LOGGER.info("Java DP secodaryIndex from Java Maapi");
        LOGGER.info("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * Maapi cursor without secondary index
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    ConfKey x;
                    MaapiCursor c;

                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

                    c = maapi.newCursor(th, "/servers/server");

                    x = maapi.getNext(c);
                    ConfObject e = x.elementAt(0);
                    LOGGER.debug("getNext --> "+e);
                    if (! e.equals( new ConfBuf("smpt")))
                        failed("getNext should have given us 'smpt'");

                    x = maapi.getNext(c);
                    e = x.elementAt(0);
                    LOGGER.debug("getNext --> "+e);
                    if (! e.equals( new ConfBuf("ssh")))
                        failed("getNext should have given us 'ssh'");

                    x = maapi.getNext(c);
                    e = x.elementAt(0);
                    LOGGER.debug("getNext --> "+e);
                    if (! e.equals( new ConfBuf("www")))
                        failed("getNext should have given us 'www'");

                    x = maapi.getNext(c);
                    if (x!=null) failed("getNext should have returned 'null'");

                    maapi.finishTrans(th);
                    s.close();
                }
            },

            /************************************************************
             * Test 1
             * Maapi cursor WITH secondary index
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    ConfKey x;
                    MaapiCursor c;

                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

                    c = maapi.newCursor(th, "/servers/server");
                    c.setSecondaryIndex("snmp");

                    do {
                        x = maapi.getNext(c);
                        LOGGER.debug("getNext --> "+x);
                    } while (x!=null);

                    maapi.finishTrans(th);
                    s.close();
                }
            },

            /************************************************************
             * Test 2
             * Maapi cursor with FAULTY secondary index
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    MaapiCursor c;

                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

                    c = maapi.newCursor(th, "/servers/server");
                    c.setSecondaryIndex("kalle");

                    try {
                        ConfKey x = maapi.getNext(c);
                        failed("should have thrown 'secondary index does not exist'");
                    } catch (MaapiException e) {
                    }

                    maapi.finishTrans(th);
                    s.close();
                }
            },

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
            LOGGER.error("failed");
            LOGGER.error("    '"+e.toString()+"'",e);

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

