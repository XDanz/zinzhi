package com.tailf.test.maapi1;

import com.tailf.navu.*;

import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.MaapiDiffIterate;

import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.ConfIterate;

import com.tailf.test.maapi1.namespaces.smp;
import java.util.Arrays;
import org.apache.log4j.Logger;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfInternal;

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

public class ExtraTest1 {

    private Socket socket;
    private Socket socket2;
    private Cdb cdb;

    private Maapi maapi;
    private int th;
    private NavuContainer maapiroot;
    private NavuContainer cdbroot;


    private static Logger log =
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


    /*
     * Test NavuContainer.toString()
     *
     */

    @Test
        public void test0(){

        try{

            maapi.startUserSession("ola",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            th = maapi.startTrans(Conf.DB_CANDIDATE,
                                  Conf.MODE_READ_WRITE);


            maapi.create(th,"/servers/server{www1}");

            maapi.setElem(th,new ConfIPv4("192.168.0.1"),
                          "/servers/server{www1}/ip");
            maapi.setElem(th,new ConfUInt16(1111),
                          "/servers/server{www1}/port");


            //Create a www2 new entry
            maapi.create(th,"/servers/server{www2}");

            maapi.setElem(th,new ConfIPv4("192.168.0.2"),
                          "/servers/server{www1}/ip");

            maapi.setElem(th,new ConfUInt16(2222),
                          "/servers/server{www2}/port");

            Queue<IterateRecord> entries =
                new LinkedList<IterateRecord>();

            maapi.diffIterate(th, new DiffIterate1(entries));


            IterateRecord record = entries.remove();
            //expected

            ConfObject[] www1path = new ConfObject[]{
                new ConfKey(new ConfBuf("www1")),
                new ConfTag(new smp().hash(), smp._server),
                new ConfTag(new smp().hash(), smp._servers)
            };

            assertArrayEquals(www1path,record.keypath);

            assertEquals(DiffIterateOperFlag.MOP_CREATED, record.operflag);


            record = entries.remove();

            ConfObject[] www2path = new ConfObject[]{
                new ConfKey(new ConfBuf("www2")),
                new ConfTag(new smp().hash(), smp._server),
                new ConfTag(new smp().hash(), smp._servers)
            };

            assertArrayEquals(www2path,record.keypath);

            assertEquals(DiffIterateOperFlag.MOP_CREATED, record.operflag);

        }catch(Exception e){
            throw throwException(e,"Test Failure!");
        }
    }

    static class IterateRecord
    {
        ConfObject[] keypath;
        DiffIterateOperFlag operflag;
        ConfValue newval;
        ConfValue oldval;
    }


    static class DiffIterate1 implements MaapiDiffIterate{

        Queue<IterateRecord> records;

        public DiffIterate1(Queue<IterateRecord> records){
            this.records = records;
        }

        public DiffIterateResultFlag iterate(ConfObject[] kp,
                                             DiffIterateOperFlag op,
                                             ConfObject nval,
                                             ConfObject oval,
                                             Object state){
            IterateRecord record = new IterateRecord();
            int nshash = -1;

            ConfTag rootTag = (ConfTag)kp[kp.length-1];
            nshash = rootTag.getNSHash();

            for(int i = 0; i < kp.length; i++){
                //ConfTag rootTag = (ConfTag)kp[kp.length-1];
                if(kp[i] instanceof ConfTag){
                    ConfTag tag = (ConfTag)kp[i];
                    // if(tag.getTagHash() == -1)
                    //     //tag.getTagHash() = nshash;
                }
            }
            record.keypath = kp;
            record.operflag = op;
            record.newval =(ConfValue) nval;
            record.oldval = (ConfValue)oval;
            records.offer(record);

            return DiffIterateResultFlag.ITER_CONTINUE;
        }
    }






    AssertionError throwException(Throwable t,String msg){
        AssertionError ae =
            new AssertionError(msg);
        ae.initCause(t);
        return ae;

    }
    @Ignore
        public void test1() {
    }

    @Ignore
        public void test2() {
    }
    @Ignore
        public void test3() {
    }
    @Ignore
        public void test4(){
    }
    @Ignore
        public void test5(){
    }

}


