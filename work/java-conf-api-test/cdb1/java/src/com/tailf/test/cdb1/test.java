package com.tailf.test.cdb1;
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
import java.util.EnumSet;
import java.util.Arrays;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbLockType;

import com.tailf.cdb.CdbPhase;
import com.tailf.cdb.CdbSession;
import com.tailf.cdb.CdbTxId;
import com.tailf.conf.*;

import org.apache.log4j.Logger;

import com.tailf.test.cdb1.namespaces.*;

public class test {
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;
    private static Logger LOGGER = Logger.getLogger(test.class);

    static public void main(String args[]) {
        int i;

        LOGGER.debug("---------------------------------------");
        LOGGER.debug("Java CDB tests");
        LOGGER.debug("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * new Cdb
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    s.close();
                }
            },

            /************************************************************
             * Test 1
             * startSession
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new mtest());
                    sess.endSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 2
             * startSession more
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSession sess1= cdb.startSession();
                    sess1.setNamespace( new mtest());

                    try {
                        CdbSession sess2 = cdb.startSession();
                        LOGGER.debug("expected error from second call "+
                                    "to StartSession()");
                        System.exit(1);
                    } catch (ConfException e) {
                        // LOGGER.debug("***"+e.getMessage());
                        if (!e.getMessage().equals("must call END_SESSION " +
                                                   "before NEW_SESSION")) {
                            failed(e);
                        }
                    }
                    sess1.endSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 3
             * cd
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSession sess = cdb.startSession();
                    sess.setNamespace( new mtest());
                    sess.cd( new ConfPath("/mtest/a_number"));

                    try {
                        sess.cd( new ConfPath("/mtest/not_exist"));
                        LOGGER.debug("expected error from cd to path " +
                                    "mtest/not_exist");
                        System.exit(1);
                    } catch (ConfException e) {
                        //LOGGER.debug("***"+e.getMessage());
                        if (!e.getMessage().equals("Bad path element " +
                                                   "\"not_exist\" after:" +
                                                   " /mtest")) {
                            failed(e);
                        }
                    }
                    s.close();
                }
            },


            /************************************************************
             * Test 4
             * pushd, popd, getcwd
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new mtest());
                    sess.cd( new ConfPath("/mtest/a_number"));
                    String cwd = sess.getcwd();
                    LOGGER.debug("cwd= "+cwd);
                    sess.pushd( new ConfPath("/mtest/servers/server"));
                    LOGGER.debug("cwd= "+ sess.getcwd());
                    sess.popd();
                    LOGGER.debug("cwd= "+ sess.getcwd());
                    try {
                        sess.popd();
                        LOGGER.debug("cwd= "+ sess.getcwd());
                        LOGGER.debug("popd() on empty stack should "  +
                                     "have failed");
                        System.exit(1);
                    } catch (ConfException e) {
                        //LOGGER.debug("***"+e.getMessage());
                        if (!e.getMessage().equals("Path stack empty")) {
                            failed(e);
                        }
                    }
                    s.close();
                }
            },


            /************************************************************
             * Test 5
             * exists
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new mtest());
                    ConfPath path=  new ConfPath("/mtest/a_number");

                    boolean flag = sess.exists(path);

                    if  (!flag) {
                        failed("exists("+path+") --> "+flag);
                    }
                    try {
                        path = new ConfPath("/servers/server{www}/foo/bar/zar");
                        flag = sess.exists( path );
                        LOGGER.debug("exists() on non-existent path " +
                                     "should have failed");
                        System.exit(1);
                    } catch (ConfException e) {
                        if (e.getErrorCode() != ErrorCode.ERR_BADPATH){
                            failed(e);
                        }
                    }
                    path = new ConfPath("/cs:system");
                    flag = sess.exists( path );
                    if  (!flag) {
                        failed("exists("+path+") --> "+flag);
                    }

                    s.close();
                }
            },

            /************************************************************
             * Test 6
             * isdefault
             */
            new TestCase() {


                public void test() throws Exception {

                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);


                    CdbSession sess =
                        cdb.startSession(CdbDBType.CDB_OPERATIONAL,
                                         EnumSet.of(CdbLockType.LOCK_REQUEST)
                                         );

                    sess.setNamespace( new mtest());
                    ConfPath path =  new ConfPath("/mtest/c_oper/c_number");

                    boolean flag = sess.isDefault(path);

                    if  (!flag) {
                        failed("isDefault("+path+") --> "+flag);
                    }else{
                        LOGGER.debug("isDefault(" + path + ") -->" + flag);
                    }


                    try {
                        sess.setElem(new ConfInt64(77),path);
                        //path = new ConfPath("/mtest/types/c_int64");
                        flag = sess.isDefault( path );

                        if(flag){
                            failed("isDefault("+ path + ") -->" + flag);
                        }else{
                            LOGGER.debug("isDefault(" + path + ") -->" + flag);
                        }
                        //System.exit(1);
                    } catch (ConfException e) {
                        //LOGGER.debug("***"+e.getMessage());
                        if (!e.getMessage().equals("Bad path element" +
                                                   " \"foo\" after: /")) {
                            failed(e);
                        }
                    }
                    path= new ConfPath("/cs:system");
                    flag = sess.exists( path );
                    if  (!flag) {
                        failed("exists("+path+") --> "+flag);
                    }

                    s.close();
                }
            },



            /************************************************************
             * Test 7
             * getElem
             */
            new TestCase() {


                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    boolean flag;

                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new mtest());
                    ConfPath path1=
                        new ConfPath("/mtest/servers/server{%s}/port",
                                     new Object[] { new String("www") });
                    ConfPath path2=
                        new ConfPath("/mtest/servers/server{%x}/port",
                                     new Object[] {
                                         new ConfBuf(
                                                     new String("smtp")
                                                     .getBytes())
                                     });
                    flag = sess.exists(path1);
                    if  (!flag) {
                        failed("exists("+path1+") --> "+flag);
                    }
                    flag= sess.exists(path2);
                    if  (!flag) {
                        failed("exists("+path2+") --> "+flag);
                    }
                    ConfValue val = sess.getElem(path1);

                    if ( ! val.equals( new ConfUInt16(80)))
                        failed("getElem("+path1+") --> "+val);

                    val = sess.getElem(path2);
                    if ( ! val.equals( new ConfUInt16(25)))
                        failed("getElem("+path2+") --> "+val);


                    // find index of first server
                    int ix = sess.nextIndex("/mtest/servers/server{\"\"}");
                    ConfValue val2 =
                        sess.getElem("/mtest/servers/server[%d]/port",
                                     new Object[] { new Integer(ix) });

                    // LOGGER.debug("val2 = " + val2);

                    // first server is supposed to be 'smtp'
                    if ( ! val2.equals( val ))
                        failed("getElem(\"/mtest/servers/server[" + ix +
                               "]/port\") --> "+val2+" != "+val);

                    s.close();
                }
            },


            /************************************************************
             * Test 8
             * numInstances
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    boolean flag;
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new mtest());
                    ConfPath path1=  new ConfPath("/mtest/servers");

                    flag = sess.exists(path1);
                    if  (!flag) {
                        failed("exists("+path1+") --> "+flag);
                    }
                    int num = sess.numInstances(path1);
                    if (num != 1)
                        failed("numInstances("+path1+") --> "+num);

                    ConfPath path2=  new ConfPath("/mtest/servers/server");
                    flag= sess.exists(path2);
                    if  (!flag) {
                        failed("exists("+path2+") --> "+flag);
                    }
                    num = sess.numInstances(path2);
                    if (num != 3)
                        failed("numInstances("+path2+") --> "+num);

                    s.close();
                }
            },

            /************************************************************
             * Test 9
             * CDB_OPERATIONAL: create, setElem, delete
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    boolean flag;

                    // start session towards operational data
                    CdbSession sess =
                        cdb.startSession(CdbDBType.CDB_OPERATIONAL);

                    sess.setNamespace( new optest());

                    ConfPath path1=
                        new ConfPath("/optest/optest-stats/servers/server{%s}",
                                     new Object[] {
                                         new String("www")
                                     });


                    flag = sess.exists(path1);
                    if  (flag) {
                        failed("exists("+path1+") --> "+flag);
                    }
                    sess.create(path1);
                    flag= sess.exists(path1);
                    if  (!flag) {
                        failed("exists("+path1+") --> "+flag);
                    }

                    ConfPath path2 = ((ConfPath)path1.clone())
                        .append("recv_packets");

                    sess.setElem(new ConfInt64(42), path2);

                    ConfPath path3= ((ConfPath)path1.clone())
                        .append("sent_packets");
                    sess.setElem(new ConfInt64(1103), path3);

                    // read operational
                    ConfValue val= sess.getElem(path2);
                    if (! val.equals( new ConfInt64(42)))
                        failed("getElem("+path2+") --> "+val);

                    val = sess.getElem(path3);

                    if (! val.equals( new ConfInt64(1103)))
                        failed("getElem("+path2+") --> "+val);

                    // delete it
                    sess.delete(path1);
                    flag= sess.exists(path1);
                    if  (flag) {
                        failed("exists("+path1+") --> "+flag);
                    }

                    sess.endSession();

                    s.close();
                }
            },


            /************************************************************
             * Test 10
             * CDB_OPERATIONAL: setObject
             */
            new TestCase() {

                public boolean skip() {
                    LOGGER.debug("NOTE: THIS MUST BE FIXED");
                    return false;
                }

                public void test() throws Exception {
                    /* NOTE MUST FIX MUST FIX.. */
                    LOGGER.debug("TEST 10 setObject() -->");
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    boolean flag;

                    LOGGER.debug("Path1 -->");
                    ConfPath path1 =
                        new ConfPath("/optest/optest-stats/servers/server{%s}",
                                     new String("www2") );

                    LOGGER.debug("Path1 --> OK");


                    // start session towards operational data
                    //Clean up if exists so we could run the
                    //test case without stoping Confd.
                    CdbSession sess =
                        cdb.startSession(CdbDBType.CDB_OPERATIONAL);

                    sess.setNamespace( new optest());


                    if(sess.exists(path1))
                        sess.delete( path1 );

                    sess.endSession();
                    //*********OK START TEST

                    LOGGER.debug("START TEST-->");

                    sess = cdb.startSession(CdbDBType.CDB_OPERATIONAL);
                    sess.setNamespace( new optest());

                    // path does not exist yet
                    flag = sess.exists(path1);
                    if  (flag) {
                        failed("exists("+path1+") --> "+flag);
                    }
                    LOGGER.debug("PASSED -->");


                    // set object
                    sess.setObject(new ConfValue[] {
                            null, /* dont cares about key if
                                     its already given in path*/
                            new ConfInt64(55),
                            new ConfInt64(77) },
                        path1);

                    // path exists now
                    flag = sess.exists(path1);
                    if  (!flag) {
                        failed("exists("+path1+") --> "+flag);
                    }
                    LOGGER.debug("PASSED2 --> OK");

                    // read operational
                    ConfPath path2 =
                        ((ConfPath)path1.clone()).append("recv_packets");

                    LOGGER.debug("CLONE:" + path1.clone());
                    LOGGER.debug("CLONE2:" + path2);

                    ConfPath path3=
                        ((ConfPath)path1.clone()).append("sent_packets");

                    ConfValue val= sess.getElem(path2);
                    if (! val.equals( new ConfInt64(55)))
                        failed("getElem("+path2+") --> "+val);

                    LOGGER.debug("path2:" + path2);
                    LOGGER.debug("path3:" + path3);

                    val = sess.getElem(path3);
                    if (! val.equals( new ConfInt64(77)))
                        failed("getElem("+path2+") --> "+val);

                    // set object where no key is given in path
                    path1=
                        new ConfPath("/optest/optest-stats/servers/server");

                    // set object 1  - overwrites old one
                    sess.setObject(new ConfValue[] {
                            new ConfBuf("www"),
                            new ConfInt64(55),
                            /* will be Cdb.CDB_NO_EXISTS */
                            null }, path1);
                    // set object 2 - create ftp server
                    sess.setObject(new ConfValue[] {
                            new ConfBuf("ftp"),
                            new ConfInt64(12),
                            new ConfInt64(13) }, path1);

                     ConfPath www_path =
                         new ConfPath("/optest/optest-stats/servers/server{%s}",
                                      new String("www") );

                    ConfPath www_recv_path=
                        www_path.copyAppend("recv_packets");

                    ConfPath www_sent_path=
                        www_path.copyAppend("sent_packetsz");

                    val= sess.getElem( www_recv_path  );
                    if (! val.equals( new ConfInt64(55)))
                        failed("getElem("+ www_recv_path +") --> "+val);

                    try {
                        val = sess.getElem( www_sent_path );

                        LOGGER.debug("getElem() on non existing element " +
                                     www_sent_path +
                                     " should throw exception");
                        System.exit(1);
                    } catch (ConfException e) {
                        if(!e.getErrorCode().equals(ErrorCode.ERR_BADPATH))
                            failed(e);
                    }
                    LOGGER.debug("HHHHHH");

                    // check 'ftp' elements


                    ConfPath ftp_path =
                        new ConfPath("/optest/optest-stats/servers/server{%s}",
                                     "ftp" );

                    ConfPath ftp_recv_path =
                        ftp_path.copyAppend("recv_packets");
                    ConfPath ftp_sent_path =
                        ftp_path.copyAppend("sent_packets");

                    val= sess.getElem( ftp_recv_path  );
                    if (! val.equals( new ConfInt64(12)))
                        failed("getElem("+ ftp_recv_path +") --> "+val);


                    val= sess.getElem( ftp_sent_path  );
                    if (! val.equals( new ConfInt64(13)))
                        failed("getElem("+ ftp_sent_path +") --> "+val);

                    // delete all
                    sess.delete( www_path );
                    //sess.delete( ftp_path );

                    sess.endSession();

                    s.close();
                }
            },


            /************************************************************
             * Test 11
             * CDB_OPERATIONAL: setValues
             */
            new TestCase() {
                //public boolean skip() { return true; }
                public void test() throws Exception {
                    LOGGER.debug("TEST 11 -->");
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    optest cs = new optest();

                    // start session towards operational data
                    CdbSession sess=
                        cdb.startSession(CdbDBType.CDB_OPERATIONAL);

                    sess.setNamespace(cs);

                    ConfPath path1=
                        new ConfPath("/optest/optest-stats/servers/server{%s}",
                                     new Object[] {
                                         new String("www")
                                     });

                    // ConfPath path1=
                    //     new ConfPath("optest:/servers/server{www}");
                    // //               new Object[] { new String("www") });



                    ConfXMLParam[] param1 =
                        new ConfXMLParam[] {
                        new ConfXMLParamValue(cs.hash(),optest._name,
                                              new ConfBuf("www")),
                        new ConfXMLParamValue(cs.hash(),optest._recv_packets,
                                              new ConfInt64(23)),
                        new ConfXMLParamValue(cs.hash(),optest._sent_packets,
                                              new ConfNoExists())
                    };


                    ConfXMLParam[] param2 =
                        new ConfXMLParam[] {
                        new ConfXMLParamValue(cs.hash(),optest._recv_packets,
                                              new ConfInt64(26)),
                        new ConfXMLParamValue(cs.hash(),optest._sent_packets,
                                              new ConfInt64(27))
                    };




                    sess.setValues(param2,
                                   "/optest/optest-stats/servers/server{www}");


                    // check 'www' elements
                    ConfPath www_recv_path =
                        path1.copyAppend("recv_packets");

                    ConfPath www_sent_path =
                        path1.copyAppend("sent_packets");

                    ConfValue val = sess.getElem( www_recv_path  );
                    if (! val.equals( new ConfInt64(26)))
                        failed("getElem("+ www_recv_path +") --> "+val);

                }
            },

            /************************************************************
             * Test 11
             * CDB waitStart() and getPhase()
             */
            new TestCase() {


                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);

                    cdb.waitStart();

                    CdbPhase phase = cdb.getPhase();
                    LOGGER.debug("getPhase() --> "+phase);

                    s.close();
                }
            },

            /************************************************************
             * Test 12
             * CDB getTxId()
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);

                    CdbTxId txid1 = cdb.getTxId();
                    LOGGER.debug("txid1 --> "+txid1);
                    CdbTxId txid2 = cdb.getTxId();
                    LOGGER.debug("txid1 --> "+txid1);
                    if ( txid1.equals( txid2 )) {
                        LOGGER.debug("txid1 == txid2 ");
                    } else
                        failed("compared CdbTxId: "+txid1+ " != "+txid2);

                    s.close();
                }
            }


        };


        /************************************************************
         * Run the tests
         */

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
        LOGGER.debug("running test from "+ from  + ",to:" + to);

        test.testnr = from;
        for(i = from ; i < to ; i++)
            tests[i].doit();

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

