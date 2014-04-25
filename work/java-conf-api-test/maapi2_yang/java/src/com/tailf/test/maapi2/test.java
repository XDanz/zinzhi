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

package com.tailf.test.maapi2;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.EnumSet;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfDefault;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfTag;

import com.tailf.conf.ConfNoExists;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfUInt32;
import com.tailf.conf.ConfValue;
import com.tailf.conf.ErrorCode;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiFlag;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuLeaf;
import com.tailf.proto.ConfEObject;

import com.tailf.test.maapi2.namespaces.*;
import org.apache.log4j.Logger;

public class test {

    private static final Logger log =
        Logger.getLogger( test.class );

    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;

    public static ConfKey lastkey = null;
    public static ConfKey firstkey = null;

    static public void main(String args[]) {
        int i;

        log.debug("---------------------------------------");
        log.debug("Java Maapi yang tests");
        log.debug("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * test J_DEFAULT and maapi.setFlags
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    maapi.setObject(th,
                                    new ConfObject[] {
                                        new ConfBuf("s1"),
                                        new ConfDefault(),
                                        new ConfDefault()},
                                    "/servers/server");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ_WRITE);

                    ConfValue val = maapi.getElem(th,
                                                  "/servers/server{s1}/ip");

                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    ConfValue expectedVal = new ConfIPv4("127.0.0.1");

                    if (!expectedVal.equals(val)) {
                        failed("expected ip 127.0.0.1 but got " + val);
                    }

                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ_WRITE);

                    EnumSet<MaapiFlag> enums =
                        EnumSet.of(MaapiFlag.NO_DEFAULTS);

                    maapi.setFlags(th, enums);

                    val = maapi.getElem(th, "/servers/server{s1}/ip");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    log.debug("VALUE IS : " + val);
                    expectedVal = new ConfDefault();
                    if (!expectedVal.equals(val)) {
                        failed("expected J_DEFAULT but got " + val);
                    }

                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 1
             * test J_NOEXISTS and maapi.setFlags
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    ConfObject[] objArr = maapi.getObject(th, "/stats");

                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    log.debug("VALUE IS : " + objArr[0]);

                    ConfValue expectedVal = new ConfUInt32(123);
                    if (!expectedVal.equals(objArr[0])) {
                        failed("expected hitrate=123 but got " + objArr[0]);
                    }


                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ_WRITE);
                    EnumSet<MaapiFlag> enums =
                        EnumSet.of(MaapiFlag.CONFIG_ONLY);
                    maapi.setFlags(th, enums);

                    objArr = maapi.getObject(th, "/stats");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    log.debug("VALUE IS : " + objArr[0]);


                    expectedVal = new ConfNoExists();
                    if (!expectedVal.equals(objArr[0])) {
                        failed("expected J_NOEXISTS but got " + objArr[0]);
                    }

                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 2
             * test of getCwd
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    if(maapi.getSchemas() == null)
                        maapi.loadSchemas();

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    maapi.setObject(th,
                                    new ConfObject[] {new ConfBuf("s3"),
                                                      new ConfDefault(),
                                                      new ConfDefault()},
                                    "/servers/server");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    th   = maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ_WRITE);

                    maapi.cd(th, "/servers/server{s3}");

                    String pathStr = maapi.getCwd(th);
                    log.debug("Current position is : " + pathStr);

                    ConfPath cpath = maapi.getCwdPath(th);

                    ConfObject[] pathElemArr = cpath.getKP();

                    //getPath()
                    //  .elements();

                    // for (ConfEObject eobj : pathElemArr) {
                    //  log.debug("PathElem = " + eobj);
                    // }
                    // log.debug("Current confPath is : " + cpath);

                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    if (!"/smp:servers/server{s3}".equals(pathStr)) {
                        failed("expected cwd to be /smp:servers/server{s3} " +
                               "but got " + pathStr);
                    }

                    if (!"smp:servers".equals(pathElemArr[2].toString())) {
                        failed ("expected first element in path to be " +
                                "[smp|servers]" +
                                "but was " + pathElemArr[2]);
                    }
                    if (!"smp:server".equals(pathElemArr[1].toString())) {
                        failed ("expected first element in path " +
                                "to be 'server' " +
                                "but was " + pathElemArr[1]);
                    }

                    if (!"{s3}"
                        .equals(pathElemArr[0].toString())) {
                        failed ("expected first element in path " +
                                "to be {s3}"
                                + "but was " + pathElemArr[0]);
                    }
                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 3
             * test of isRunningModified Works only once.
             * NOTE:! To run multiple times run > make iclean
             *
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);


                    String path1= "/servers/server{s5}";
                    String key= "s5";

                    maapi.setObject(th, new ConfObject[] {
                            new ConfBuf(key),
                            new ConfDefault(),
                            new ConfDefault()},
                        "/servers/server");


                    maapi.copyRunningToStartup();
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);

                    boolean result =
                        maapi.isRunningModified();

                    if (result != true) {
                        failed("expected result of isRunningModified to " +
                               "be true but was : false");
                    }


                    /****************************************/

                    th = maapi.startTrans(Conf.DB_RUNNING,
                                     Conf.MODE_READ_WRITE);

                    path1 = "/servers/server{s6}";
                    key = "s6";

                    maapi.setObject(th, new ConfObject[] {
                            new ConfBuf(key),
                            new ConfDefault(),
                            new ConfDefault()},
                        "/servers/server");

                    maapi.copyRunningToStartup();

                    result = maapi.isRunningModified();

                    if(result == true){
                        failed("expected result of isRunningModified to " +
                               "be false but was : true");
                    }

                    maapi.setNamespace(th, "http://tail-f.com/test/smp");



                    maapi.setObject(th, new ConfObject[] {
                            new ConfBuf("s5"),
                            new ConfDefault(),
                            new ConfDefault()},
                        "/servers/server");
                    maapi.applyTrans(th, true);
                    maapi.finishTrans(th);



                    result = maapi.isRunningModified();


                    if (result == false) {
                        failed("expected result of isRunningModified " +
                               "to be true but was : false");
                    }

                    maapi.endUserSession();
                    s.close();

                }
            },

            /************************************************************
             * Test 4
             * test of doDisplay()
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    boolean result =
                        maapi.doDisplay(th,
                                        "/servers/server{s3}/port");
                    log.debug("result was : " + result);
                    if (result == true) {
                        failed("expected result of doDisplay() " +
                               "to be false but was : true");
                    }

                    result = maapi.doDisplay(th, "/servers/server{s1}/port");
                    log.debug("result was : " + result);
                    if (result == false) {
                        failed("expected result of doDisplay() to be " +
                               "true but was : false");
                    }

                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 5
             * test of userMessage, sysMessage, prioMessage
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    // this is a dumb test since it needs a
                    //cli or webui session to verify the response
                    // still it checks that the calls does not throw exceptions.
                    maapi.userMessage("all", "user-message-1", "user1");

                    maapi.sysMessage("all",
                                     "\n---------------\nsys-message-1\n" +
                                     "---------------\n");

                    try{
                        maapi
                            .prioMessage("try",
                                   "\n***************\nprio-message-1\n*" +
                                         "**************\n");
                    }catch(ConfException e){
                        if(e.getErrorCode() != ErrorCode.ERR_NOEXISTS){
                            log.error("Should throw exception " +
                                         e.getErrorCode(),e);
                        }
                    }

                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 6
             * test of xpath2kpath
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.loadSchemas();
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    ConfObject[] ref =
                        new ConfObject[]{
                        new ConfTag(new smp().hash(), smp._ip),
                        new ConfKey(new ConfBuf("s1")),
                        new ConfTag(new smp().hash(), smp._server),
                        new ConfTag(new smp().hash(), smp._servers)
                    };



                    ConfPath path =
                        maapi.xpath2kpath("/smp:servers/server[name=s1]/ip");

                    ConfObject[] kp = path.getKP();
                    log.debug("ref:" + Arrays.toString(ref));
                    log.debug("kp:" + Arrays.toString(kp));

                    log.debug("path : " + path);

                    for(int i = 0; i < ref.length; i++){
                        if(ref[i] instanceof ConfTag){
                            ConfTag currTag = (ConfTag)ref[i];

                            ConfTag kpTag = (ConfTag)kp[i];
                            kpTag.setConfNamespace(new smp());

                            //currTag.setConfNamespace(new smp());

                            log.debug("tag:" + currTag.getTag());
                            log.debug("uri:" + currTag.getURI());
                            log.debug("prefix:" +currTag.getPrefix());
                            log.debug("ns:" + currTag.getConfNamespace());



                        }

                        if(!ref[i].equals(kp[i])){
                            failed("elem:"  +ref[i]  + " expected");
                        }
                    }

                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 7
             * test of deref
             */

            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");


                    maapi.setObject(th, new ConfObject[] {new ConfBuf("s44"),
                                                          new ConfDefault(),
                                                          new ConfDefault()},
                        "/servers/server");
                    maapi.setObject(th, new ConfObject[] {new ConfBuf("s45"),
                                                          new ConfDefault(),
                                                          new ConfDefault()},
                        "/servers/server");

                    maapi.setElem(th, "s44", "/test/ref");

                    maapi.setObject(th,
                                    new ConfObject[] {
                                        new ConfBuf("s45")},
                                    "/test2/server2");

                    ConfObject[][] refs =
                        maapi.deref(th,  "/test/ref");

                    log.debug("refs = " +
                                Arrays.toString(refs[0]));

                    ConfObject[] keypath = refs[0];
                    //String ss = Conf.kpToString(keypath);
                    ConfPath path = new ConfPath(keypath);
                    log.debug("path:" + path);

                    if (path.toString()
                        .compareTo("/smp:servers" +
                                   "/server{s44}/name") != 0) {
                        failed("need path " +
                               "/smp:servers/" +
                               "server{s44}/name but got " + path.toString());
                    }

                    smp smp_cs = new smp();




                    maapi.applyTrans(th, true);

                    maapi.endUserSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 8
             * test of active/inactive
             */

            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"oper"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    log.debug("START TRANS -->");
                    // start transaction
                    EnumSet<MaapiFlag> flags =
                        EnumSet.of(MaapiFlag.NO_DEFAULTS,
                                   MaapiFlag.HIDE_INACTIVE);

                    int th = 0;
                    ErrorCode ecode = null;
                    try {
                        th  = maapi.startTransFlags(Conf.DB_RUNNING,
                                                    Conf.MODE_READ_WRITE,
                                                    maapi.getMyUserSession(),
                                                    flags);

                        log.debug("START TRANS --> OK");
                    } catch (ConfException e) {
                        log.error("",e);

                    }

                    if (ecode == null) {
                        failed("expected call of StartTransFlags() " +
                               "with Mode_READ_WRITE and HIDE_INACTIVE to fail,"
                               +  "but it did not.");

                    } else if (ecode != ErrorCode.ERR_PROTOUSAGE) {
                        failed("expected call of StartTransFlags() " +
                               "to produce ERR_PROTOUSAGE exception but was " +
                               ecode.stringValue());
                    }

                    th  = maapi.startTransFlags(Conf.DB_RUNNING,
                                                Conf.MODE_READ,
                                                maapi.getMyUserSession(),
                                                flags);

                    maapi.setNamespace(th, "http://tail-f.com/test/smp");

                    String path = "/activetest/active";

                    boolean found = maapi.exists(th, path);
                    if (found) {
                        failed ("maapi.exists() found element that " +
                                "was expected to fail because of " +
                                "HIDE_INACTIVE");
                    }

                    th  =
                        maapi.startTransFlags(Conf.DB_RUNNING,
                                              Conf.MODE_READ_WRITE,
                                              maapi.getMyUserSession(),
                                              EnumSet
                                              .of(MaapiFlag.NO_DEFAULTS));
                    maapi.setNamespace(th, "http://tail-f.com/test/smp");
                    found = maapi.exists(th, path);
                    if (!found) {
                        failed ("maapi.exists() did not find element that " +
                                "was expected to be found");
                    }

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

        //from = 8;
        //to = 9;

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
        log.debug("---------------------------------------");
        log.debug("  Passed:  " + test.pass);
        log.debug("  Failed:  " + test.fail);
         if(test.fail > 0)
            log.debug("TEST FAILED:" +
                        Arrays.toString(TestCase.failedtests.toArray()));
        log.debug("  Skipped: " + test.skip);
        log.debug("---------------------------------------");
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
            log.debug("passed");
        }
    }

    private static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            log.info("skipped");
        }
    }


    private static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            log.error("failed");
            log.error("    '"+e.toString()+"'",e);
            //e.printStackTrace();
        }
    }

    private static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            log.error("failed");
            log.error("    '"+reason+"'");
        }
    }
}

