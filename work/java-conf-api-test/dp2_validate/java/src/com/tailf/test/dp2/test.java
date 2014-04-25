package com.tailf.test.dp2;

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

import java.util.Arrays;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfUInt16;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.MaapiWarningException;

import com.tailf.test.dp2.namespaces.smp;
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
        LOGGER.info("Java DP Validation tests");
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
                    maapi.delete(th, "/servers/server{www}");
                    maapi.setElem(th, new ConfIPv4(193,168,0,1),
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

                    maapi.delete(th, "/servers/server{www}");
                    maapi.setElem(th, new ConfIPv4(193,168,0,1),
                                  "/servers/server{ssh}/ip");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 2
             * create (validation point is called)
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

                    maapi.create(th, "/servers/server{www}");
                    maapi.setElem(th, new ConfIPv4(193,168,0,1),
                                  "/servers/server{www}/ip");
                    maapi.setElem(th, new ConfUInt16(88),
                                  "/servers/server{www}/port");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.endUserSession();

                    s.close();
                }
            },


            /************************************************************
             * Test 3
             * create (validation error, same port)
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

                    maapi.create(th, "/servers/server{ftp}");
                    maapi.setElem(th, new ConfIPv4(193,168,0,1),
                                  "/servers/server{ftp}/ip");
                    maapi.setElem(th, new ConfUInt16(88),
                                  "/servers/server{ftp}/port");
                    try {
                        maapi.validateTrans(th, false, true);
                        failed("should have thrown MaapiException: validation failed");
                    } catch (MaapiException e) {
                        /* ok */
                    }
                    maapi.setElem(th, new ConfUInt16(89),
                                  "/servers/server{ftp}/port");
                    /* now the validation should work fine */
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 4
             * create (validation warning,
             *IP is in local subnet 192.168. / 10.0.0 subnet)
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

                    maapi.create(th, "/servers/server{local}");
                    maapi.setElem(th, new ConfIPv4(192,168,0,1),
                                  "/servers/server{local}/ip");
                    maapi.setElem(th, new ConfUInt16(90),
                                  "/servers/server{local}/port");
                    try {
                        maapi.validateTrans(th, false, true);
                        failed("should have thrown MaapiException");
                    } catch (MaapiException e) {
                        /* ok */
                    }
                    maapi.setElem(th, new ConfIPv4(193,168,0,1),
                                  "/servers/server{local}/ip");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            }





        };



        // /************************************************************
        //  * Run the tests
        //  */

        // int from = 0;
        // int to   = tests.length;

        // // from = 21;
        // // to = 22;

        // test.testnr = from;

        // for(i = from ; i < to ; i++)
        //     tests[i].doit();

        int from = 0;
        int to   = tests.length;

        if(args.length > 0){
            String sfrom = args[0];

            try{
                from = Integer.parseInt(sfrom);
            }catch(NumberFormatException e){
                //ignore

            }

        }


        if(args.length > 1){
            String sto = args[1];

            try{
                to = Integer.parseInt(sto);
            }catch(NumberFormatException e){
                //ignore
            }

        }


        for(i = from ; i < to ; i++)
            tests[i].doit();


        //test.testnr = from;
        // /************************************************************
        //  * Summary
        //  */
        // LOGGER.info("---------------------------------------");
        // LOGGER.info("  Passed:  " + test.pass);
        // LOGGER.info("  Failed:  " + test.fail);
        // LOGGER.info("  Skipped: " + test.skip);
        // LOGGER.info("---------------------------------------");
        // if (test.fail == 0)
        //     System.exit(0);
        // else
        //     System.exit(1);

         /************************************************************
         * Summary
         */
        LOGGER.info("---------------------------------------");
        LOGGER.info("  Passed:  " + test.pass);
        LOGGER.info("  Failed:  " + test.fail);
        if(test.fail > 0)
            LOGGER.info("TEST FAILED:" +
                        Arrays.toString(TestCase.failedtests.toArray()));
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

