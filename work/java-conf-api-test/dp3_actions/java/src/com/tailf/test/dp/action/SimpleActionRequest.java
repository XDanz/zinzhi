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


public class SimpleActionRequest{
    public Maapi maapi;
    public Socket s;
    public ConfNamespace ns;
    public ConfNamespace ns2;
    public ConfXMLParamToXML transf;
    public int th;


    private static Logger LOGGER =
        Logger.getLogger(SimpleActionRequest.class);

    public SimpleActionRequest(){

        try{
            s = new Socket("localhost", Conf.PORT);
            maapi = new Maapi(s);
            maapi.startUserSession("ola",
                                   InetAddress.getLocalHost(),
                                   "maapi", new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);
            maapi.loadSchemas();

            ns = new config();
            ns2 = new testaction();
            maapi.addNamespace( ns );
            maapi.addNamespace(ns2);
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
        SimpleActionRequest sar = new SimpleActionRequest();
        sar.go();
    }


    public void go(){

        try{
            ArrayList<ConfNamespace> ns_list = maapi.addNamespace( ns );

            ConfXMLParam[] params= new ConfXMLParam[] {

                new ConfXMLParamValue(ns2,
                                      testaction._alarm_status_,
                                      ConfEnumeration.getEnumByLabel(
                        "/ta:testalarms/purge-alarms/alarm-status", "any"))

            };
            Document doc = transf.toXML(params);

            transf.serialize(doc,System.out);

            ConfXMLParam[] res =
                maapi.requestAction(params,
                                    ns2.hash(),
                                    "/testalarms/purge-alarms");

        }catch(ConfException e){
            LOGGER.error(e);
        }catch(IOException e){
            LOGGER.error(e);
        }catch(Exception e){
            LOGGER.error(e);
        }


    }



}