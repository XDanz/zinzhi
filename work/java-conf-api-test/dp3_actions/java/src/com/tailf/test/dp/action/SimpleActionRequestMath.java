package com.tailf.test.dp.action;


import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


import com.tailf.conf.Conf;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamStart;
import com.tailf.conf.ConfEnumeration;

import com.tailf.conf.ConfList;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfInt32;

import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfXMLParamStop;
import com.tailf.util.*;

import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfNamespace;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.conf.ConfException;


import org.w3c.dom.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;

import org.apache.log4j.Logger;

import com.tailf.test.dp.action.namespaces.*;


public class SimpleActionRequestMath{
    public Maapi maapi;
    public Socket s;
    public ConfNamespace ns;

    public ConfXMLParamToXML transf;
    public int th;


    private static Logger LOGGER =
        Logger.getLogger(SimpleActionRequest.class);

    public SimpleActionRequestMath(){

        try{
            s = new Socket("localhost", Conf.PORT);
            maapi = new Maapi(s);
            maapi.startUserSession("ola",
                                   InetAddress.getLocalHost(),
                                   "maapi", new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            th = maapi.startTrans(Conf.DB_RUNNING,
                                  Conf.MODE_READ_WRITE);
            maapi.setNamespace(th, new mathrpc().uri());
            ns = new mathrpc();

            //maapi.addNamespace( ns );
            transf = new ConfXMLParamToXML();

        }catch(ConfException e){
            LOGGER.error(e);
        }catch(UnknownHostException e){
            LOGGER.error(e);
        }catch(IOException e){
            LOGGER.error(e);
        }
    }

    public static void main(String[] arg){
        SimpleActionRequestMath sar =
            new SimpleActionRequestMath();
        sar.go();
    }


    public void go(){

        try{
            ArrayList<ConfNamespace> ns_list = maapi.addNamespace( ns );

            ConfXMLParam[] params = new ConfXMLParam[] {
                new ConfXMLParamStart(ns.hash(),
                                      mathrpc._add),

                new ConfXMLParamValue(ns.hash(),mathrpc._operand,
                                      new ConfList(new ConfObject[]{
                                              new ConfInt32(1),
                                              new ConfInt32(2),
                                              new ConfInt32(3) })),
                new ConfXMLParamStop(ns.hash(),
                                     mathrpc._add)
            };

            // Document doc = transf.toXML(params);
            // transf.serialize(doc,System.out);

            ConfXMLParam[] res =
                maapi.requestActionTh(th,
                                      params,
                                      "/math");
            //

        }catch(ConfException e){
            LOGGER.error(e);
        }catch(IOException e){
            LOGGER.error(e);
        }catch(Exception e){
            LOGGER.error(e);
        }

    }



}