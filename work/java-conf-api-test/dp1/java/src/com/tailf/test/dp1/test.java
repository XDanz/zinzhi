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

package com.tailf.test.dp1;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.test.dp1.namespaces.smp;
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
                    LOGGER.debug("start user session -->");
                    maapi.startUserSession("admin", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);
                    LOGGER.debug("start user session --> OK");

                    MaapiAuthentication mauth =
                        maapi.authenticate("oper", "oper");

                    if (mauth.isValid()) {
                        failed("user oper was authenticated, which is wrong");
                    }

                    if (!mauth.getReason().equals("BAD BAD user oper")) {
                        failed("did not receive correct errstring " +
                               "from auth callback using setError()");
                    }
                    mauth = maapi.authenticate("admin", "admin");
                    if (!mauth.isValid()) {
                        failed("user admin was not authenticated," +
                               " which is wrong");
                    }

                    LOGGER.debug("start trans  -->");
                    // start transaction
                    int th   = maapi.startTrans(Conf.DB_RUNNING,
                                                Conf.MODE_READ_WRITE);
                    LOGGER.debug("start trans  --> th:" + th);


                    LOGGER.debug("set namespace -->");
                    maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");
                    LOGGER.debug("set namespace --> OK");

                    LOGGER.debug("delete   --> ");
                    maapi.delete(th, "/servers/server{www}");
                    LOGGER.debug("delete  --> OK");

                    LOGGER.debug("setElem  --> ");
                    maapi.setElem(th, new ConfIPv4(192,168,0,1),
                                  "/servers/server{ssh}/ip");
                    LOGGER.debug("setElem  --> OK");

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
                    maapi.setElem(th, new ConfIPv4(192,168,0,1),
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
                    LOGGER.debug("MacAddr= "+macaddr);
                    maapi.setElem(th, macaddr, "/servers/server{ssh}/macaddr");
                    ConfOID oid = new ConfOID("1.1.6.1.24961.1.0");
                    LOGGER.debug("Oid= "+oid);
                    maapi.setElem(th, oid, "/servers/server{ssh}/snmpref");
                    ConfOctetList prefixmask=
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
            },


            /************************************************************
             * Test 3
             * test get attrs
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

                    ConfAttributeValue attrTag = new ConfAttributeValue(ConfAttributeType.TAGS, new ConfList(new ConfObject[] {new ConfBuf("DummyAttrTag")}));
                    ConfAttributeValue attrAnnot = new ConfAttributeValue(ConfAttributeType.ANNOTATION, new ConfBuf("DummyAttrAnnotation"));
                    ConfAttributeValue attrInactive = new ConfAttributeValue(ConfAttributeType.INACTIVE, new ConfBuf("DummyAttrInactive"));

                    maapi.setAttr(th, attrTag, "/servers/server{ssh}");
                    maapi.setAttr(th, attrAnnot, "/servers/server{ssh}");
//                  maapi.setAttr(th, attrInactive, "/servers/server{ssh}");

                    ConfAttributeValue[] attribs = null;
                    attribs = maapi.getAttrs(th, null, "/servers/server{ssh}");
                    for (ConfAttributeValue val : attribs) {
                        if (ConfAttributeType.TAGS.equals(val.getAttributeType())) {
                                String tagstr = ((ConfBuf)((ConfList) val.getAttributeValue()).elements()[0]).toString();
                                if (!attrTag.getAttributeValue().toString().equals(tagstr)) {
                                        failed("Tagvalue is : " + tagstr + " should be : " + attrTag.getAttributeValue().toString());
                                }
                        } else if (ConfAttributeType.ANNOTATION.equals(val.getAttributeType())) {
                                String annotstr = ((ConfBuf) val.getAttributeValue()).toString();
                                if (!attrAnnot.getAttributeValue().toString().equals(annotstr)) {
                                        failed("Annotation value is : " + annotstr + " should be : " + attrAnnot.getAttributeValue().toString());
                                }
                        }
                    }
                    attribs = maapi.getAttrs(th, new ConfAttributeType [] {}, "/servers/server{ssh}");
                    for (ConfAttributeValue val : attribs) {
                        if (ConfAttributeType.TAGS.equals(val.getAttributeType())) {
                                String tagstr = ((ConfBuf)((ConfList) val.getAttributeValue()).elements()[0]).toString();
                                if (!attrTag.getAttributeValue().toString().equals(tagstr)) {
                                        failed("Tagvalue is : " + tagstr + " should be : " + attrTag.getAttributeValue().toString());
                                }
                        } else if (ConfAttributeType.ANNOTATION.equals(val.getAttributeType())) {
                                String annotstr = ((ConfBuf) val.getAttributeValue()).toString();
                                if (!attrAnnot.getAttributeValue().toString().equals(annotstr)) {
                                        failed("Annotation value is : " + annotstr + " should be : " + attrAnnot.getAttributeValue().toString());
                                }
                        }
                    }
                    attribs = maapi.getAttrs(th, new ConfAttributeType [] {ConfAttributeType.ANNOTATION}, "/servers/server{ssh}");
                    for (ConfAttributeValue val : attribs) {
                        if (ConfAttributeType.ANNOTATION.equals(val.getAttributeType())) {
                                String annotstr = ((ConfBuf) val.getAttributeValue()).toString();
                                if (!attrAnnot.getAttributeValue().toString().equals(annotstr)) {
                                        failed("Annotation value is : " + annotstr + " should be : " + attrAnnot.getAttributeValue().toString());
                                }
                        }
                    }
                    attribs = maapi.getAttrs(th, new ConfAttributeType [] {ConfAttributeType.ANNOTATION, ConfAttributeType.TAGS}, "/servers/server{ssh}");
                    for (ConfAttributeValue val : attribs) {
                        if (ConfAttributeType.TAGS.equals(val.getAttributeType())) {
                                String tagstr = ((ConfBuf)((ConfList) val.getAttributeValue()).elements()[0]).toString();
                                if (!attrTag.getAttributeValue().toString().equals(tagstr)) {
                                        failed("Tagvalue is : " + tagstr + " should be : " + attrTag.getAttributeValue().toString());
                                }
                        } else if (ConfAttributeType.ANNOTATION.equals(val.getAttributeType())) {
                                String annotstr = ((ConfBuf) val.getAttributeValue()).toString();
                                if (!attrAnnot.getAttributeValue().toString().equals(annotstr)) {
                                        failed("Annotation value is : " + annotstr + " should be : " + attrAnnot.getAttributeValue().toString());
                                }
                        }
                    }

                    maapi.applyTrans(th, false);
                    maapi.finishTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },


            /************************************************************
             * Test 4
             * getObject
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


                    ConfObject[] result = maapi.getObject(th, "/servers/server{ssh}");
                    LOGGER.debug("Array is = "+Arrays.toString(result));
                    maapi.validateTrans(th, false, true);
                    maapi.prepareTrans(th);
                    maapi.commitTrans(th);
                    maapi.endUserSession();
                    s.close();
                }
            },


            /***********************************************************
             * Test 5
             * auth callback throwing DpCallbackExtendedException
             */
            new TestCase() {
                // public boolean skip() { return true; }
                public void test() throws Exception {
                    Socket s = new Socket("localhost", Conf.PORT);
                    Maapi maapi = new Maapi(s);
                    maapi.addNamespace(new smp());
                    maapi.startUserSession("admin", InetAddress.getLocalHost(),
                                           "maapi", new String[] {"admin"},
                                           MaapiUserSessionFlag.PROTO_TCP);

                    MaapiAuthentication mauth =
                        maapi.authenticate("xxx", "oper");
                    LOGGER.debug ("user xxx status valid = " + mauth.isValid());
                    LOGGER.debug ("user xxx reason = " + mauth.getReason());
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
        //     tests[i].doit();


        int from = 0;
        int to   = tests.length;

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


        LOGGER.debug("START ALL TESTS ********************");
        //Run all the Testcases
        for(i = from ; i < to ; i++)
            tests[i].doit();

        // /************************************************************
        //  * Summary
        //  */
        // LOGGER.debug("---------------------------------------");
        // LOGGER.debug("  Passed:  " + test.pass);
        // LOGGER.debug("  Failed:  " + test.fail);
        // LOGGER.debug("  Skipped: " + test.skip);
        // LOGGER.debug("---------------------------------------");
        // if (test.fail == 0)
        //     System.exit(0);
        // else
        //     System.exit(1);


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
            LOGGER.debug("failed");
            LOGGER.debug("    '"+e.toString()+"'");
            e.printStackTrace();
        }
    }

    private static void failed(String reason) {
        if (!test.processed) {
            test.fail++;
            test.processed = true;
            LOGGER.debug("failed");
            LOGGER.debug("    '"+reason+"'");
        }
    }
}

