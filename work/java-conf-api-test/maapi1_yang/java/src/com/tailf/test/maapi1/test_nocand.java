package com.tailf.test.maapi1;


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
import java.math.BigInteger;
import java.util.Arrays;

import com.tailf.conf.*;
import com.tailf.maapi.*;

import com.tailf.test.maapi1.namespaces.*;

public class test_nocand {
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;

    static public void main(String args[]) {
        int i;

        System.err.println("---------------------------------------");
        System.err.println("Java MAAPI tests");
        System.err.println("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * new Maapi
             */
            new TestCase() {
                public void test() throws Exception {
                    init1();
                    close1();
                }
            },

            /************************************************************
             * Test 1
             * startUserSession
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    close2();
                }
            },

            /************************************************************
             * Test 2
             * start user session 2
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    try {
                        maapi.startUserSession("jb",
                                               InetAddress.getLocalHost(),
                                               "maapi", new String[] {"oper"},
                                               MaapiUserSessionFlag.PROTO_TCP);
                        System.err.println("expected error from second call "+
                                           "to StartUserSession()");
                        System.exit(1);
                    } catch (MaapiException e) {
                        if (!e.getMessage().
                            equals("User session already exists on sock")) {
                            failed(e);
                        }
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 3
             * kill user session
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    try {
                        maapi.killUserSession(-1);
                        failed("session should not exist");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("Session not found")) {
                            failed("x"+e.toString());
                        }
                    }
                    close1();
                }
            },

            /************************************************************
             * Test 4
             * get my user session + get user session
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    MaapiUserSession usess = maapi.getUserSession(usid);

                    if (!(usess.getUser().equals("jb") &&
                          usess.getContext().equals("maapi") &&
                          usess.getSessionFlags()
                          .equals(MaapiUserSessionFlag.PROTO_TCP) &&
                          usess.getIPAddress()
                          .equals(InetAddress.getLocalHost()) &&
                          usess.getUserId() == usid)) {
                        failed("non matching usess: "+usess);
                    }

                    usess.toString();
                    close2();
                }
            },

            /************************************************************
             * Test 5
             * get user session 2 (bad uid)
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    try {
                        MaapiUserSession usess2 = maapi.getUserSession(-8);
                        failed("should have thrown a MaapiException:"+usess2);
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("User session not found"))
                            failed("bad exception"+e);
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 6
             * start trans
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int th = maapi.startTrans(Conf.DB_RUNNING,
                                              Conf.MODE_READ);
                    close2();
                }
            },

            /************************************************************
             * Test 7
             * attach + detach
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    Socket s2 = new Socket(host, port);
                    Maapi maapi2 = new Maapi(s2);
                    int usid = maapi.getMyUserSession();
                    maapi2.attach(th, "http://tail-f.com/test/mtest/1.0",
                                  usid);
                    maapi2.detach(th);
                    maapi2.attach(th, "http://tail-f.com/test/mtest/1.0",
                                  usid);
                    maapi2.detach(th);
                    maapi.finishTrans(th);
                    s2.close();
                    close2();
                }
            },

            /************************************************************
             * Test 8
             * attach + detach bad trans id
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th = maapi.startTrans(Conf.DB_RUNNING,
                                              Conf.MODE_READ);

                    try {
                        Socket s2 = new Socket(host, port);
                        Maapi maapi2 = new Maapi(s2);
                        maapi2.attach(-8, -1, usid);
                        maapi2.detach(-8);
                        s2.close();
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No such transaction"))
                            failed("should have thrown a ConfException: "+e);
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 9
             * attach + detach bad usid
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int th = maapi.startTrans(Conf.DB_RUNNING,
                                              Conf.MODE_READ);

                    try {
                        Socket s2 = new Socket(host, port);
                        Maapi maapi2 = new Maapi(s2);
                        maapi2.attach(th, "mtest", -8);
                        maapi2.detach(th);
                        s2.close();
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage()
                            .equals("User sess -8 doesn't exist"))
                            failed("should have thrown a MaapiException: "+e);
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 10
             * detach bad trans
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    try {
                        Socket s2 = new Socket(host, port);
                        Maapi maapi2 = new Maapi(s2);
                        // maapi2.attach(th, mtest.mtest__ns, usid);
                        maapi2.attach(th, "http://tail-f.com/test/mtest/1.0",
                                      usid);
                        maapi2.detach(-5);
                        s2.close();
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No such transaction"))
                            failed("should have thrown a MaapiException: "+e);
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 11
             * stop trans
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);
                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 12
             * apply trans
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    initOper();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.applyTrans(th, true);
                    try {
                        maapi.applyTrans(th, true);
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("badstate"))
                            failed("should have thrown 'badstate'"+e);
                    }
                    try {
                        maapi.validateTrans(th, false, false);
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("badstate"))
                            failed("should have thrown 'badstate'"+e);
                    }
                    try {
                        maapi.prepareTrans(th);
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("badstate"))
                            failed("should have thrown 'badstate'"+e);
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 13
             * validate trans
             */
            new TestCase() {
                public boolean skip() { return true; }
                public void test() throws Exception {

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.delete(th, "/mtest/types/c_ipv4");
                    try {
                        maapi.validateTrans(th, true, true);
                    } catch (MaapiException e) {
                        if (!e.getMessage()
                            .equals("'/mtest:mtest/types/c_ipv4' " +
                                    "is not configured"))
                            failed("should have thrown "+
                                   "'/mtest:mtest/types/c_ipv4' is not "+
                                   "configured: "+
                                   e);
                    }
                    maapi.setElem(th, "1.2.3.4",
                                  "/mtest/types/c_ipv4");

                    maapi.validateTrans(th, true, true);

                    maapi.setElem(th, "200", "/mtest/types/c_int16");

                    try {
                        maapi.validateTrans(th, true, true);
                        failed("should have thrown a validation error");
                    } catch (MaapiWarningException e) {
                        if (e.getWarnings() != null &&
                            e.getWarnings().length == 1 &&
                            e.getWarnings()[0].getPath().toString().
                            equals("/mtest:mtest/types/c_int16") &&
                            e.getWarnings()[0].getMessage().
                            equals("must be less than 100")) {
                            // ok
                        }
                        else {
                            failed("should have caused a validation warning: "+
                                   e.getWarnings()[0].getPath()+" "+
                                   e.getWarnings()[0].getMessage()+" "+
                                   e.getWarnings().length);
                        }
                    }

                    close2();
                }
            },

            /************************************************************
             * Test 14
             * prepare trans
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.delete(th, "/mtest/types/c_ipv4");
                    maapi.setElem(th, "1.2.3.4", "/mtest/types/c_ipv4");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 15
             * commit
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.delete(th, "/mtest/types/c_ipv4");
                    maapi.setElem(th, "1.2.3.4",
                                  "/mtest/types/c_ipv4");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 16
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    try {
                        maapi.setNamespace(th, "foobar");
                        failed("should have thrown an exception");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No such namespace"))
                            failed("Should have thrown a 'No such namespace' "+
                                   "exception");
                    }

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 17
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.cd(th, "/mtest/servers/server{%s}/foo",
                             new Object[] {
                                 new String("www")
                             });
                    maapi.cd(th, "/mtest/servers/server{%x}/foo",
                             new Object[] {
                                 new ConfBuf(new String("www").getBytes())
                             });
                    try {
                        maapi.cd(th, "/mtest/servers/server{%d}/foo",
                                 new Object[] {
                                     new ConfBuf(new String("www").getBytes())
                                 });
                        failed("should have thrown an exception");
                    } catch (ConfException e) {
                        ;
                    }
                    maapi.cd(th, "/mtest/servers/server{x%d}/foo",
                             new Object[] {
                                 new Integer(100)
                             });
                    maapi.cd(th, "/mtest/servers/server{www}/foo");
                    maapi.cd(th, "../interface{eth0}");
                    maapi.cd(th, "/mtest/servers/server");
                    maapi.cd(th, "{smtp}");
                    maapi.cd(th, "../../dks");
                    maapi.cd(th, "..");
                    maapi.cd(th, "..");
                    try {
                        maapi.cd(th, "..");
                        failed("should throw cd exception");
                    } catch (Exception e) {
                        if (!e.getMessage().equals("bad '..' path: /"))
                            failed("should have thrown a 'bad .. path'"+
                                   "MaapiException. Got: "+e.getMessage());
                    }

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 18
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.cd(th, "/mtest/servers/server{www}/foo");
                    maapi.pushd(th, "/mtest/servers/server{smtp}/foo");
                    maapi.pushd(th, "/mtest");

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 19
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.cd(th, "/mtest/servers/server{www}/foo");
                    maapi.pushd(th, "/mtest/servers/server{smtp}/foo");
                    maapi.pushd(th, "/mtest");
                    maapi.popd(th);
                    maapi.popd(th);

                    try {
                        maapi.popd(th);
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("stack is empty"))
                           failed("should have thrown an empty MaapiException");
                    }

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 20
             */
            new TestCase() {
                public void test() throws Exception {
                    boolean b;

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.cd(th, "/mtest/servers/server{www}/foo");
                    b = maapi.exists(th, "/mtest/servers/server{smtp}");
                    if (!b)
                        failed("1 - should have been true");
                    b = maapi.exists(th, "../../server{smtpx}");
                    if (b)
                        failed("2 - should have been false");
                    b = maapi.exists(th, "../../server{%x}", new Object[] {
                        new String("smtp")
                    });
                    if (!b)
                        failed("3 - should have been true");

                    try {
                        b = maapi.exists(th, "../../server{smtp}/x");
                    } catch (MaapiException e) {
                        /* ok */
                    }

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 21
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ);
                    ConfObject r;

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    System.err.println("");

                    r = maapi.getElem(th, "/mtest/types/c_int8");
                    if (((ConfInt8) r).intValue() != 77)
                        failed("c_int8 expected 77 got "+r);
                    System.err.println("  c_int8: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_int16");
                    if (((ConfInt16) r).intValue() != 77)
                        failed("c_int16 expected 77 got "+r);
                    System.err.println("  c_int16: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_int32");
                    if (((ConfInt32) r).intValue() != 77)
                        failed("c_int32 expected 77 got "+r);
                    System.err.println("  c_int32: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_int64");
                    if (((ConfInt64) r).longValue() != 77)
                        failed("c_int64 expected 77 got "+r);
                    System.err.println("  c_int64: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_uint8");
                    if (((ConfUInt8) r).longValue() != 77)
                        failed("c_uint8 expected 77 got "+r);
                    System.err.println("  c_uint8: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_uint16");
                    if (((ConfUInt16) r).longValue() != 77)
                        failed("c_uint16 expected 77 got "+r);
                    System.err.println("  c_uint16: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_uint32");
                    if (((ConfUInt32) r).longValue() != 77)
                        failed("c_uint32 expected 77 got "+r);
                    System.err.println("  c_uint32: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_uint64");
                    if ( !((ConfUInt64) r).bigIntegerValue()
                        .equals(
                                new BigInteger("77")))
                        failed("c_uint64 expected 77 got "+r);
                    System.err.println("  c_uint64: "+r);

                    r = maapi.getElem(th, "/mtest/types/b");
                    if (((ConfBool) r).booleanValue() != true)
                        failed("c_uint64 expected true got "+r);
                    System.err.println("  b: "+r);

                    r = maapi.getElem(th, "/mtest/types/f");
                    if (((ConfDouble) r).doubleValue() != 4.66)
                        failed("f expected 4.66 got "+r);
                    System.err.println("  f: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_ipv4");
                    if (!(((ConfIPv4) r).getAddress().
                          equals(InetAddress.
                                 getByAddress(new byte[] {1,2,3,4}))))
                        failed("c_ipv4  expected 1.2.3.4 got "+r);
                    System.err.println("  c_ipv4: "+r);

                    r = maapi.getElem(th, "/mtest/types/c_ipv6");
                    if (!(((ConfIPv6) r).getAddress().
                          equals(InetAddress.
                                 getByAddress(new byte[]
                                     {(byte)254, (byte)128, 0, 0, 0,
                                      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}))))
                        failed("c_ipv6  expected fe80:: got "+r);
                    System.err.println("  c_ipv6: "+r);

                    r = maapi.getElem(th, "/mtest/types/datetime");
                    System.err.println("  datetime: "+r);

                    r = maapi.getElem(th, "/mtest/types/date");
                    System.err.println("  date: "+r);

                    r = maapi.getElem(th, "/mtest/types/gyearmonth");
                    System.err.println("  gyearmonth: "+r);

                    r = maapi.getElem(th, "/mtest/types/gyear");
                    System.err.println("  gyear: "+r);

                    r = maapi.getElem(th, "/mtest/types/time");
                    System.err.println("  time: "+r);

                    r = maapi.getElem(th, "/mtest/types/gday");
                    System.err.println("  gday: "+r);

                    r = maapi.getElem(th, "/mtest/types/gmonthday");
                    System.err.println("  gmonthday: "+r);

                    r = maapi.getElem(th, "/mtest/types/gmonth");
                    System.err.println("  gmonth: "+r);

                    r = maapi.getElem(th, "/mtest/types/duration");
                    System.err.println("  duration: "+r);

                    maapi.finishTrans(th);
                    close2();
                }

            },

            /************************************************************
             * Test 22
             */
            new TestCase() {
                public void test() throws Exception {

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.setElem(th, new ConfIPv4(127,0,0,1),
                                  "/mtest/types/c_ipv4");

                    maapi.setElem(th, "1.2.3.4",
                                  "/mtest/types/c_ipv4");

                    // maapi.applyTrans(th, true);
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 23
             * delete
             */

            new TestCase() {
                public void test() throws Exception {

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    maapi.delete(th, "/mtest/types/c_ipv4");

                    try {
                        // maapi.applyTrans(th, true);
                        maapi.validateTrans(th, false, true);
                        maapi.prepareTrans(th);
                        maapi.commitTrans(th);
                        failed("should have thrown MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage()
                            .equals("/mtest:mtest/types/c_ipv4 is not "  +
                                    "configured")) {
                            failed("should have thrown 'is not configured'  "+
                                   "MaapiException: "+e);
                        }
                    }

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 24
             * insert
             */

            new TestCase() {
                public void test() throws Exception {
                    boolean b;

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    b = maapi.exists(th, "/mtest/indexes/index{3}");
                    if (b == true)
                        failed("/mtest/indexes/index{3} should not exist");
                    maapi.insert(th, "/mtest/indexes/index{1}");
                    b = maapi.exists(th, "/mtest/indexes/index{3}");
                    if (b == false)
                        failed("/mtest/indexes/index{3} should now exist");

                    b = maapi.exists(th, "/mtest/indexes/index{20}");
                    if (b == true)
                        failed("/mtest/indexes/index{20} should not exist");
                    maapi.insert(th, "/mtest/indexes/index{20}");
                    b = maapi.exists(th, "/mtest/indexes/index{20}");
                    if (b == false)
                        failed("/mtest/indexes/index{20} should now exist");

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 25
             * move
             */

            new TestCase() {
                public void test() throws Exception {
                    boolean b;

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    b = maapi.exists(th, "/mtest/indexes/index{4}");
                    if (b == true)
                        failed("/mtest/indexes/index{3} should not exist");

                    maapi.move(th, new ConfKey(new ConfObject[] {
                        new ConfUInt16(4)}),
                               "/mtest/indexes/index{2}");

                    b = maapi.exists(th, "/mtest/indexes/index{4}");
                    if (b == false)
                        failed("/mtest/indexes/index{4} should now exist");

                    maapi.move(th, "5", "/mtest/indexes/index{4}");

                    b = maapi.exists(th, "/mtest/indexes/index{4}");
                    if (b == true)
                        failed("/mtest/indexes/index{4} should not exist");

                    b = maapi.exists(th, "/mtest/indexes/index{5}");
                    if (b == false)
                        failed("/mtest/indexes/index{5} should now exist");

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 26
             * lock & unlock
             */

            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    maapi.lock(Conf.DB_CANDIDATE);
                    try {
                        Socket s2 = new Socket(host, port);
                        Maapi maapi2 = new Maapi(s2);
                        maapi2.lock(Conf.DB_CANDIDATE);
                        s2.close();
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No user session"))
                            failed("should have thrown a MaapiException: "+e);
                    }
                    try {
                        Socket s2 = new Socket(host, port);
                        Maapi maapi2 = new Maapi(s2);
                        maapi2.startUserSession("jb",
                                               InetAddress.getLocalHost(),
                                               "maapi", new String[] {"oper"},
                                               MaapiUserSessionFlag.PROTO_TCP);
                        maapi2.lock(Conf.DB_CANDIDATE);
                        s2.close();
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage().startsWith("the configuration " +
                                                       "database is locked by "
                                                       + " session "))
                            failed("should have thrown a MaapiException: "+e);
                    }
                    maapi.unlock(Conf.DB_CANDIDATE);
                    try {
                        maapi.unlock(Conf.DB_CANDIDATE);
                        failed("should have thrown a MaapiException");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("external"))
                            failed("should have thrown a MaapiException: "+e);
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 27
             * is_lock_set
             */

            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    maapi.lock(Conf.DB_CANDIDATE);

                    Socket s2 = new Socket(host, port);
                    Maapi maapi2 = new Maapi(s2);
                    int b;

                    b = maapi2.isLockSet(Conf.DB_CANDIDATE);
                    if (b == 0) failed("candidate lock should have been set");

                    maapi.unlock(Conf.DB_CANDIDATE);
                    b = maapi2.isLockSet(Conf.DB_CANDIDATE);
                    if (b > 0) failed("candidate lock should have been set");

                    s2.close();
                    close2();
                }
            },


            /************************************************************
             * Test 28
             * candidate_validate
             */

            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    try {
                        maapi.candidateValidate();
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 29
             * delete_config
             */

            new TestCase() {
                public void test() throws Exception {
                    System.out.println("initAdmin()");
                    initAdmin();
                    System.out.println("maapi.deleteConfig()");
                    try {
                        maapi.deleteConfig(Conf.DB_CANDIDATE);
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 30
             * candidate_commit
             */

            new TestCase() {
                public void test() throws Exception {
                    try {
                        initOper();
                        maapi.candidateCommit();
                        close2();
                        failed("should have thrown an No candiate exception.");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                }
            },

            /************************************************************
             * Test 31
             * candidate_confirmed_commit
             */

            new TestCase() {
                public void test() throws Exception {
                    try {
                        initOper();
                        maapi.candidateConfirmedCommit(20);
                        maapi.candidateCommit();
                        close2();
                        failed("should have thrown an access denied error.");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                }
            },

            /************************************************************
             * Test 32
             * candidate_reset
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    try {
                        maapi.candidateReset();
                        failed("should have thrown No candidate");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 33
             * candidate abort commit
             */
            new TestCase() {
                public void test() throws Exception {
                    initAdmin();
                    try {
                        maapi.candidateAbortCommit();
                        failed("should have thrown No candidate");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                    close2();
                }
            },

            /************************************************************
             * Test 34
             * confirmed commit in progress
             */
            new TestCase() {
                public void test() throws Exception {
                    initAdmin();
                    int usid = maapi.getMyUserSession();
                    try {
                        int b = maapi.confirmedCommitInProgress();
                        failed("should have thrown No candidate");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("should have thrown No candidate");
                    }
                    close2();
                }
            },


            /************************************************************
             * Test 35
             * copy running to startup
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    initOper();
                    maapi.copyRunningToStartup();
                    close2();
                }
            },

            /************************************************************
             * Test 36
             * copy
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    try {
                        initOper();
                        int th1 = maapi.startTrans(Conf.DB_RUNNING,
                                                   Conf.MODE_READ);
                        int th2 = maapi.startTrans(Conf.DB_CANDIDATE,
                                                   Conf.MODE_READ_WRITE);
                        maapi.copy(th1, th2);
                        maapi.applyTrans(th2, false);
                        close2();
                        failed("should have thrown No candidate");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("1 expected No candidate" + e);
                    }
                    try {
                        initAdmin();
                        int th1 = maapi.startTrans(Conf.DB_RUNNING,
                                                   Conf.MODE_READ);
                        int th2 = maapi.startTrans(Conf.DB_CANDIDATE,
                                                   Conf.MODE_READ_WRITE);
                        maapi.copy(th1, th2);
                        maapi.applyTrans(th2, false);
                        close2();
                        failed("should have thrown No candidate");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No candidate"))
                            failed("2 expected No candidate" + e);
                    }
                }
            },

            /************************************************************
             * Test 37
             * authenticate
             */
            new TestCase() {
                public void test() throws Exception {
                    MaapiAuthentication x;
                    initOper();
                    x = maapi.authenticate("oper","kaka");
                    if (x.isValid())
                        failed("authentication should have failed");
                    x = maapi.authenticate("oper","oper");
                    if (!x.isValid())
                        failed("authentication should have succeeded");
                    if (x.getGroups().length != 1 ||
                        !x.getGroups()[0].equals("oper"))
                        failed("group information does not match" +
                               x.getGroups()[0]);
                    close2();
                }
            },

            /************************************************************
             * Test 38
             * get running db status
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    initOper();
                    int s = maapi.getRunningDbStatus();
                    if (s == 0)
                        failed("db status should be 1 (valid)");
                    close2();
                }
            },

            /************************************************************
             * Test 39
             * set running db satus
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    int s;
                    initOper();
                    maapi.setRunningDbStatus(0);
                    s = maapi.getRunningDbStatus();
                    if (s == 1)
                        failed("db status should be 0 (invalid)");
                    maapi.setRunningDbStatus(1);
                    s = maapi.getRunningDbStatus();
                    if (s == 0)
                        failed("db status should be 1 (valid)");
                    close2();
                }
            },

            /************************************************************
             * Test 40
             * get next
             */

            new TestCase() {
                public void test() throws Exception {
                    ConfObject x;

                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    MaapiCursor c = maapi.newCursor(th,
                                                    "/mtest/indexes/index");

                    x = maapi.getNext(c);

                    if (!x.toString().equals("{1}"))
                        failed("should have been {1}"+x);

                    x = maapi.getNext(c);
                    if (!x.toString().equals("{2}"))
                        failed("should have been {2}"+x);

                    x = maapi.getNext(c);
                    if (!x.toString().equals("{8}"))
                        failed("should have been {8}"+x);

                    x = maapi.getNext(c);
                    if (x != null)
                        failed("should have been null");

                    maapi.finishTrans(th);
                    close2();
                }
            },

            /************************************************************
             * Test 41
             * list rollback
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    MaapiRollback[] r = maapi.listRollback();
                    if (r[0].getNR() != 0 || !r[0].getVia().equals("system"))
                        failed("rollback[0] should be nr 0");

                    close2();
                }
            },

            /************************************************************
             * Test 42
             * diff iterate
             */
            new TestCase() {

                private boolean traversed_c_int8 = false;
                private boolean traversed_port = false;

                // Override the Default iterate function in the TestCase class
                public DiffIterateResultFlag iterate(ConfObject[] kp,
                                                     DiffIterateOperFlag op,
                                                     ConfObject oldValue,
                                                     ConfObject newValue)
                {
                    if (op != DiffIterateOperFlag.MOP_VALUE_SET) {
                        return DiffIterateResultFlag.ITER_RECURSE;
                    }
                    if (kp[0].toString().equals("mtest:c_int8")) {
                        traversed_c_int8 = true;
                        if (!newValue.toString().equals("42")) {
                            failed("c_int8 expected 42 got "+
                                   newValue.toString());
                        }
                        System.out.println("kp    :" + Arrays.toString(kp));
                        System.out.println("newval: " + newValue);
                }
                if (kp[0].toString().equals("mtest:port")) {
                        traversed_port = true;
                        if (!newValue.toString().equals("8080")) {
                                failed("port expected 42 got "+
                                       newValue.toString());
                        }
                        System.out.println("kp    :" + Arrays.toString(kp));
                        System.out.println("newval: " + newValue);
                }

                return DiffIterateResultFlag.ITER_RECURSE;

            }

                public void test() throws Exception {

                    initOper();
                    // maapi.debugLevel = Conf.DEBUG_TRACE;

                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    ConfObject r;

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    System.err.println("");

                    // read value: 77
                    r = maapi.getElem(th, "/mtest/types/c_int8");
                    if (((ConfInt8) r).intValue() != 77)
                        failed("c_int8 expected 77 got "+r);
                    // set value to: 42
                    maapi.setElem(th, new ConfInt8(42), "/mtest/types/c_int8");

                    r = maapi.getElem(th, "/mtest/servers/server{www}/port");
                    if (((ConfUInt16) r).longValue() != 80)
                                failed("server www port expected 80 got "+r);
                            // set value to: 42
                            maapi.setElem(th, new ConfUInt16(8080),
                                          "/mtest/servers/server{www}/port");

                    // DiffIterate
                    maapi.diffIterate(th, (MaapiDiffIterate) this);
                    if (!traversed_c_int8 || !traversed_port) {
                        failed ("did not traverse complete diff set");
                    }
                    traversed_c_int8 = false;
                    traversed_port = false;
                    maapi.diffIterate(th, (MaapiDiffIterate) this,
                                      "/mtest/types");
                    if (!traversed_c_int8 || traversed_port) {
                        failed ("did not traverse complete diff set");
                    }


                    System.err.println("done diffIterate!");

                    maapi.finishTrans(th);
                    close2();
                }
            }

        };

        /************************************************************
         * Run the tests
         */

        int from = 0;
        int to   = tests.length;

        // from = 36;
        // to = 37;

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
}

