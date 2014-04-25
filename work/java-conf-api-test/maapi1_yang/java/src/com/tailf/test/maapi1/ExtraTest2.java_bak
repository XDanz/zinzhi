package com.tailf.test.maapi1;

import com.tailf.navu.*;

import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.MaapiDiffIterate;
import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.conf.DiffIterateOperFlag;

import com.tailf.test.maapi1.namespaces.mtest;
import java.util.Arrays;
import org.apache.log4j.Logger;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfException;


import com.tailf.conf.*;

import org.junit.runner.*;

import org.apache.log4j.Logger;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;

//import com.tailf.ncs.ns.NcsAlarms;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbSession;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbLockType;

import java.util.List;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

import com.tailf.maapi.MaapiSchemas;


import java.io.IOException;

import java.net.*;

public class ExtraTest2{

    private Socket socket;
    private Socket socket2;
    private Cdb cdb;

    private Maapi maapi;
    private int th;
    private NavuContainer maapiroot;
    private NavuContainer cdbroot;



    private static Logger LOGGER =
        Logger.getLogger(ExtraTest1.class);


    void connect(){
        try{
            socket = new Socket("127.0.0.1",Conf.PORT);
            maapi = new Maapi(socket);

            socket2 = new Socket("127.0.0.1",Conf.PORT);
            cdb = new Cdb("navucdb",socket2);

        }catch(ConfException e){
            throw throwException(e,"Test Failure!");
        }catch(UnknownHostException e){
            throw throwException(e,"Test Failure!");
        }catch(IOException e){
            throw throwException(e,"Test Failure!");
        }
    }



    @Before
        public void init(){
        connect();
    }


    private void session(){

        try{
            maapi.startUserSession("ola",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            th = maapi.startTrans(Conf.DB_RUNNING,
                                  Conf.MODE_READ_WRITE);
        }catch(Exception e){
            fail(e);
        }
    }

    AssertionError throwException(Throwable t,String msg){
        AssertionError ae =
            new AssertionError(msg);
        ae.initCause(t);
        return ae;

    }

    void fail(Throwable e){
        throw throwException(e,"Test Failure!");
    }



    @Test
        public void test1(){
        session();

        try{
            //Create different keys.
            //****************Test 1

            ConfPath ref =
                new ConfPath("/bintest/bindata{%x %x %x}",
                             new ConfHexList("ff:ff:ff"),
                             new ConfHexList("ff:ff:ff"),
                             new ConfHexList("ff:ff:ff"));

            ConfPath path0 =
                new ConfPath("/bintest/bindata{ff:ff:ff ff:ff:ff ff:ff:ff}");

            maapi.create(th,path0);

            assertTrue(maapi.exists(th,ref));

        }catch(Exception e){
            fail(e);
            //throw throwException(e,"Test Failure!");
        }
    }

    @Test
        public void test2() {
        session();

        try{
            ConfPath ref =
                new ConfPath("/bintest/bindata{%x %x %x}",
                             new ConfHexList("ff:5e:5f:ff"),
                             new ConfHexList("ff:5e:4f:ff"),
                             new ConfHexList("ff:4e:ff:ff"));

            ConfObject[] arr = new ConfObject[]{
                new ConfKey(new ConfObject[]{
                        new ConfHexList("ff:5e:5f:ff"),
                        new ConfHexList("ff:5e:4f:ff"),
                        new ConfHexList("ff:4e:ff:ff")
                    }),

                new ConfTag(new mtest().hash(), mtest._bindata),
                new ConfTag(new mtest().hash(), mtest._bintest)
            };

            //maapi.create(th,);
            maapi.create(th, new ConfPath(arr));

            assertTrue(maapi.exists(th,ref ));


        }catch( Exception e){
            throw throwException(e,"Test Failure!");
        }
    }
    @Test
        public void test3() {
        session();
        //****************Test 2
        try{
            ConfPath ref =
                new ConfPath("/bintest/bindata{%x %x %x}",
                             new ConfHexList("ff:ff:ff:7e"),
                             new ConfHexList("ff:ff:ff:6e"),
                             new ConfHexList("ff:ff:ff:5e")
                             );

            ConfPath path1 =
                new ConfPath("/bintest/bindata{%x}",
                             new ConfKey(new ConfObject[]{
                                     new ConfHexList("ff:ff:ff:7e"),
                                     new ConfHexList("ff:ff:ff:6e"),
                                     new ConfHexList("ff:ff:ff:5e")
                                 }));

            maapi.create(th,path1);
            assertTrue(maapi.exists(th, ref));

        }catch( Exception e){
            fail(e);
        }

    }



    @Test
        public void test4() {
        session();

        try{

            //***************'Test 3
            ConfPath ref =
                new ConfPath("/bintest/bindata{%x %x %x}",
                             new ConfHexList("ff:ff:ff:ff:7e"),
                             new ConfHexList("ff:ff:ff:ff:6e"),
                             new ConfHexList("ff:ff:ff:ff:5e"));


            ConfKey key1 =
                new ConfKey(new ConfObject[]{
                        new ConfHexList("ff:ff:ff:ff:7e"),
                        new ConfHexList("ff:ff:ff:ff:6e"),
                    });

            ConfPath path2 =
                new ConfPath("/bintest/bindata{%x %x}",
                             key1, new ConfHexList("ff:ff:ff:ff:5e"));

            maapi.create(th,path2);

            assertTrue(maapi.exists(th,ref));

        }catch( Exception e){
            fail(e);
        }

    }
    @Test
        public void test5(){
        session();
        //***************'Test 4

        try{
            ConfPath ref = new ConfPath("/bintest/bindata{%x %x %x}",
                                        new ConfHexList("ff:ff:ff:ff:7e"),
                                        new ConfHexList("ff:ff:ff:ff:4e"),
                                        new ConfHexList("ff:ff:ff:ff:5e"));

            ConfKey key1 =
                new ConfKey(new ConfObject[]{
                        new ConfHexList("ff:ff:ff:ff:7e"),
                        new ConfHexList("ff:ff:ff:ff:4e"),
                    });

            ConfPath path =
                new ConfPath("/bintest/bindata{%x%x}",
                             key1,
                             new ConfHexList("ff:ff:ff:ff:5e"));

            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));
        }catch( Exception e){
            fail(e);
        }
    }

    @Test
        public void test6(){
        session();
        //***************'Test 5

        try{
            ConfPath ref = new ConfPath("/bintest/bindata{%x %x %x}",
                                        new ConfHexList("ff:ff:ff:ff:6e"),
                                        new ConfHexList("ff:ff:ff:ff:7e"),
                                        new ConfHexList("ff:ff:ff:ff:5e"));

            ConfKey key2 =
                new ConfKey(new ConfObject[]{
                        new ConfHexList("ff:ff:ff:ff:6e"),
                    });

            ConfKey key3 =
                new ConfKey(new ConfObject[]{
                        new ConfHexList("ff:ff:ff:ff:7e"),
                    });

            ConfPath path =
                new ConfPath("/bintest/bindata{%x %x %x}",
                             key2,key3,
                             new ConfHexList("ff:ff:ff:ff:5e"));

            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));

        }catch(Exception e){
            fail(e);
        }
    }

    @Test
        public void test7(){
        session();
        //***************'Test 6
        try{
            ConfPath ref = new ConfPath("/bintest/bindata{%x %x %x}",
                                        new ConfHexList("ff:ff:ff:6e"),
                                        new ConfHexList("ff:ff:ff:8e"),
                                        new ConfHexList("ff:ff:ff:6e"));


            ConfKey key = new ConfKey(new ConfObject[]{
                    new ConfHexList("ff:ff:ff:8e"),
                    new ConfHexList("ff:ff:ff:6e"),
                });


            ConfPath path = new ConfPath("/bintest/bindata{%s %x}",
                                         "ff:ff:ff:6e",
                                         key);

            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));

        }catch(Exception e){
            fail(e);
        }

    }


    @Test
        public void test8(){
        session();
        //***************'Test 6

        try{
            ConfPath ref = new ConfPath("/bintest/bindata{%x %x %x}",
                                        new ConfHexList("ff:ff:ff:5e"),
                                        new ConfHexList("ff:ff:ff:6e"),
                                        new ConfHexList("ff:ff:ff:6e"));


            ConfKey key = new ConfKey(new ConfObject[]{
                    new ConfHexList("ff:ff:ff:6e"),
                });


            ConfPath path = new ConfPath("/bintest/bindata{%s %x ff:ff:ff:6e}",
                                         "ff:ff:ff:5e",
                                         key);

            maapi.create(th,path);

            assertTrue(maapi.exists(th, ref));

        }catch(Exception e){
            fail(e);
        }
    }





    @Ignore
        public void test9(){

    }

    //ConfIPv4 tests with ConfPath.
    public void test10(){
        session();

        try{
            //Create different keys.
            //****************Test 10

            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));

            String strPath =
                "/iptest/ipdata{192.168.0.1 192.168.0.2 192.168.0.3}";
            ConfPath path =
                new ConfPath( strPath );

            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));

        }catch(Exception e){
            fail(e);
        }
    }

    @Test
        public void test12() {
        session();

        try{
            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));


            ConfObject[] arr = new ConfObject[]{
                new ConfKey(new ConfObject[]{
                        new ConfIPv4(new int[]{192,168,0,1}),
                        new ConfIPv4(new int[]{192,168,0,2}),
                        new ConfIPv4(new int[]{192,168,0,3} )}),
                new ConfTag(new mtest().hash(), mtest._ipdata),
                new ConfTag(new mtest().hash(), mtest._iptest)
            };

            //maapi.create(th,);
            maapi.create(th, new ConfPath(arr));
            assertTrue(maapi.exists(th,ref ));


        }catch( Exception e){
            fail(e);
        }
    }
    @Test
        public void test13() {
        session();
        //****************Test 13
        try{
            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));


            ConfPath path1 =
                new ConfPath("/iptest/ipdata{%x}",
                             new ConfKey(new ConfObject[]{
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3})
                                 }));

            maapi.create(th,path1);
            assertTrue(maapi.exists(th, ref));

        }catch( Exception e){
            fail(e);
        }

    }



    @Test
        public void test14() {
        session();

        try{
            //***************'Test 14
            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));


            ConfKey key =
                new ConfKey(new ConfObject[]{
                        new ConfIPv4(new int[]{192,168,0,1}),
                        new ConfIPv4(new int[]{192,168,0,2}),
                    });

            ConfPath path =
                new ConfPath("/iptest/ipdata{%x %x}",
                             key, new ConfIPv4(new int[]{192,168,0,3}));

            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));

        }catch( Exception e){
            fail(e);
        }
    }
    @Test
        public void test15(){
        session();
        //***************'Test 15

        try{

             ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));

             ConfKey key =
                new ConfKey(new ConfObject[]{
                        new ConfIPv4(new int[]{192,168,0,1}),
                        new ConfIPv4(new int[]{192,168,0,2})
                    });

            ConfPath path =
                new ConfPath("/iptest/ipdata{%x%x}",
                             key,
                             new ConfIPv4(new int[]{192,168,0,3}));

            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));
        }catch( Exception e){
            fail(e);
        }
    }

    @Test
        public void test16(){
        session();
        //***************'Test 15

        try{
            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));
            ConfKey key1 =
                new ConfKey(new ConfObject[]{
                        new ConfIPv4(new int[]{192,168,0,1}),
                    });

            ConfKey key2 =
                new ConfKey(new ConfObject[]{
                        new ConfIPv4(new int[]{192,168,0,2}),
                    });

            ConfPath path =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             key1,key2,
                             new ConfIPv4(new int[]{192,168,0,3}));


            maapi.create(th,path);

            assertTrue(maapi.exists(th,ref));

        }catch(Exception e){
            fail(e);
        }
    }

    @Test
        public void test17(){
        session();
        //***************'Test 6
        try{
            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));


            ConfKey key = new ConfKey(new ConfObject[]{
                      new ConfIPv4(new int[]{192,168,0,2}),
                      new ConfIPv4(new int[]{192,168,0,3})
                });


            ConfPath path = new ConfPath("/iptest/ipdata{%s %x}",
                                         "192.168.0.1",
                                         key);

            maapi.create(th,path);
            assertTrue(maapi.exists(th,ref));

        }catch(Exception e){
            fail(e);
        }

    }


    @Test
        public void test18(){
        session();
        //***************'Test 6

        try{
            ConfPath ref =
                new ConfPath("/iptest/ipdata{%x %x %x}",
                             new ConfIPv4(new int[]{192,168,0,1}),
                             new ConfIPv4(new int[]{192,168,0,2}),
                             new ConfIPv4(new int[]{192,168,0,3}));


            ConfKey key = new ConfKey(new ConfObject[]{
                    new ConfIPv4(new int[]{192,168,0,2}),
                });


            ConfPath path = new ConfPath("/iptest/ipdata{%s %x 192.168.0.3}",
                                         "192.168.0.1",
                                         key);

            maapi.create(th,path);

            assertTrue(maapi.exists(th, ref));

        }catch(Exception e){
            fail(e);
        }
    }
}


