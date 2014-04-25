package com.tailf.test.cdb3;

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
import java.util.List;

import com.tailf.cdb.*;
import com.tailf.conf.*;
import com.tailf.test.cdb3.namespaces.*;
import org.apache.log4j.Logger;

public class test {
    public static int testnr = 0;
    public static int pass = 0;
    public static int fail = 0;
    public static int skip = 0;
    public static boolean processed = false;
    private static Logger LOGGER = Logger.getLogger(test.class);


    static String leaf_names[] = {
        "index",
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "t",
        "u",
        "v",
        "w",
        "x",
        "y",
        "z"};
    static int num_instances1 = 50;
    static int num_instances2 = 500;
    static int num_elems1 = leaf_names.length;
    static int num_elems2 = 2;
    static String data1 = "/data1";
    static String data2 = "/data2";


    static public void main(String args[]) {
        int i;

        LOGGER.info("---------------------------------------");
        LOGGER.info("Java CDB tests");
        LOGGER.info("---------------------------------------");

        TestCase[] tests = new TestCase[] {

            /************************************************************
             * Test 0
             * CdbSession getObject
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    cdb.addNamespace(new bulk());
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new bulk());
                    sess.cd(data1);
                    int n = sess.numInstances("entry");
                    if (n != num_instances1) {
                        failed("expected " + num_instances1 +
                               " instances but got " + n);
                    }

                    for (int i=0; i<n; i++) {
                        ConfObject[] objArr =
                            sess.getObject(num_elems1, "entry[%d]", i);

                        if (num_elems1 != objArr.length) {
                            failed("expected " + num_elems1 +
                                   " elements but got " + objArr.length);
                        }

                        for (int k=0; k<objArr.length; k++) {
                            ConfObject obj = objArr[k];
                            if (k==0) {
                                if (!obj.toString()
                                    .equals(String.valueOf(i+1))) {
                                    failed("expected elemvalue " +
                                           (i+1) + " but got " + obj);
                                }
                            } else {
                                if (!"12345678901234567890"
                                    .equals(obj.toString())) {
                                    failed("expected elemvalue " +
                                           "12345678901234567890 but got " +
                                           obj);
                                }
                            }
                        }
                    }

                    sess.cd(data2);
                    n = sess.numInstances("entry");
                    if (n != num_instances2) {
                        failed("expected " +
                               "num_instances2" +
                               " instances but got " + n);
                    }

                    for (int i=0; i<n; i++) {
                        ConfObject[] objArr =
                            sess.getObject(num_elems2, "entry[%d]", i);

                        if (num_elems2 != objArr.length) {
                            failed("expected " + num_elems1 +
                                   " elements but got " + objArr.length);
                        }

                        for (int k=0; k<objArr.length; k++) {
                            ConfObject obj = objArr[k];
                            if (k==0) {
                                if (!obj.toString()
                                    .equals(String.valueOf(i+1))) {
                                    failed("expected elemvalue " +
                                           (i+1) + " but got " + obj);
                                }
                            } else {
                                if (!"12345678901234567890"
                                    .equals(obj.toString())) {
                                    failed("expected elemvalue " +
                                           "12345678901234567890 but got " +
                                           obj);
                                }
                            }
                        }

                    }
                    sess.endSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 1
             * CdbSession getObjects
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    cdb.addNamespace(new bulk());
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace( new bulk());
                    sess.cd(data1);
                    int n = sess.numInstances("entry");
                    if (n != num_instances1) {
                        failed("expected " + num_instances1 +
                               " instances but got " + n);
                    }

                    List<ConfObject[]> objList =
                        sess.getObjects(num_elems1, 0, num_instances1, "entry");

                    if (objList.size() != num_instances1) {
                        failed("expected " + num_instances1 +
                               " instances but got " + n);
                    }

                    int i = 0;
                    for (ConfObject[] objArr : objList) {

                        if (num_elems1 != objArr.length) {
                            failed("for instance "+i+
                                   " expected " + num_elems1 +
                                   " elements but got " + objArr.length);
                        }

                        for (int k=0; k<objArr.length; k++) {
                            ConfObject obj = objArr[k];
                            if (k==0) {
                                if (!obj.toString().equals(String.valueOf(i+1))) {
                                    failed("expected elemvalue " + (i+1) + " but got " + obj);
                                }
                            } else {
                                if (!"12345678901234567890".equals(obj.toString())) {
                                    failed("expected elemvalue 12345678901234567890 but got " + obj);
                                }
                            }
                        }
                        i++;
                    }

                    sess.cd(data2);
                    n = sess.numInstances("entry");
                    if (n != num_instances2) {
                        failed("expected " + num_instances2 + " instances but got " + n);
                    }


                    objList = sess.getObjects(num_elems2, 0, num_instances2, "entry");

                    if (objList.size() != num_instances2) {
                        failed("expected " + num_instances2 + " instances but got " + n);
                    }

                    i = 0;
                    for (ConfObject[] objArr : objList) {

                        if (num_elems2 != objArr.length) {
                            failed("for instance "+i+" expected " + num_elems2 + " elements but got " + objArr.length);
                        }

                        for (int k=0; k<objArr.length; k++) {
                            ConfObject obj = objArr[k];
                            if (k==0) {
                                if (!obj.toString().equals(String.valueOf(i+1))) {
                                    failed("expected elemvalue " + (i+1) + " but got " + obj);
                                }
                            } else {
                                if (!"12345678901234567890".equals(obj.toString())) {
                                    failed("expected elemvalue 12345678901234567890 but got " + obj);
                                }
                            }
                        }
                        i++;
                    }

                    sess.endSession();
                    s.close();
                }
            },

            /************************************************************
             * Test 2
             * CdbSession loadFile
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    cdb.addNamespace(new bulk());

                    CdbSession sess =
                        cdb.startSession(CdbDBType.CDB_OPERATIONAL);

                    sess.setNamespace( new bulk());
                    sess.cd("/data3");
                    int n = sess.numInstances("entry");

                    if (n != 0) {
                        failed("expected 0 elements but found " + n);
                    }

                    sess.loadFile("bulkload.xml");
                    n = sess.numInstances("entry");
                    if (n != 5) {
                        failed("expected 5 elements but found " + n);
                    }

                    sess.endSession();
                    s.close();
                }
            },


            /************************************************************
             * Test 3
             * CdbSession getCase, setCase
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    cdb.addNamespace(new bulk());
                    CdbSession sess =
                        cdb.startSession(CdbDBType.CDB_OPERATIONAL);

                    sess.setNamespace( new bulk());
                    sess.cd("/data4");

                    //              sess.loadFile("choice_1.xml");
                    sess.setCase("choice1", "case3", "/data4");

                    ConfObject obj = sess.getCase("choice1", "/data4");

                    if (!"bulk:case3".equals(obj.toString())) {
                        failed("expected bulk:case3 but got " + obj);
                    }

                    sess.setCase("choice1", "case2", "/data4");

                    obj = sess.getCase("choice1", "/data4");
                    if (!"bulk:case2".equals(obj.toString())) {
                        failed("expected bulk:case2 but got " + obj);
                    }


                    sess.endSession();
                    s.close();
                }
            },


            /************************************************************
             * Test 4
             * CdbSession getValues with ConfXMLParam
             */
            new TestCase() {
                public void test() throws Exception {
                    Socket s = new Socket(host, Conf.PORT);
                    Cdb cdb = new Cdb("test",s);
                    ConfNamespace b = new bulk();
                    cdb.addNamespace(b);
                    CdbSession sess= cdb.startSession();
                    sess.setNamespace(b);
                    sess.cd(data1);

                    ConfXMLParam[] params = new ConfXMLParam[] {
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[0]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[1]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[2]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[3]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[4]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[5]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[6]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[7]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[8]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[9]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[10]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[11]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[12]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[13]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[14]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[15]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[16]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[17]),
                                             cdb.getNsList()),

                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[18]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[19]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[20]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[21]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[22]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[23]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[24]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[25]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[26]),
                                             cdb.getNsList())
                    };

                    ConfXMLParam[] resTag = null;
                    int offset = 0;

                    try{

                        resTag =
                            sess.getValues(params,
                                       new ConfPath("entry[%d]", 0));

                    }catch(ConfException e){
                        LOGGER.error("",e);
                        failed(e.toString());
                    }

                    for (ConfXMLParam param : resTag) {
                        if (offset==0) {
                            if (!param.getValue().toString()
                                .equals(String.valueOf(1))) {
                                failed("expected elemvalue 1 but got " +
                                       param.getValue());
                            }
                        } else {
                            if (!"12345678901234567890"
                                .equals(param.getValue().toString())) {
                                failed("expected elemval 12345678901234567890"
                                       +  "but got " + param.getValue());
                            }
                        }
                        offset++;
                    }



                    params = new ConfXMLParam[] {
                        new ConfXMLParamCdbStart(b.hash(),
                                                 b.toHash("entry"),
                                                 cdb.getNsList(), 0),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[0]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[1]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[2]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[3]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[4]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[5]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[6]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[7]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[8]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[9]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[10]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[11]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[12]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[13]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[14]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[15]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[16]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[17]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[18]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[19]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[20]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[21]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[22]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[23]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[24]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[25]),
                                             cdb.getNsList()),
                        new ConfXMLParamLeaf(b.hash(),
                                             b.toHash(leaf_names[26]),
                                             cdb.getNsList()),
                        new ConfXMLParamStop(b.hash(),
                                             b.toHash("entry"),
                                             cdb.getNsList())
                    };


                    resTag = sess.getValues(params, new ConfPath("."));

                    if (resTag.length != num_elems1+2) {
                        failed ("expected " + num_elems1 +
                                " elements but got " + resTag.length);
                    }

                    offset = 0;
                    for (ConfXMLParam param : resTag) {
                        if (offset==0) {
                            continue;
                        } else if (offset==1) {
                            if (!param.getValue().toString()
                                .equals(String.valueOf(1))) {
                                failed("expected elemvalue 1 but got " +
                                       param.getValue());
                            }
                        } else if (offset <= num_elems1) {
                            if (!"12345678901234567890"
                                .equals(param.getValue().toString())) {
                                failed("expected elemval 12345678901234567890"
                                       + " but got " +
                                       param.getValue());
                            }
                        }
                        offset++;
                    }

                    sess.endSession();
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
            LOGGER.info("failed");
            LOGGER.info("    '"+e.toString()+"'");
            e.printStackTrace();
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

