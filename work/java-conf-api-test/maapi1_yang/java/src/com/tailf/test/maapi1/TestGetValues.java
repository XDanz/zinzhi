package com.tailf.test.maapi1;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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

import com.tailf.proto.*;



public class TestGetValues {

    private Logger log =
        Logger.getLogger(TestGetValues.class);

    Maapi maapi = null;
    public static final int port = 4565;

    Socket s = null;
    int th = -1;

    public static void
        main(String[] arg)
    {
        //new TestGetValues().test();
    }


    private void init() throws IOException,UnknownHostException,
                               ConfException {
        s = new Socket("127.0.0.1", port);
        maapi = new Maapi(s);
        maapi.startUserSession("admin",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"admin"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int usid = maapi.getMyUserSession();
        th   = maapi.startTrans(Conf.DB_RUNNING,
                                Conf.MODE_READ_WRITE);

        maapi.setNamespace(th,new mtest().hash());


    }


    public boolean test0() {

        try {
            mtest ns = new mtest();
            int h = ns.hash();

            List<ConfXMLParam> p = new ArrayList<ConfXMLParam>();

            p.add(new ConfXMLParamStart(h,
                                        mtest._server));


            p.add(new ConfXMLParamValue(h,
                                        mtest._srv_name,
                                        new ConfBuf("www3")));


            p.add(new ConfXMLParamLeaf(h,mtest._ip));

            p.add(new ConfXMLParamLeaf(h,mtest._port));

            p.add(new ConfXMLParamStart(h,mtest._foo));

            p.add(new ConfXMLParamLeaf(h,mtest._bar));

            p.add(new ConfXMLParamLeaf(h,mtest._baz));


            p.add(new ConfXMLParamStop(h,mtest._foo));

            p.add(new ConfXMLParamStart(h,mtest._interface));
            p.add(new ConfXMLParamValue(h,
                                        mtest._if_name,
                                        new ConfBuf("eth0")));

            p.add(new ConfXMLParamLeaf(h,
                                       mtest._mtu));

            p.add(new ConfXMLParamStop(h,
                                       mtest._interface));

            p.add(new ConfXMLParamStart(h,mtest._interface));

            p.add(new ConfXMLParamValue(h,
                                        mtest._if_name,
                                        new ConfBuf("eth5")));

            p.add(new ConfXMLParamLeaf(h,
                                       mtest._mtu));

            p.add(new ConfXMLParamStop(h,
                                       mtest._interface));

            p.add(new ConfXMLParamStop(h,
                                       mtest._server));

            List<ConfXMLParam> res =
                maapi.<List<ConfXMLParam> >getValues(th,p,
                          "/mtest/servers");
            return true;
        } catch ( Exception e ) {
            log.error("", e);
            return false;
        }

    }


    // public void start1() throws IOException,UnknownHostException,
    //                             ConfException{
    //        mtest ns = new mtest();
    //     int h = ns.hash();
    //     ArrayList<ConfNamespace> ns_list =
    //         maapi.getNsList();


    //     ConfXMLParam[] params = new ConfXMLParam[]{



    //         new ConfXMLParamStart(h,mtest._interface,
    //                               ns_list),

    //         new ConfXMLParamValue(h,
    //                               mtest._if_name,
    //                               new ConfBuf("eth0"),
    //                               ns_list),

    //         new ConfXMLParamLeaf(h,
    //                              mtest._mtu,
    //                              ns_list),

    //         new ConfXMLParamStop(h,
    //                              mtest._interface,
    //                              ns_list),



    //         new ConfXMLParamStart(h,mtest._interface,
    //                               ns_list),

    //         new ConfXMLParamValue(h,
    //                               mtest._if_name,
    //                               new ConfBuf("eth1"),
    //                               ns_list),

    //         new ConfXMLParamLeaf(h,
    //                              mtest._mtu,
    //                              ns_list),

    //         new ConfXMLParamStop(h,
    //                              mtest._interface,
    //                              ns_list),



    //         new ConfXMLParamStart(h,mtest._interface,
    //                               ns_list),

    //         new ConfXMLParamValue(h,
    //                               mtest._if_name,
    //                               new ConfBuf("eth2"),
    //                               ns_list),

    //         new ConfXMLParamLeaf(h,
    //                              mtest._mtu,
    //                              ns_list),

    //         new ConfXMLParamStop(h,
    //                              mtest._interface,
    //                              ns_list),


    //          new ConfXMLParamStart(h,mtest._interface,
    //                               ns_list),

    //         new ConfXMLParamValue(h,
    //                               mtest._if_name,
    //                               new ConfBuf("eth3"),
    //                               ns_list),

    //         new ConfXMLParamLeaf(h,
    //                              mtest._mtu,
    //                              ns_list),

    //         new ConfXMLParamStop(h,
    //                              mtest._interface,
    //                              ns_list),

    //     };


    //     ConfXMLParam[] res =
    //         maapi.getValues(th,params,
    //                         "/mtest/servers/server{www3}");

    // }





}