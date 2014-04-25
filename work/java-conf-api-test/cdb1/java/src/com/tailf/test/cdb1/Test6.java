package com.tailf.test.cdb1;

import java.net.Socket;
import java.io.IOException;
import com.tailf.test.cdb1.namespaces.*;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbPhase;
import com.tailf.cdb.CdbSession;
import com.tailf.cdb.CdbTxId;
import com.tailf.conf.*;
import java.util.Arrays;
import org.apache.log4j.Logger;


public class Test6 {
    private static Logger LOGGER = Logger.getLogger(Test6.class);
    public static void main(String[] arg) throws Exception{
        //new Test6().test();
        new Test6().testp();

    }

    public void testp(){
        try{
            Socket s = new Socket("localhost", Conf.PORT);
            Cdb cdb = new Cdb("test",s);

            ConfPath path = new ConfPath("/mtest:mtest/servers/server{\"\"}");
            ConfObject[] pp = path.getKP();
            LOGGER.info(Arrays.toString(pp));
            LOGGER.info("key:" + pp[0]);
            ConfKey key = (ConfKey) pp[0];

            LOGGER.info("ELEMS:" +key.elements().length);
            if(key.elements()[0] instanceof ConfBuf)
                LOGGER.info("it is a buf");
            LOGGER.info("XX:" + (ConfBuf)key.elements()[0]);

            String ss = new String("\0");
            LOGGER.info("ss:" + ss.getBytes().length);


        }catch(Exception e){
            LOGGER.error("",e);
        }

    }

    public void test()  {
        Socket s = null;
        try{
            s = new Socket("localhost", Conf.PORT);
            Cdb cdb = new Cdb("test",s);
            boolean flag;

            CdbSession sess =
                cdb.startSession();

        sess.setNamespace( new mtest());

        ConfPath path1 =
            new ConfPath("/mtest/servers/server{%s}/port",
                         new Object[] {
                             new String("www")
                         });
        ConfPath path2 =
            new ConfPath("/mtest/servers/server{%x}/port",
                         new Object[] {
                             new ConfBuf(new String("smtp").getBytes())
                         });

        flag = sess.exists(path1);
        if  (!flag) {
            LOGGER.info("exists("+path1+") --> "+flag);
        }
        flag = sess.exists(path2);
        if  (!flag) {
            LOGGER.info("exists("+path2+") --> "+flag);
        }
        ConfValue val = sess.getElem(path1);

        if ( ! val.equals( new ConfUInt16(80)))
            LOGGER.info("getElem("+path1+") --> "+val);

        val = sess.getElem(path2);
        if ( ! val.equals( new ConfUInt16(25)))
            LOGGER.info("getElem("+path2+") --> "+val);


        // find index of first server
        int ix = sess.nextIndex("/mtest/servers/server{\"\"}");
        ConfValue val2 =
            sess.getElem("/mtest/servers/server[%d]/port",
                         new Object[] { new Integer(ix) });

        // System.err.println("val2 = " + val2);

        // first server is supposed to be 'smtp'
        if ( ! val2.equals( val ))
            LOGGER.info("getElem(\"/mtest/servers/server[" + ix +
                               "]/port\") --> "+val2+" != "+val);

        }catch(Exception e){
            LOGGER.info("",e);
        }finally{
            try{
            s.close();
            }catch(IOException e){}
        }

    }

     public void test0() throws Exception {
         Socket s = new Socket("localhost", Conf.PORT);
         Cdb cdb = new Cdb("test",s);
         boolean flag;

         CdbSession sess =
             cdb.startSession();


         sess.setNamespace(new mtest());

         ConfPath path1 =
             //new ConfPath("/mtest/servers/server{\"\"}");
             new ConfPath("/mtest/servers");
         LOGGER.info(path1.toString());

        ConfPath path2 =
            new ConfPath("/mtest/servers/server{%x}/port",
                         new Object[] {
                             new ConfBuf(new String("smtp").getBytes())
                         });

        flag = sess.exists(path1);
        LOGGER.info("exists("+path1+") --> "+flag);

        int num = sess.numInstances("/mtest/servers/server");
        LOGGER.info("num:" + num);

        final mtest ns = new mtest();
        ConfXMLParam[] rows = new ConfXMLParam[9*num];
        int j = 0;
        for(int i = 0; i < num; i++){
            rows[j++] = new ConfXMLParamCdbStart(ns.hash(),
                                                 ns._server,
                                                 i);

            rows[j++] = new ConfXMLParamLeaf(ns.hash(),
                                             ns._name);


            rows[j++] = new ConfXMLParamLeaf(ns.hash(),
                                             ns._ip);


            rows[j++] = new ConfXMLParamLeaf(ns.hash(),
                                             ns._port);

            rows[j++] = new ConfXMLParamCdbStart(ns.hash(),
                                                 ns._foo,0);

            rows[j++] = new ConfXMLParamLeaf(ns.hash(),
                                             ns._bar);

            rows[j++] = new ConfXMLParamLeaf(ns.hash(),
                                             ns._baz);

            rows[j++] = new ConfXMLParamStop(ns.hash(),
                                             ns._foo);

            rows[j++] = new ConfXMLParamStop(ns.hash(),
                                             ns._server);

        }

        ConfXMLParam[] resvalues =
            sess.getValues(rows, path1);

        Arrays.toString(resvalues);

        for(int i = 0; i < resvalues.length; i++){
            ConfXMLParam param = resvalues[i];

            if(i % 9 == 0) LOGGER.info("");
            LOGGER.info(param);
        }

        /*
        for(int i = 0; i < num; i++){
            ConfValue val =
                sess.getValues("/mtest/servers/server[%d]/port",
                             new Object[] { new Integer(ix) });
            ix++;
            LOGGER.info("val:" + val);
            }*/
        //LOGGER.info("Exists:" + num + " entries");
        //int idx = sess.nextIndex(path1);
        //LOGGER.info("nextidx:" + idx);




        /*
        flag = sess.exists(path2);
        if  (!flag) {
            LOGGER.info("exists("+path2+") --> "+flag);
        }
        ConfValue val = sess.getElem(path1);

        if ( ! val.equals( new ConfUInt16(80)))
            LOGGER.info("getElem("+path1+") --> "+val);

        val = sess.getElem(path2);
        if ( ! val.equals( new ConfUInt16(25)))
            LOGGER.info("getElem("+path2+") --> "+val);


        // find index of first server
        int ix = sess.nextIndex("/mtest/servers/server{\"\"}");
        ConfValue val2 =
            sess.getElem("/mtest/servers/server[%d]/port",
                         new Object[] { new Integer(ix) });
        */
        // System.err.println("val2 = " + val2);

        // first server is supposed to be 'smtp'
        /*
        if ( ! val2.equals( val ))
            LOGGER.info("getElem(\"/mtest/servers/server[" + ix +
                               "]/port\") --> "+val2+" != "+val);
        */

        s.close();
    }

    public void test1() throws Exception{
        Socket s = new Socket("localhost", Conf.PORT);
        Cdb cdb = new Cdb("test",s);

        boolean flag;

        CdbSession sess =
            cdb.startSession();


        sess.setNamespace(new mtest());

        //ConfPath path1 =
            //new ConfPath("/mtest/servers/server[%d]");


        //LOGGER.info(path1.toString());


        //flag = sess.exists(path1);
        //LOGGER.info("exists("+path1+") --> "+flag);

        int num = sess.numInstances("/mtest/servers/server");
        LOGGER.info("num:" + num);

        final mtest ns = new mtest();

        ConfXMLParam[] rows =  new ConfXMLParam[]{

            new ConfXMLParamLeaf(ns.hash(),
                                 ns._name),
            new ConfXMLParamLeaf(ns.hash(),
                                 ns._ip),
            new ConfXMLParamLeaf(ns.hash(),
                                 ns._port),
            new ConfXMLParamCdbStart(ns.hash(),
                                     ns._foo,
                                     0),
            new ConfXMLParamLeaf(ns.hash(),
                                 ns._bar),
            new ConfXMLParamLeaf(ns.hash(),
                                 ns._baz),
            new ConfXMLParamStop(ns.hash(),
                                 ns._foo),
        };

        for(int i = 0; i < sess.numInstances("/mtest/servers/server"); i++){
           ConfXMLParam[] retvals =
               sess.getValues(rows, "/mtest/servers/server[%d]",i);
           //LOGGER.info(retvals);
           for(ConfXMLParam param : retvals){
               LOGGER.info(param);
           }

        }
        s.close();


        //ConfXMLParam[] resvalues = sess.getValues(rows, path1);



    }






}