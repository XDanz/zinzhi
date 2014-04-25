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

package com.tailf.test.maapi3;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EnumSet;
import java.util.Arrays;

import javax.naming.ConfigurationException;

import com.tailf.conf.*;
import com.tailf.proto.*;
import com.tailf.maapi.*;


import org.apache.log4j.Logger;

import com.tailf.test.maapi3.namespaces.*;

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

        LOGGER.debug("---------------------------------------");
        LOGGER.debug("Java Maapi candidate db tests");
        LOGGER.debug("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * test of standard confirmed commit and candidate reset
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());


                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int rth   = maapi.startTrans(Conf.DB_RUNNING,
                                                 Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");
                    int cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                                 Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");

                    maapi.setObject(cth, new ConfObject[] {new ConfBuf("s1"),
                                                           new ConfDefault(),
                                                           new ConfDefault()},
                        "/servers/server");
                    maapi.applyTrans(cth, true);
                    maapi.finishTrans(cth);
                    maapi.candidateCommit();

                    maapi.endUserSession();
                    s.close();

                    // setup
                    s = new Socket("localhost", Conf.PORT);
                    maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    rth   = maapi.startTrans(Conf.DB_RUNNING,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");
                    cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");

                    String path = "/servers/server{s1}";
                    if (!maapi.exists(cth, path)) {
                        failed("path " + path + " initially does not exist");
                    }

                    maapi.delete(cth, path);
                    maapi.applyTrans(cth, true);
                    maapi.finishTrans(cth);
                    maapi.endUserSession();
                    s.close();

                    // candidate reset
                    s = new Socket("localhost", Conf.PORT);
                    maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    rth   = maapi.startTrans(Conf.DB_RUNNING,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");
                    cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");

                    if (maapi.exists(cth, path)) {
                        failed("path " + path + " exist after delete");
                    }

                    maapi.candidateReset();
                    if (!maapi.exists(cth, path)) {
                        failed("path " + path + " does not exist after reset");
                    }

                    maapi.delete(cth, path);
                    if (maapi.exists(cth, path)) {
                        failed("path " + path +
                               " exist after delete after reset");
                    }

                    maapi.applyTrans(cth, true);
                    maapi.finishTrans(cth);

                    maapi.endUserSession();
                    s.close();
                }
            },


            /************************************************************
             * Test 1
             * test of timeout, abort, endsession and confirm for
             * candidate commit
             */
            new TestCase() {
                 public boolean skip() { return true; }
                public void test() throws Exception {
                    /** NOTE NOTE NOTE  FIX THIS **/
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());


                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    int rth   = maapi.startTrans(Conf.DB_RUNNING,
                                                 Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");


                    int cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                                 Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");
                    String path = "/servers/server{s1}";

                    /* setup */
                    maapi.create(rth,path);
                    maapi.applyTrans(rth,true);
                    maapi.finishTrans(rth);

                    rth = maapi.startTrans(Conf.DB_RUNNING,
                                           Conf.MODE_READ_WRITE);

                    if(!maapi.exists(rth,path)){
                        failed("path: " + path +
                               " should have exists");
                    }
                    maapi.delete(cth,path);
                    maapi.applyTrans(cth,true);
                    maapi.finishTrans(cth);



                    /* candidate reset */
                    cth = maapi.startTrans(Conf.DB_CANDIDATE,
                                           Conf.MODE_READ_WRITE);

                    if(maapi.exists(cth,path)){
                        failed("path:" + path +
                               " in candid should have not exists");
                    }
                    maapi.candidateReset();

                    if(!maapi.exists(cth,path)){
                        failed("path:" + path +
                               " in candid should have exists");
                    }

                    if(maapi.isCandidateModified()){
                        failed("Candidate should NOT have been modified");
                    }

                    maapi.delete(cth, path);

                    if(maapi.exists(cth,path)){
                          failed("path:" + path +
                               " in candid should NOT have exists");
                    }

                    maapi.applyTrans(cth, true);
                    maapi.finishTrans(cth);

                    if(!maapi.isCandidateModified()){
                        failed("Candidate should  have been modified");
                    }


                    // timeout
                    maapi.candidateConfirmedCommit(2);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path + " exist in timeout test");
                    }


                    Thread.sleep(3000);
                    if (!maapi.exists(rth, path)) {
                        failed("path " + path +
                               " does not exist in timeout test");
                    }


                    // abort
                    maapi.candidateConfirmedCommit(10);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path + " exist in abort test");
                    }

                    maapi.candidateAbortCommit();
                    if (!maapi.exists(rth, path)) {
                        failed("path " + path +
                               " does not exist in abort test");
                    }


                    // end session
                    maapi.candidateConfirmedCommit(10);

                    if (maapi.exists(rth, path)) {
                        failed("path " + path + " exist in end session test");
                    }
                    int uzid = maapi.getMyUserSession();
                    s.close();
                    s = new Socket("localhost", Conf.PORT);
                    maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    rth =  maapi.startTrans(Conf.DB_RUNNING,
                                            Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");

                    boolean keepsleep = true;
                    /* wait for the old user session to go away
                       before continuing */

                    while(keepsleep){
                        boolean found = false;
                        for(int u: maapi.getUserSessions()){
                            if(u == uzid){
                                found = true;
                            }
                        }

                        if(found)
                            Thread.sleep(1000);
                        else{
                            keepsleep = false;
                            break;
                        }
                    }

                    cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");

                    if (!maapi.exists(rth, path)) {
                        failed("path " + path +
                               " does not exist in end session test");
                    }

                    // confirm
                    maapi.candidateConfirmedCommit(1);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path + " exist in confirm test");
                    }

                    maapi.candidateCommit();
                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " exist after commit in confirm test");
                    }

                    maapi.endUserSession();
                    s.close();
                }
            },


            /************************************************************
             * Test 2
             * test of tail-f confirmed commit
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());

                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    // start transaction
                    int rth   = maapi.startTrans(Conf.DB_RUNNING,
                                                 Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");
                    int cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                                 Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");


                    maapi.setObject(cth,
                                    new ConfObject[] {new ConfBuf("s1"),
                                                      new ConfDefault(),
                                                      new ConfDefault()},
                                    "/servers/server");
                    maapi.applyTrans(cth, true);
                    maapi.finishTrans(cth);
                    maapi.candidateCommit();

                    maapi.endUserSession();
                    s.close();

                    // setup
                    s = new Socket("localhost", Conf.PORT);
                    maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    rth   = maapi.startTrans(Conf.DB_RUNNING,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");
                    cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");

                    String path = "/servers/server{s1}";

                    maapi.candidateReset();

                    if (!maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 initially does not exist");
                    }

                    maapi.delete(cth, path);
                    if (maapi.exists(cth, path)) {
                        failed("path " + path + " test3 exists after delete");
                    }
                    maapi.applyTrans(cth, true);
                    maapi.finishTrans(cth);

                    String cookie = "test";

                    // timeout
                    maapi.candidateConfirmedCommitPersistent(1, cookie, null);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 exists after commit persistent");
                    }
                    Thread.sleep(2000);
                    if (!maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 does not exist after timeout");
                    }

                    // abort
                    maapi.candidateConfirmedCommitPersistent(10, cookie, null);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 exists after commit " +
                               "persistent in abort test");
                    }
                    maapi.candidateAbortCommitPersistent(cookie);
                    if (!maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 does not exist after abort");
                    }


                    // end session (survives)
                    maapi.candidateConfirmedCommitPersistent(10, cookie, null);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 exists after commit persistent " +
                               "in end session test");
                    }
                    s.close();
                    s = new Socket("localhost", Conf.PORT);
                    maapi = new Maapi(s);
                    maapi.addNamespace(new smp());


                    maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    // start transaction
                    rth   = maapi.startTrans(Conf.DB_RUNNING,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(rth, "http://tail-f.com/test/smp");
                    cth   = maapi.startTrans(Conf.DB_CANDIDATE,
                                             Conf.MODE_READ_WRITE);
                    maapi.setNamespace(cth, "http://tail-f.com/test/smp");

                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 exists after end session test");
                    }


                    // confirm w wrong cookie (fails but no effect on commit)
                    try {
                        maapi.candidateCommitPersistent("bar");
                    } catch (MaapiException e) {
                        if (e.getErrorCode() != ErrorCode.ERR_NOEXISTS) {
                            failed("got wrong exception, " +
                                   "expected errCode = 1 but got : " +
                                   e.getErrorCode());
                        }
                    }
                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 exists after wrong commit");
                    }
                    // ERR(maapi_candidate_commit_persistent(sock, "bar"));
                    // assert(confd_errno == CONFD_ERR_NOEXISTS);
                    // assert(maapi_exists(sock, tid, path) == 0);

                    // confirm
                    maapi.candidateCommitPersistent(cookie);
                    Thread.sleep(4000);
                    if (maapi.exists(rth, path)) {
                        failed("path " + path +
                               " test3 exists after confirmed commit");
                    }

                    maapi.endUserSession();
                    s.close();
                }
            }
        };


        /************************************************************
         * Run the tests
         */

        // int from = 0;
        // int to   = tests.length;

        // // from = 21;
        // // to = 22;

        // test.testnr = from;

        // for(i = from ; i < to ; i++)
        //     tests[i].doit()
        //;

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
        LOGGER.debug("---------------------------------------");
        LOGGER.debug("  Passed:  " + test.pass);
        LOGGER.debug("  Failed:  " + test.fail);
        if(test.fail > 0)
            LOGGER.debug("TEST FAILED:" +
                        Arrays.toString(TestCase.failedtests.toArray()));
        LOGGER.debug("  Skipped: " + test.skip);
        LOGGER.debug("---------------------------------------");
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
            LOGGER.debug("passed");
        }
    }

    private static void skipped() {
        if (!test.processed) {
            test.processed = true;
            test.skip++;
            LOGGER.debug("skipped");
        }
    }


    private static void failed(Exception e) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.error("failed");
            LOGGER.error("    '"+e.toString()+"'",e);
            //e.printStackTrace();
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

