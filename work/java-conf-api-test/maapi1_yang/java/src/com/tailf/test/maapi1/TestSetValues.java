package com.tailf.test.maapi1;


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

import com.tailf.dp.Dp;

import com.tailf.conf.*;

import com.tailf.maapi.*;
import com.tailf.test.maapi1.namespaces.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.w3c.dom.Document;
import com.tailf.util.*;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestSetValues {

    private static  Logger log =
        Logger.getLogger(Test51.class);

    Maapi maapi = null;
    public static final int port = 4565;
    Socket s = null;
    int th = -1;

    public static void
        main(String[] arg)
    {

        try {
            Socket s = new Socket("127.0.0.1", port);
            Maapi maapi = new Maapi(s);
            maapi.startUserSession("admin",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"admin"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            int usid = maapi.getMyUserSession();
            int th   = maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);

        } catch ( Exception e ) {
            log.error("", e );
            System.exit(1);
        }
    }


    // private void finish() throws ConfException,IOException{
    //     maapi.validateTrans(th,false,false);
    //     maapi.prepareTrans(th);
    //     maapi.commitTrans(th);
    //     s.close();
    // }

    public boolean test0() {

        try {
            mtest ns = new mtest();
            int h = ns.hash();

        List<ConfXMLParam> p =
            new ArrayList<ConfXMLParam>();

        p.add(new ConfXMLParamValue(h,mtest._ip,
                                    new ConfIPv4("127.0.0.1")));


        p.add(new ConfXMLParamValue(h,mtest._port,
                                    new ConfUInt16((long)8080)));


        p.add(new ConfXMLParamStart(h,mtest._foo));

        p.add(new ConfXMLParamValue(h,mtest._bar,
                                    new ConfInt64((long)666)));

        p.add(new ConfXMLParamValue(h,mtest._baz,
                                    new ConfInt64((long)777)));

        p.add(new ConfXMLParamStop(h,mtest._foo));

        p.add(new ConfXMLParamStart(h,mtest._interface));


        p.add(new ConfXMLParamValue(h,
                                    mtest._if_name,
                                    new ConfBuf("eth0")));

        p.add(new ConfXMLParamValue(h,
                                    mtest._mtu,
                                    new ConfInt64((long)1600)));

        p.add(new ConfXMLParamStop(h,mtest._interface));


        maapi.setValues(th,p,new ConfPath("/mtest/servers/server{kalle}"));
        return true;
        } catch ( Exception e ) {
            log.error("",e);
            return false;
        }
    }

    public boolean test1() {
        try {
            mtest ns = new mtest();
            int h = ns.hash();

            List<ConfXMLParam> params =
                new ArrayList<ConfXMLParam>();

            params.add(new ConfXMLParamValue(h,
                                             mtest._srv_name,
                                             new ConfBuf("www3")));

            params.add(new ConfXMLParamValue(h,mtest._ip,
                                             new ConfIPv4("127.0.0.1")));


            params.add(new ConfXMLParamValue(h,mtest._port,
                                             new ConfUInt16((long)8080)));


            params.add(new ConfXMLParamStart(h,mtest._foo));

            params.add(new ConfXMLParamValue(h,mtest._bar,
                                             new ConfInt64((long)666)));

            params.add(new ConfXMLParamValue(h,mtest._baz,
                                             new ConfInt64((long)777)));

            params.add(new ConfXMLParamStop(h,mtest._foo));

            params.add(new ConfXMLParamStart(h,mtest._interface));

            params.add(new ConfXMLParamValue(h,
                                             mtest._if_name,
                                             new ConfBuf("eth0")));

            params.add(new ConfXMLParamValue(h,
                                             mtest._mtu,
                                             new ConfInt64((long)1600)));

            params.add(new ConfXMLParamStop(h,mtest._interface));

            maapi.setValues(th,params,
                            new ConfPath("/mtest/servers/server"));
            return true;
        } catch ( Exception e ) {
            log.error("",e );
            return false;
        }
    }

    private boolean test2() {

        try {
            mtest ns = new mtest();
            int h = ns.hash();

            List<ConfXMLParam> p = new ArrayList<ConfXMLParam>();

            p.add(
                new ConfXMLParamValue(h, mtest._srv_name,
                                      new ConfBuf("www3")));

            p.add(new ConfXMLParamValue(h,mtest._ip,
                                        new ConfIPv4("127.0.0.1")));

            p.add(new ConfXMLParamValue(h,mtest._port,
                                        new ConfUInt16((long)8080)));

            p.add(new ConfXMLParamStart(h,mtest._foo));

            p.add(new ConfXMLParamValue(h,mtest._bar,
                                        new ConfInt64((long)666)));

            p.add(new ConfXMLParamValue(h,mtest._baz,
                                        new ConfInt64((long)777)));

            p.add(new ConfXMLParamStop(h,mtest._foo));

            p.add(new ConfXMLParamStart(h,mtest._interface));

            p.add(new ConfXMLParamValue(h, mtest._if_name,
                                        new ConfBuf("eth0")));

            p.add(new ConfXMLParamValue(h,mtest._mtu,
                                        new ConfInt64((long)1600)));


            p.add(new ConfXMLParamStop(h,mtest._interface));


            p.add(new ConfXMLParamStart(h,mtest._interface));


            p.add(new ConfXMLParamValue(h,mtest._if_name,
                                        new ConfBuf("eth1")));

            p.add(new ConfXMLParamValue(h,mtest._mtu,
                                        new ConfInt64((long)1600)));

            p.add(new ConfXMLParamStop(h,mtest._interface));


            p.add(new ConfXMLParamStart(h,mtest._interface));

            p.add(new ConfXMLParamValue(h,
                                  mtest._if_name,
                                        new ConfBuf("eth2")));

            p.add(new ConfXMLParamValue(h,
                                        mtest._mtu,
                                        new ConfInt64((long)1600)));

            p.add(new ConfXMLParamStop(h,mtest._interface));


            maapi.setValues(th,p,new ConfPath("/mtest/servers/server"));
            return true;
        } catch ( Exception e ) {
            log.error("",e );
            return false;
        }
    }

    private boolean test3() {
        try {
            mtest ns = new mtest();
            int h = ns.hash();


            List<ConfXMLParam> p =
                new ArrayList<ConfXMLParam>();


            p.add(new ConfXMLParamStart(h,mtest._interface));

            p.add(new ConfXMLParamValue(h,
                                  mtest._if_name,
                                        new ConfBuf("eth3")));

            p.add(new ConfXMLParamValue(h,
                                        mtest._mtu,
                                        new ConfInt64((long)1700)));


            p.add(new ConfXMLParamStop(h,mtest._interface));


            p.add(new ConfXMLParamStart(h,mtest._interface));

            p.add(new ConfXMLParamValue(h,
                                        mtest._if_name,
                                        new ConfBuf("eth4")));

            p.add(new ConfXMLParamValue(h,
                                        mtest._mtu,
                                        new ConfInt64((long)1700)));


            p.add(new ConfXMLParamStop(h,mtest._interface));

            p.add(new ConfXMLParamStart(h,mtest._interface));


            p.add(new ConfXMLParamValue(h,
                                        mtest._if_name,
                                        new ConfBuf("eth5")));

            p.add(new ConfXMLParamValue(h,mtest._mtu,
                                        new ConfInt64((long)1700)));

            p.add(new ConfXMLParamStop(h,mtest._interface));


            maapi.setValues(th,p,
                            new ConfPath("/mtest/servers/server{www3}"));
            return true;
        } catch ( Exception e ) {
            log.error("",e );
            return false;
        }
    }
}