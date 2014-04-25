package com.tailf.test.dp6;
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
import com.tailf.conf.ConfHexList;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfOID;
import com.tailf.conf.ConfOctetList;
import com.tailf.conf.ConfUInt16;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.test.dp6.namespaces.*;

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
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");
                    maapi.create(th,"/servers/server{www}");
                    maapi.setElem(th, new ConfUInt16(10000),
                                  "/servers/server{www}/port");
                    maapi.create(th,"/servers/server{ssh}");
                    maapi.setElem(th, new ConfUInt16(10001),
                                  "/servers/server{ssh}/port");
                    maapi.delete(th, "/servers/server{www}");
                    maapi.setElem(th, new ConfIPv4(192,168,0,1),
                                  "/servers/server{ssh}/ip");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.abortTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 1
             * commit trans
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
                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

                    maapi.create(th,"/servers/server{www}");
                    maapi.setElem(th, new ConfUInt16(10000), "/servers/server{www}/port");
                    maapi.create(th,"/servers/server{ssh}");
                    maapi.setElem(th, new ConfUInt16(10001), "/servers/server{ssh}/port");
                    maapi.delete(th, "/servers/server{www}");
                    maapi.setElem(th, new ConfIPv4(192,168,0,1), "/servers/server{ssh}/ip");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },


            /************************************************************
             * Test 2
             * commit trans
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
                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

                    maapi.setElem(th, new ConfIPv4(192,168,0,2),
                                  "/servers/server{ssh}/ip");

                    maapi.setElem(th, new ConfUInt16(6666),
                                  "/servers/server{ssh}/port");
                    ConfHexList macaddr= new ConfHexList("4f:4c:41:00:00:01");
                    LOGGER.info("MacAddr= "+macaddr);
                    maapi.setElem(th, macaddr,
                                  "/servers/server{ssh}/macaddr");
                    ConfOID oid = new ConfOID("1.1.6.1.24961.1.0");
                    LOGGER.debug("Oid= "+oid);
                    maapi.setElem(th, oid, "/servers/server{ssh}/snmpref");

                    ConfOctetList prefixmask =
                        new ConfOctetList("255.255.224.0");

                    LOGGER.debug("OctetList= "+prefixmask);
                    maapi.setElem(th, prefixmask,
                                  "/servers/server{ssh}/prefixmask");

                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
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
        LOGGER.info("Test "+test.testnr+": ");
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
            LOGGER.error("failed");
            LOGGER.error("    '"+reason+"'");
        }
    }
}

