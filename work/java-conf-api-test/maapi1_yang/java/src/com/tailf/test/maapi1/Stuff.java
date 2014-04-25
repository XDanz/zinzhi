package com.tailf.test.maapi1;

import java.net.Socket;
import java.net.UnknownHostException;
import java.net.*;
import java.util.ArrayList;
import java.util.EnumSet;

import java.io.IOException;

import com.tailf.conf.ConfException;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbSession;
import com.tailf.cdb.CdbLockType;
import com.tailf.cdb.CdbDBType;

import com.tailf.maapi.*;
import com.tailf.conf.Conf;

import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfInt64;

import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfXMLParamStart;
import com.tailf.conf.ConfXMLParamStop;
import com.tailf.conf.ConfXMLParam;


import org.apache.log4j.Logger;

import com.tailf.conf.ConfNamespace;

import com.tailf.test.maapi1.namespaces.*;

public class Stuff {

    private static Logger log =
        Logger.getLogger(Stuff.class);

    private Socket socket;
    private Socket socket2;
    private Socket socket3;

    private Cdb cdb;

    private Maapi maapi;

    public static void main(String[] arg) throws Exception{
        //try{
            new Stuff().test();
            //        }catch(Exception e){}
    }

    public void test() throws Exception {
        String path1 =
            "/mtest:mtest/aggetest/serviceagge{%x}";

        String path2 =
            path1 + "/agge/bzz:buzz/servers/server{%x}";

        ConfKey key1 =
            new ConfKey(new ConfBuf("apa"));

        ConfKey key2 =
            new ConfKey(new ConfBuf("www1"));

        log.info("START..");
        socket2 = new Socket("127.0.0.1",Conf.PORT);
        maapi = new Maapi(socket2);

        maapi.startUserSession("admin",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] { "admin" },
                               MaapiUserSessionFlag.PROTO_TCP);

        // Start a read transaction towards the candidate configuration.

        int th =
            maapi.startTrans(Conf.DB_CANDIDATE,
                             Conf.MODE_READ_WRITE);

        if(!maapi.exists(th,path1,key1)){
            maapi.create(th,path1,key1);
        }


        if(!maapi.exists(th,path2,key1,key2)){
            maapi.create(th,path2,key1,key2);
        }

        maapi.setElem(th,new ConfIPv4("192.168.0.1"),
                      path2  + "/ip",
                      key1,key2);

        maapi.applyTrans(th,false);
        maapi.finishTrans(th);
        maapi.candidateCommit();
        maapi.endUserSession();
        socket2.close();
        //DONE ----------------

        log.info("NOW START NEW MAAPI TRANS -->");

        socket3 =  new Socket("127.0.0.1",Conf.PORT);
        maapi = new Maapi(socket3);

        maapi.startUserSession("admin",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] { "admin" },
                               MaapiUserSessionFlag.PROTO_TCP);

        // Start a read transaction towards the running configuration.
        th =  maapi.startTrans(Conf.DB_RUNNING,
                             Conf.MODE_READ);

        log.info("extist now?:" +  maapi.exists(th,path2,key1,key2));

        maapi.endUserSession();
        socket3.close();
        log.info("NOW START NEW MAAPI TRANS --> OK");
        //Done 2 ----------


        log.info("NOW START NEW CDB SESSION  --> ");
        socket = new Socket("127.0.0.1",Conf.PORT);
        cdb = new Cdb("writer",socket);

        CdbSession sess =
            cdb.startSession(CdbDBType.CDB_OPERATIONAL,
                             EnumSet.of(CdbLockType.LOCK_WAIT,
                                        CdbLockType.LOCK_REQUEST));

        log.info("DO SET VALUES.");
        buzz bzz = new buzz();
        int h = bzz.hash();


        ConfXMLParam[] param = new ConfXMLParam[]{

            new ConfXMLParamValue(h,buzz._srv_name,
                                  new ConfBuf("www6")),

            new ConfXMLParamValue(h,buzz._ip,
                                  new ConfIPv4("192.168.0.1")),

            new ConfXMLParamValue(h,buzz._port,
                                  new ConfUInt16(8080)),

            new ConfXMLParamStart(h,buzz._spink),

            new ConfXMLParamValue(h,buzz._bar,
                                  new ConfInt64(66)),

            new ConfXMLParamValue(h,buzz._baz,
                                  new ConfInt64(77)),

            new ConfXMLParamStop(h,buzz._spink),

            new ConfXMLParamStart(h,buzz._interface),
            new ConfXMLParamValue(h,buzz._if_name,
                                  new ConfBuf("eth0")),

            new ConfXMLParamValue(h,buzz._mtu,
                                  new ConfInt64(88)),

            new ConfXMLParamStop(h,buzz._interface),

        };


        ConfPath path =
            new ConfPath("/mtest:mtest/aggetest/serviceagge{%x}/" +
                         "agge/bzz:buzz/stats/sutate ",
                         new ConfKey(new ConfBuf("apa")));

        /*
        sess.setCase(mtest._service_type_choice_,
                     buzz._buzz_case_,
                     new ConfPath("/mtest:mtest/aggetest/serviceagge{%x}/agge",
                                  new ConfKey(new ConfBuf("apa"))));
        */
        log.info("path:" + path);
        sess.setValues(param,path);


    }

}