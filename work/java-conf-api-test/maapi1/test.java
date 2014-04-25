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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.tailf.conf.*;
import com.tailf.maapi.*;

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

        //LogConfig.setDefault();
        LOGGER.info("---------------------------------------");
        LOGGER.info("Java MAAPI tests");
        LOGGER.info("---------------------------------------");

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
                        LOGGER.info("expected error from second call "+
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
                          usess.getSessionFlags().equals(MaapiUserSessionFlag.PROTO_TCP) &&
                          usess.getIPAddress().equals(InetAddress.getLocalHost()) &&
                          usess.getUserId() == usid)) {
                        failed("non matching usess: "+usess);
                    }

                    if (!usess.getSnmpV3Context().equals(""))
                        failed("snmp v3 context is non default: "+
                               usess.getSnmpV3Context());

                    usess.toString();

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
             * Test 5
             * get user sessions
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    int usid = maapi.getMyUserSession();
                    int[] who = maapi.getUserSessions();
                    int i;
                    boolean ok = false;

                    for(i=0; i < who.length ; i++) {
                        if (who[i] == usid)
                            ok = true;
                    }
                    if (!ok)
                        failed("did not find own session in session list");
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
                            failed("should have thrown a MaapiException: "+e);
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
                        if (!e.getMessage().equals("User sess -8 doesn't exist"))
                            failed("should have thrown a MaapiException: "+e.getMessage());
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
                            failed("should have thrown 'bad state'"+e);
                    }
                    try {
                        maapi.validateTrans(th, false, false);
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("badstate"))
                            failed("should have thrown 'bad state'"+e);
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
                        if (!e.getMessage().equals("'/mtest:mtest/types/c_ipv4' is not configured"))
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
                        if (e.warnings != null &&
                            e.warnings.length == 1 &&
                            e.warnings[0].path.toString().
                            equals("/mtest:mtest/types/c_int16") &&
                            e.warnings[0].message.
                            equals("must be less than 100")) {
                            // ok
                        }
                        else {
                            failed("should have caused a validation warning: "+
                                   e.warnings[0].path+" "+
                                   e.warnings[0].message+" "+
                                   e.warnings.length);
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
                /* skip for now same test as in maapi1_yang */
                 public boolean skip() { return true; }

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
                        // LOGGER.info("got exception: "+ e.getMessage());
                        if (!e.getMessage().equals("bad '..' path: /"))
                            failed("should have thrown a 'bad .. path'" +
                                   "MaapiException. Got: " + e.getMessage());
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
                    if(maapi.getSchemas() == null)
                        maapi.loadSchemas();
                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    ConfObject r, r2;

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");
                    maapi.addNamespace(new mtest());

                    LOGGER.info("");

                    r = maapi.getElem(th, "/mtest/types/c_int8");
                    if (((ConfInt8) r).intValue() != 77)
                        failed("c_int8 expected 77 got "+r);

                    LOGGER.info("  c_int8: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_int8");
                    r2 = maapi.getElem(th, "/mtest/types/c_int8");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_int8");

                    /* c_int16  */
                    r = maapi.getElem(th, "/mtest/types/c_int16");
                    if (((ConfInt16) r).intValue() != 77)
                        failed("c_int16 expected 77 got "+r);
                    LOGGER.info("  c_int16: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_int16");
                    r2 = maapi.getElem(th, "/mtest/types/c_int16");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_int16");

                    /* c_int32  */
                    r = maapi.getElem(th, "/mtest/types/c_int32");
                    if (((ConfInt32) r).intValue() != 77)
                        failed("c_int32 expected 77 got "+r);
                    LOGGER.info("  c_int32: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_int32");
                    r2 = maapi.getElem(th, "/mtest/types/c_int32");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_int32");

                    /* c_int64  */
                    r = maapi.getElem(th, "/mtest/types/c_int64");
                    if (((ConfInt64) r).longValue() != 77)
                        failed("c_int64 expected 77 got "+r);
                    LOGGER.info("  c_int64: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_int64");
                    r2 = maapi.getElem(th, "/mtest/types/c_int64");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_int64");

                    /* c_uint8  */
                    r = maapi.getElem(th, "/mtest/types/c_uint8");
                    if (((ConfUInt8) r).intValue() != 77)
                        failed("c_uint8 expected 77 got "+r);
                    LOGGER.info("  c_uint8: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_uint8");
                    r2 = maapi.getElem(th, "/mtest/types/c_uint8");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_uint8");

                    r = maapi.getElem(th, "/mtest/types/c_uint16");
                    if (((ConfUInt16) r).intValue() != 77)
                        failed("c_uint16 expected 77 got "+r);
                    LOGGER.info("  c_uint16: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_uint16");
                    r2 = maapi.getElem(th, "/mtest/types/c_uint16");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_uint16");

                    r = maapi.getElem(th, "/mtest/types/c_uint32");
                    if (((ConfUInt32) r).intValue() != 77)
                        failed("c_uint32 expected 77 got "+r);
                    LOGGER.info("  c_uint32: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_uint32");
                    r2 = maapi.getElem(th, "/mtest/types/c_uint32");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_uint32");

                    r = maapi.getElem(th, "/mtest/types/c_uint64");
                    if (((ConfUInt64) r).intValue().intValue() != 77)
                        failed("c_uint64 expected 77 got "+r);
                    LOGGER.info("  c_uint64: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_uint64");
                    r2 = maapi.getElem(th, "/mtest/types/c_uint64");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_uint64");

                    r = maapi.getElem(th, "/mtest/types/b");
                    if (((ConfBool) r).booleanValue() != true)
                        failed("c_uint64 expected true got "+r);
                    LOGGER.info("  b: "+r);
                    maapi.setElem(th, r, "/mtest/types/b");
                    r2 = maapi.getElem(th, "/mtest/types/b");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/b");

                    r = maapi.getElem(th, "/mtest/types/f");
                    if (((ConfDouble) r).doubleValue() != 4.66)
                        failed("f expected 4.66 got "+r);
                    LOGGER.info("  f: "+r);
                    maapi.setElem(th, r, "/mtest/types/f");
                    r2 = maapi.getElem(th, "/mtest/types/f");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/f");



                    r = maapi.getElem(th, "/mtest/types/c_ipv4");
                    if (!(((ConfIPv4) r).getAddress().
                          equals(InetAddress.
                                 getByAddress(new byte[] {1,2,3,4}))))
                        failed("c_ipv4  expected 1.2.3.4 got "+r);
                    LOGGER.info("  c_ipv4: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_ipv4");
                    r2 = maapi.getElem(th, "/mtest/types/c_ipv4");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_ipv4");



                    r = maapi.getElem(th, "/mtest/types/c_ipv6");
                    if (!(((ConfIPv6) r).getAddress().
                          equals(InetAddress.
                                 getByAddress(new byte[]
                                     {(byte)254, (byte)128, 0, 0, 0,
                                      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}))))
                        failed("c_ipv6  expected fe80:: got "+r);

                    LOGGER.info("  c_ipv6: "+r);
                    maapi.setElem(th, r, "/mtest/types/c_ipv6");
                    r2 = maapi.getElem(th, "/mtest/types/c_ipv6");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/c_ipv6");

                    r = maapi.getElem(th, "/mtest/types/datetime");
                    LOGGER.info("  datetime: "+r);
                    maapi.setElem(th, r, "/mtest/types/datetime");
                    r2 = maapi.getElem(th, "/mtest/types/datetime");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/datetime");

                    r = maapi.getElem(th, "/mtest/types/date");
                    LOGGER.info("  date: "+r);
                    maapi.setElem(th, r, "/mtest/types/date");
                    r2 = maapi.getElem(th, "/mtest/types/date");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/date");

                    // r = maapi.getElem(th, "/mtest/types/gyearmonth");
                    // LOGGER.info("  gyearmonth: "+r);
                    // maapi.setElem(th, r, "/mtest/types/gyearmonth");
                    // r2 = maapi.getElem(th, "/mtest/types/gyearmonth");
                    // if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                    //     failed("could not write back /mtest/types/gyearmonth");

                    // r = maapi.getElem(th, "/mtest/types/gyear");
                    // LOGGER.info("  gyear: "+r);
                    // maapi.setElem(th, r, "/mtest/types/gyear");
                    // r2 = maapi.getElem(th, "/mtest/types/gyear");
                    // if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                    //     failed("could not write back /mtest/types/gyear");

                    r = maapi.getElem(th, "/mtest/types/time");
                    LOGGER.info("  time: "+r);
                    maapi.setElem(th, r, "/mtest/types/time");
                    r2 = maapi.getElem(th, "/mtest/types/time");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/time");

                    // r = maapi.getElem(th, "/mtest/types/gday");
                    // LOGGER.info("  gday: "+r);
                    // maapi.setElem(th, r, "/mtest/types/gday");
                    // r2 = maapi.getElem(th, "/mtest/types/gday");
                    // if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                    //     failed("could not write back /mtest/types/gday");

                    // r = maapi.getElem(th, "/mtest/types/gmonthday");
                    // LOGGER.info("  gmonthday: "+r);
                    // maapi.setElem(th, r, "/mtest/types/gmonthday");
                    // r2 = maapi.getElem(th, "/mtest/types/gmonthday");
                    // if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                    //     failed("could not write back /mtest/types/gmonthday");

                    // r = maapi.getElem(th, "/mtest/types/gmonth");
                    // LOGGER.info("  gmonth: "+r);
                    // maapi.setElem(th, r, "/mtest/types/gmonth");
                    // r2 = maapi.getElem(th, "/mtest/types/gmonth");
                    // if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                    //     failed("could not write back /mtest/types/gmonth");


                    r = maapi.getElem(th, "/mtest/types/duration");
                    LOGGER.info("  duration: "+r);
                    maapi.setElem(th, r, "/mtest/types/duration");
                    r2 = maapi.getElem(th, "/mtest/types/duration");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/duration");

                    r = maapi.getElem(th, "/mtest/types/enum");
                    LOGGER.info("  enum: "+r);
                    maapi.setElem(th, r, "/mtest/types/enum");
                    r2 = maapi.getElem(th, "/mtest/types/enum");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/enum");


                    r = maapi.getElem(th, "/mtest/types/objectref");
                    LOGGER.info("  objectref: "+r);

                    //this uses hkeypath so wont work ..need to fix
                    //maapi.setElem(th, r, "/mtest/types/objectref");
                    LOGGER.info("before setElem()");
                    /*
                    maapi.setElem(th,
                                  "/mtest/servers/server[srv-name=\"smtp\"]/ip",
                                  "/mtest/types/objectref");
                    */
                    LOGGER.info("after setElem()");

                    r2 = maapi.getElem(th, "/mtest/types/objectref");

                    // if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                    //     failed("could not write back /mtest/types/objectref");

                    r = maapi.getElem(th, "/mtest/types/ipv4Prefix");
                    LOGGER.info("  ipv4Prefix: "+r);
                    maapi.setElem(th, r, "/mtest/types/ipv4Prefix");
                    r2 = maapi.getElem(th, "/mtest/types/ipv4Prefix");

                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/ipv4Prefix");

                    r = maapi.getElem(th, "/mtest/types/ipv6Prefix");
                    LOGGER.info("  ipv6Prefix: "+r);
                    maapi.setElem(th, r, "/mtest/types/ipv6Prefix");
                    r2 = maapi.getElem(th, "/mtest/types/ipv6Prefix");
                    if (!r.equals(r2) && !(r.hashCode() == r2.hashCode()))
                        failed("could not write back /mtest/types/ipv6Prefix");

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
                        if (!e.getMessage().equals("/mtest:mtest/types/c_ipv4 is not configured")) {
                            failed("should have thrown 'is not configured' MaapiException: "+e);
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

                    maapi.move(th, "3 4", "/mtest/movables/movable{1 2}");

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
                        if (!e.getMessage().startsWith("the configuration database is locked by session "))
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
                    if (b != 0) failed("candidate lock should have been set");

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
                    maapi.candidateReset();
                    maapi.candidateValidate();
                    close2();
                }
            },

            /************************************************************
             * Test 29
             * delete_config
             */

            new TestCase() {
                public void test() throws Exception {
                    boolean b;
                    int th;
                    initAdmin();
                    maapi.deleteConfig(Conf.DB_CANDIDATE);
                    th = maapi.startTrans(Conf.DB_CANDIDATE,
                                          Conf.MODE_READ);
                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");
                    b = maapi.exists(th, "/mtest/indexes/index{1}");
                    if (b)
                        failed("configuration should be deleted");
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_CANDIDATE,
                                            Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");
                    maapi.loadRollback(th, 1);
                    b = maapi.exists(th, "/mtest/indexes/index{1}");
                    if (!b)
                        failed("configuration should now exist again (1)");
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.finishTrans(th);

                    th = maapi.startTrans(Conf.DB_CANDIDATE,
                                                          Conf.MODE_READ);
                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");
                    b = maapi.exists(th, "/mtest/indexes/index{1}");
                    if (!b)
                        failed("configuration should now exist again (2)");
                    maapi.finishTrans(th);

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
                        failed("should have thrown an access denied error.");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("access denied"))
                            failed("should have thrown access denied");
                    }
                    initAdmin();
                    maapi.candidateCommit();
                    close2();
                }
            },

            /************************************************************
             * Test 31
             * candidate_confirmed_commit
             */

            new TestCase() {
                public void test() throws Exception {
                    // Temporary fix.
                    // This doesn't work anymore.
                    //     try {
                    //  initOper();
                    //  maapi.candidateConfirmedCommit(20);
                    //  maapi.candidateCommit();
                    //  close2();
                    //  failed("should have thrown an access denied error.");
                    //     } catch (MaapiException e) {
                    //  if (!e.getMessage().equals("access_denied"))
                    //      failed("should have thrown access_denied");
                    //     }
                    initOper();
                    maapi.candidateConfirmedCommit(20);
                    maapi.candidateCommit();
                    initAdmin();
                    maapi.candidateConfirmedCommit(20);
                    maapi.candidateCommit();
                    close2();
                }
            },

            /************************************************************
             * Test 32
             * candidate_reset
             */
            new TestCase() {
                public void test() throws Exception {
                    initOper();
                    maapi.candidateReset();
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
                    maapi.candidateConfirmedCommit(20);
                    maapi.candidateAbortCommit();
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
                    maapi.candidateConfirmedCommit(20);
                    int b = maapi.confirmedCommitInProgress();
                    if (b == 0)
                        failed("should have indicated confirming commit in "+
                               "progress");
                    if (b != usid)
                        failed("should have indicated own usid");
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
                    try {
                        maapi.copyRunningToStartup();
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("No startup"))
                            failed("should have thrown No startup: " + e);
                    }
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
                        failed("should have thrown an exception");
                    } catch (MaapiException e) {
                        if (!e.getMessage().equals("access denied"))
                            failed("expected access denied" + e);
                    }
                    initAdmin();
                    int th1 = maapi.startTrans(Conf.DB_RUNNING,
                                               Conf.MODE_READ);
                    int th2 = maapi.startTrans(Conf.DB_CANDIDATE,
                                               Conf.MODE_READ_WRITE);
                    maapi.copy(th1, th2);
                    maapi.applyTrans(th2, false);
                    close2();
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
                    if (x.getGroups().length != 1 || !x.getGroups()[0].equals("oper"))
                        failed("group information does not match" + x.getGroups()[0]);
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
                    ConfKey x;
                    int usid, th, i;
                    MaapiCursor c;

                    initOper();
                    usid = maapi.getMyUserSession();
                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    c = maapi.newCursor(th, "/mtest/indexes/index");

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

                    initOper();
                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ);

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");
                    c = maapi.newCursor(th, "/mtest/indexes/index");

                    i = 0;
                    x = maapi.getNext(c);
                    while(x != null) {
                        ConfObject p;

                        p = maapi.getElem(th, "/mtest/indexes/index{%x}/port",
                                          x.elementAt(0));
                        if (!(p instanceof ConfInt64))
                            failed("should have been an int64:"+
                                   p.getClass().getName());
                        else if (((ConfInt64) p).longValue() != 110)
                            failed("should have been 110");

                        i++;
                        x = maapi.getNext(c);
                    }

                    if (i != 3)
                        failed("should have reported 3 instances got: "+i);

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
                    if (r[0].getNR() != 0 || !r[0].getVia().equals("maapi"))
                        failed("rollback[0] should be nr 0");

                    close2();
                }
            },

            /************************************************************
             * Test 42
             * action
             */
            new TestCase() {
                public void test() throws Exception {

                    initAdmin();
                maapi.loadSchemas();

                int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    ArrayList<ConfNamespace> ns_list = maapi.getNsList();

                    ConfNamespace n = new cs();

                    maapi.setNamespace(th, n.uri());
                    try {
                        maapi.delete(th, "/system/computer{fred}");
                    } catch (Exception e) {
                        /* ignore */
                    }

                    maapi.create(th, "/system/computer{fred}");

                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);

                    ConfXMLParam[] params = new ConfXMLParam[] {
                        new ConfXMLParamStart(n.hash(), cs.cs_operation,
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_number,
                                                  new ConfInt32(13),
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_type,
                          ConfEnumeration.getEnumByLabel(
                                      "/cs:system/computer/math/operation/type", "add"),
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_operands,
                                                  new ConfList(new ConfObject[] { new ConfInt16(13), new ConfInt16(25) }),
                                                  ns_list),
                        new ConfXMLParamStop(n.hash(), cs.cs_operation,
                                                 ns_list),

                        new ConfXMLParamStart(n.hash(), cs.cs_operation,
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_number,
                                                  new ConfInt32(1),
                                                  ns_list),
         new ConfXMLParamValue(n.hash(), cs.cs_type,
                          ConfEnumeration.getEnumByLabel(
                          "/cs:system/computer/math/operation/type", "square"),
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_operands,
                                                  new ConfList(new ConfObject[] { new ConfInt16(7) }),
                                                  ns_list),
                        new ConfXMLParamStop(n.hash(), cs.cs_operation,
                                                 ns_list),

                        new ConfXMLParamStart(n.hash(), cs.cs_operation,
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_number,
                                                  new ConfInt32(42),
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_type,
                          ConfEnumeration.getEnumByLabel(
                          "/cs:system/computer/math/operation/type", "div"),
                                                  ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_operands,
                                                  new ConfList(new ConfObject[] { new ConfInt16(25), new ConfInt16(4) }),
                                                  ns_list),
                        new ConfXMLParamStop(n.hash(), cs.cs_operation,
                                                 ns_list)
                    };

                    ConfXMLParam[] res =
                        maapi.requestAction(params, n.hash(),
                                            "/system/computer{fred}/math");

                    if (res.length != 15)
                        failed("response to short");

                    if (!(res[0] instanceof ConfXMLParamStart))
                        failed("expected start, got "+res[0]);

                    if (!(res[1] instanceof ConfXMLParamValue))
                        failed("expected value, got "+res[1]);

                    if (!(res[1].val instanceof ConfInt32))
                        failed("expected int32, got "+res[1].val);

                    if (!(res[2] instanceof ConfXMLParamValue))
                        failed("expected value, got "+res[2]);

                    if (!(res[2].val instanceof ConfEnumeration))
                        failed("expected enumhash, got "+res[2].val);

                    if (!(res[4] instanceof ConfXMLParamStop))
                        failed("expected stop, got "+res[4]);

                    close2();
                }
            },

            /************************************************************
             * Test 43
             * action
             */
            new TestCase() {
                public void test() throws Exception {

                    initAdmin();
                    maapi.loadSchemas();

                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    ArrayList<ConfNamespace> ns_list = maapi.getNsList();
                    ConfNamespace n = new cs();

                    maapi.setNamespace(th, n.uri());

                    ConfXMLParam[] params = new ConfXMLParam[] {
                        new ConfXMLParamStart(n.hash(), cs.cs_operation,
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_number,
                                              new ConfInt32(13),
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_type,
                                              ConfEnumeration.getEnumByLabel(
                                   "/cs:system/computer/math/operation/type", "add"),
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_operands,
                                              new ConfList(new ConfObject[]
                                                  { new ConfInt16(13),
                                                    new ConfInt16(25) }),
                                              ns_list),
                        new ConfXMLParamStop(n.hash(), cs.cs_operation,
                                             ns_list),

                        new ConfXMLParamStart(n.hash(), cs.cs_operation,
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_number,
                                              new ConfInt32(1),
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_type,
                                  ConfEnumeration.getEnumByLabel(
                  "/cs:system/computer/math/operation/type", "square"),
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_operands,
                                              new ConfList(new ConfObject[] {
                                                      new ConfInt16(7) }),
                                              ns_list),
                        new ConfXMLParamStop(n.hash(), cs.cs_operation,
                                             ns_list),

                        new ConfXMLParamStart(n.hash(), cs.cs_operation,
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_number,
                                              new ConfInt32(42),
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_type,
                                  ConfEnumeration.getEnumByLabel(
                   "/cs:system/computer/math/operation/type", "div"),
                                              ns_list),
                        new ConfXMLParamValue(n.hash(), cs.cs_operands,
                                              new ConfList(new ConfObject[] {
                                                      new ConfInt16(25),
                                                      new ConfInt16(4) }),
                                              ns_list),
                        new ConfXMLParamStop(n.hash(), cs.cs_operation,
                                             ns_list)
                    };

                    ConfXMLParam[] res =
                        maapi.requestActionTh(th, params,
                                              "/system/computer{fred}/math");

                    if (res.length != 15)
                        failed("response to short");

                    if (!(res[0] instanceof ConfXMLParamStart))
                        failed("expected start, got "+res[0]);

                    if (!(res[1] instanceof ConfXMLParamValue))
                        failed("expected value, got "+res[1]);

                    if (!(res[1].val instanceof ConfInt32))
                        failed("expected int32, got "+res[1].val);

                    if (!(res[2] instanceof ConfXMLParamValue))
                        failed("expected value, got "+res[2]);

                    if (!(res[2].val instanceof ConfEnumeration))
                        failed("expected enumhash, got "+res[2].val);

                    if (!(res[4] instanceof ConfXMLParamStop))
                        failed("expected stop, got "+res[4]);

                    close2();
                }
            },


            /************************************************************
             * Test 44
             * diff iterate
             */
            new TestCase() {

                private boolean traversed_c_int8 = false;
                private boolean traversed_port = false;

                // Override the Default iterate function in the TestCase class
                public DiffIterateResultFlag
                    iterate(ConfObject[] kp,
                            DiffIterateOperFlag op,
                            ConfValue oldValue,
                            ConfValue newValue,
                            Object state) {

                    if (op != DiffIterateOperFlag.MOP_VALUE_SET) {
                        return DiffIterateResultFlag.ITER_RECURSE;
                    }
                    LOGGER.info("kp[0]:" + kp[0]);
                    LOGGER.info("kp    :" + Arrays.toString(kp));
                    if (((ConfTag)kp[0]).tag == 585848417) {
                        traversed_c_int8 = true;
                        if (!newValue.toString().equals("42")) {
                            failed("c_int8 expected 42 got "+
                                   newValue.toString());
                        }
                        LOGGER.info("newval: " + newValue);
                    }
                    if (((ConfTag)kp[0]).tag == 10387472) {
                        traversed_port = true;
                        if (!newValue.toString().equals("8080")) {
                            failed("port expected 42 got "+newValue.toString());
                        }
                        LOGGER.info("newval: " + newValue);
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

                    maapi.setNamespace(th,
                                       "http://tail-f.com/test/mtest/1.0");

                    LOGGER.info("");

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

                    LOGGER.info("done diffIterate!");
                    maapi.finishTrans(th);
                    close2();
                }
            },


            /************************************************************
             * Test 45
             * Partial locks
             */
            new TestCase() {

                public void test() throws Exception {

                    initOper();

                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    ConfObject r;

                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");

                    LOGGER.info("");

                    // take lock
                    int lockId= maapi.lockPartial(Conf.DB_RUNNING,"/mtest/types");
                    LOGGER.info("lockId= "+lockId);

                    // do some read/write
                    // read value: 77
                    r = maapi.getElem(th, "/mtest/types/c_int8");
                    if (((ConfInt8) r).intValue() != 77)
                        failed("c_int8 expected 77 got "+r);
                    // set value to: 42
                    maapi.setElem(th, new ConfInt8(42), "/mtest/types/c_int8");

                    // unlock
                    maapi.unlockPartial(lockId);

                    LOGGER.info("unlock succeded!");
                    maapi.finishTrans(th);
                    close2();
                }
            },


            /************************************************************
             * Test 46
             * Load schemas
             */
            new TestCase() {

                public void test() throws Exception {
                    initOper();

                    // get Maapi schema object
                    MaapiSchemas schemas = Maapi.getSchemas();
                    if (schemas == null)
                        failed("loadSchemas() returned with nulul");

                    int nodeCount = 0;
                    Iterator<MaapiSchemas.CSSchema> iter0 =
                        schemas.getLoadedSchemas().iterator();
                    while (iter0.hasNext()) {
                        MaapiSchemas.CSSchema sch = iter0.next();
                        LOGGER.info("sch:" + sch);
                        if ("http://tail-f.com/test/mtest/1.0"
                            .equals(sch.getURI())) {
                            if (!"2010-06-08".equals(sch.getRevision())) {
                                failed("expected revision '2010-06-08' but got : " +
                                       sch.getRevision());
                            }
                        }

                        LOGGER.info("getRoot:" + sch);
                        MaapiSchemas.CSNode root = sch.getRootNode();
                        Iterator<MaapiSchemas.CSNode> iter =
                            root.getSiblings().iterator();
                        while (iter.hasNext()) {
                            MaapiSchemas.CSNode n = iter.next();
                            nodeCount += 1 + countChildren(n);
                        }
                    }
                    close2();
                    if (nodeCount != 159) {
                        failed("wrong number of nodes in transversal. Expected 159 but found : " + nodeCount);
                    } else {
                        System.out.println("number of nodes : " + nodeCount);
                    }
                }
            },

            /************************************************************
             * Test 47
             * Save config
             */
            new TestCase() {

                public void test() throws Exception {


                    initAdmin();

                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    boolean ret = true;

                    ret = dotest47(maapi, s, th,
                                   EnumSet.of(MaapiConfigFlag.XML_FORMAT),
                                   null, null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.XML_FORMAT");
                    }

                    ret = dotest47(maapi, s, th,
                                   EnumSet.of(MaapiConfigFlag.CISCO_XR_FORMAT),
                                   null, null);
                    if (!ret) {
                        failed("failed in saveConfig with Maapi.MaapiConfigFlag.CISCO_XR_FORMAT");
                    }

                    ret = dotest47(maapi, s, th, EnumSet.of(MaapiConfigFlag.JUNIPER_CLI_FORMAT), null, null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.JUNIPER_CLI_FORMAT");
                    }

                    ret = dotest47(maapi, s, th, EnumSet.of(MaapiConfigFlag.CISCO_IOS_FORMAT), null, null);
                    if (!ret) {
                        failed("failed in saveConfig with Maapi.MaapiConfigFlag.CISCO_IOS_FORMAT");
                    }

                    ret = dotest47(maapi, s, th, EnumSet.of(MaapiConfigFlag.XML_FORMAT), "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.XML_FORMAT path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th, EnumSet.of(MaapiConfigFlag.CISCO_XR_FORMAT), "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.CISCO_XR_FORMAT  path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th, EnumSet.of(MaapiConfigFlag.JUNIPER_CLI_FORMAT), "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.JUNIPER_CLI_FORMAT  path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th, EnumSet.of(MaapiConfigFlag.CISCO_IOS_FORMAT), "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.CISCO_IOS_FORMAT  path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th,
                                EnumSet.of(MaapiConfigFlag.XML_FORMAT, MaapiConfigFlag.WITH_DEFAULTS),
                                "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.XML_FORMAT, MaapiConfigFlag.WITH_DEFAULTS path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th,
                                EnumSet.of(MaapiConfigFlag.CISCO_XR_FORMAT, MaapiConfigFlag.WITH_DEFAULTS),
                                "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig withMaapiConfigFlag.CISCO_XR_FORMAT, MaapiConfigFlag.WITH_DEFAULTS path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th,
                                EnumSet.of(MaapiConfigFlag.JUNIPER_CLI_FORMAT, MaapiConfigFlag.WITH_DEFAULTS),
                                "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.JUNIPER_CLI_FORMAT, MaapiConfigFlag.WITH_DEFAULTS path /mtest:mtest");
                    }

                    ret = dotest47(maapi, s, th,
                                EnumSet.of(MaapiConfigFlag.CISCO_IOS_FORMAT, MaapiConfigFlag.WITH_DEFAULTS),
                                "/mtest:mtest", null);
                    if (!ret) {
                        failed("failed in saveConfig with MaapiConfigFlag.CISCO_IOS_FORMAT, MaapiConfigFlag.WITH_DEFAULTS path /mtest:mtest");
                    }


                    maapi.finishTrans(th);
                    close2();
                }
            },


            /************************************************************
             * Test 48
             * Load config
             */
            new TestCase() {

                public void test() throws Exception {

                    initAdmin();

                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    boolean ret = true;
                    ret = dotest48(maapi, s, th, EnumSet.of(MaapiConfigFlag.XML_FORMAT));
                    if (!ret) {
                        failed("failed to load and save Config with MaapiConfigFlag.XML_FORMAT");
                    }

                    ret = dotest48(maapi, s, th, EnumSet.of(MaapiConfigFlag.JUNIPER_CLI_FORMAT));
                    if (!ret) {
                        failed("failed to load and save Config with MaapiConfigFlag.JUNIPER_CLI_FORMAT");
                    }

                    ret = dotest48(maapi, s, th, EnumSet.of(MaapiConfigFlag.CISCO_XR_FORMAT));
                    if (!ret) {
                        failed("failed to load and save Config with MaapiConfigFlag.CISCO_XR_FORMAT");
                    }

                    ret = dotest48(maapi, s, th, EnumSet.of(MaapiConfigFlag.CISCO_IOS_FORMAT));
                    if (!ret) {
                        failed("failed to load and save Config with MaapiConfigFlag.CISCO_IOS_FORMAT");
                    }

                    maapi.finishTrans(th);
                    close2();

                }
            },

            // test 49, getObject(), setObject()
            new TestCase() {

                public void test() throws Exception {
                    initAdmin();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/mtest/1.0");
                    maapi.addNamespace(new mtest());

                    ConfBuf w = new ConfBuf("www");
                    ConfObject[] objs =
                        maapi.getObject(th,"/mtest/servers/server{%x}", w);
                    for (int i = 0; i<objs.length; i++) {
                        System.out.println("i = " + i + "OBJ = " + objs[i]);
                    }

                    objs[2] = new ConfUInt16(6666);
                    maapi.setObject(th, objs, "/mtest/servers/server{%x}", w);


                    // and now read that one
                    ConfObject objs2[]  =
                        maapi.getObject(th, "/mtest/servers/server{%x}",w);
                    for (int i = 0; i<objs2.length; i++) {
                        System.out.println("i = " + i + "OBJ2 = " + objs2[i]);
                    }

                    ConfUInt16 uu = (ConfUInt16)objs2[2];
                    if (uu.intValue() != 6666)
                        failed("bad intval");

                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);
                    close2();
                }

            },
            new TestCase(){
                public void test() throws Exception {
                       System.out.println("Testing testReadOnlyMode()");

                       initOper();

                       int usid = maapi.getMyUserSession();
                       int th = maapi.startTrans(Conf.DB_RUNNING, Conf.MODE_READ_WRITE);
                       int th2 = -1;
                       int th3 = -1;

                       maapi.setReadOnlyMode(true);

                       //start the other transaction that shuld fail
                       Socket s2 = new Socket(host,port);
                       Maapi maapi2 = new Maapi(s2);

                       maapi2.startUserSession("jb",
                                               InetAddress.getLocalHost(),
                                               "maapi",
                                               new String[] { "oper" },
                                               MaapiUserSessionFlag.PROTO_TCP);
                       try{
                          th2 =  maapi2.startTrans(Conf.DB_RUNNING,Conf.MODE_READ_WRITE);
                          maapi2.setElem(th2, new ConfInt8(42), "/mtest/types/c_int8");

                          failed("shold have thrown node is in read only mode");
                       }catch(MaapiException e){
                           System.out.println("ALL ok..");
                       }

                        //start the other transaction that shuld fail
                       Socket s3 = new Socket(host,port);
                       Maapi maapi3 = new Maapi(s3);

                       maapi3.startUserSession("jb",
                                               InetAddress.getLocalHost(),
                                               "maapi3",
                                               new String[]{"oper"},
                                               MaapiUserSessionFlag.PROTO_TCP);

                       try{
                           maapi.setReadOnlyMode(false);
                           th3 = maapi3.startTrans(Conf.DB_RUNNING,Conf.MODE_READ_WRITE);
                           maapi3.setElem(th3, new ConfInt8(42), "/mtest/types/c_int8");

                       }catch(MaapiException e){
                            e.printStackTrace();
                            failed("shold NOT have thrown node is in read only mode");
                       }finally{
                           s2.close();
                       }

                }
            },

            /************************************************************
             * Test 50
             * Load config cmds
             */

            new TestCase() {

                public void test() throws Exception {

                    initAdmin();

                    int usid = maapi.getMyUserSession();
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);

                    boolean ret = true;
                    ret = dotest50(maapi, s, th,
                                   EnumSet.of(MaapiConfigFlag.CISCO_XR_FORMAT));
                    if (!ret) {
                        failed("failed to load and save Config with MaapiConfigFlag.CISCO_XR_FORMAT");
                    }

                    ret = dotest50(maapi, s, th,
                                   EnumSet.of(MaapiConfigFlag.CISCO_IOS_FORMAT));
                    if (!ret) {
                        failed("failed to load and save Config with MaapiConfigFlag.CISCO_IOS_FORMAT");
                    }

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


        // just do the last test
        //from = 50;


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

    private static int countChildren(MaapiSchemas.CSNode n) {
        int childCount = 0;
        List<MaapiSchemas.CSNode> chlist = n.getChildren();
        if (chlist == null) return 0;
        Iterator<MaapiSchemas.CSNode> iter = n.getChildren().iterator();
        while (iter.hasNext()) {
            MaapiSchemas.CSNode c = iter.next();
            childCount += 1 + countChildren(c);
        }
        return childCount;
    }

    private static boolean dotest47(Maapi maapi, Socket sock, int tid, EnumSet<MaapiConfigFlag> flags, String path, File f) {
        try {
            MaapiInputStream in = maapi.saveConfig(tid, flags, path);
            if (in == null) {
                return false;
            }

            FileWriter fw = null;
            if (f != null) {
                fw = new FileWriter(f);
            }

            int cnt = 0;
            int b;
            while ((b=in.read()) >= 0) {
                if (fw != null) {
                    fw.write(b);
                }
                cnt++;
            }
            in.close();

            if (fw != null) {
                fw.flush();
                fw.close();
            }

            if (cnt == 0) {
                return false;
            }
            //      System.out.println("config size : "+cnt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean dotest48(Maapi maapi, Socket sock, int tid, EnumSet<MaapiConfigFlag> flags) {
        try {
            File fp = new File("cfg.out");
            boolean ret = true;

            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            maapi.loadConfig(tid,flags, "cfg.out");

            fp = new File("cfg2.out");

            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            ret = compare_files("cfg.out", "cfg2.out");
            if (!ret) {
                return ret;
            }


    /* Trac #4125 - repeat with maapi_set_user_session()
       instead of maapi_start_user_session() */
            int usid = maapi.getMyUserSession();
            Socket s2 = new Socket(sock.getInetAddress(), sock.getPort());
            Maapi maapi2 = new Maapi(s2);
            maapi2.setUserSession(usid);
            int tid2 = maapi2.startTrans(Conf.DB_RUNNING,
                                        Conf.MODE_READ_WRITE);
            maapi2.loadConfig(tid2, flags,"cfg.out");
            maapi2.applyTrans(tid2, false);
            maapi2.finishTrans(tid2);
            s2.close();

            fp = new File("cfg2.out");
            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            ret = compare_files("cfg.out", "cfg2.out");
            if (!ret) {
                return ret;
            }

            // Ditto with maapi_start_trans2()
            int usid3 = maapi.getMyUserSession();
            Socket s3 = new Socket(sock.getInetAddress(), sock.getPort());
            Maapi maapi3 = new Maapi(s3);
            int tid3 = maapi3.startTrans2(Conf.DB_RUNNING,
                                         Conf.MODE_READ_WRITE, usid3);
            maapi3.loadConfig(tid3, flags,"cfg.out");
            maapi3.applyTrans(tid3, false);
            maapi3.finishTrans(tid3);
            s3.close();

            fp = new File("cfg2.out");
            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            ret = compare_files("cfg.out", "cfg2.out");
            if (!ret) {
                return ret;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean dotest50(Maapi maapi, Socket sock, int tid, EnumSet<MaapiConfigFlag> flags) {
        try {
            File fp = new File("cfg.out");
            boolean ret = true;
            String diff;
            EnumSet cmdflags = EnumSet.of(MaapiConfigFlag.CISCO_XR_FORMAT,
                                          MaapiConfigFlag.MERGE_CONFIGURATIONS);

            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            maapi.loadConfigCmds(tid, cmdflags,
                                 "mtest servers server xx\nip 1.1.1.1\n", "");

            fp = new File("cfg2.out");

            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            diff = diff_files("cfg.out", "cfg2.out");

            if (!diff.equals("> mtest servers server xx\n>  ip 1.1.1.1\n> !\n")) {
                    System.out.println("Diff="+diff);
                    return false;
                }

            maapi.loadConfigCmds(tid, cmdflags, "port 40",
                                 "/mtest/servers/server{xx}");

            fp = new File("cfg3.out");

            ret = dotest47(maapi, sock, tid, flags, null, fp);
            if (!ret) {
                return ret;
            }

            diff = diff_files("cfg.out", "cfg3.out");

            if (!diff.equals("> mtest servers server xx\n>  ip   1.1.1.1\n>  port 40\n> !\n")) {
                System.out.println("Diff="+diff);
                return false;
            }

            maapi.loadConfig(tid, flags, "cfg.out");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean compare_files(String file1, String file2)
        throws Exception {
        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        BufferedReader br2 = new BufferedReader(new FileReader(file2));

        String line;
        StringBuffer stb1 = new StringBuffer();
        while ((line=br1.readLine()) != null) {
            stb1.append(line);
        }

        StringBuffer stb2 = new StringBuffer();
        while ((line=br2.readLine()) != null) {
            stb2.append(line);
        }
        br1.close();
        br2.close();

        return stb1.toString().equals(stb2.toString());
    }


    // Diff code from:
    // http://introcs.cs.princeton.edu/96optimization/Diff.java.html

    private static String diff_files(String file1, String file2)
        throws Exception {

        // read in lines of each file

        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        BufferedReader br2 = new BufferedReader(new FileReader(file2));

        String line;
        StringBuffer stb1 = new StringBuffer();
        while ((line=br1.readLine()) != null) {
            stb1.append(line+"\n");
        }

        StringBuffer stb2 = new StringBuffer();
        while ((line=br2.readLine()) != null) {
            stb2.append(line+"\n");
        }
        br1.close();
        br2.close();

        String[] x = stb1.toString().split("\\n");
        String[] y = stb2.toString().split("\\n");
        StringBuffer res = new StringBuffer();

        // number of lines of each file
        int M = x.length;
        int N = y.length;

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M+1][N+1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M-1; i >= 0; i--) {
            for (int j = N-1; j >= 0; j--) {
                if (x[i].equals(y[j]))
                    opt[i][j] = opt[i+1][j+1] + 1;
                else
                    opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
            }
        }

        // recover LCS itself and print out non-matching lines to standard
        // output
        int i = 0, j = 0;
        while(i < M && j < N) {
            if (x[i].equals(y[j])) {
                i++;
                j++;
            }
            else if (opt[i+1][j] >= opt[i][j+1])
                res.append("< " + x[i++] + "\n");
            else
                res.append("> " + y[j++] + "\n");
        }

        // dump out one remainder of one string if the other is exhausted
        while(i < M || j < N) {
            if      (i == M) res.append("> " + y[j++] + "\n");
            else if (j == N) res.append("< " + x[i++] + "\n");
        }

        return res.toString();
    }
}

