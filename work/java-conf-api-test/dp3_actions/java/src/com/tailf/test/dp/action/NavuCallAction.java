package com.tailf.test.dp.action;


import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import java.net.UnknownHostException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;


import com.tailf.conf.Conf;
import com.tailf.conf.*;
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

import com.tailf.navu.*;



import org.w3c.dom.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;

import org.apache.log4j.Logger;

import com.tailf.test.dp.action.namespaces.*;


public class NavuCallAction{
    public Maapi maapi;
    public Socket s;
    public ConfNamespace ns;
    public ConfNamespace ns2;
    public ConfXMLParamToXML transf;
    public int th;
    public NavuContainer root ;


    private static Logger LOGGER = Logger.getLogger(NavuCallAction.class);

    public NavuCallAction(){

        try{
            s = new Socket("localhost", Conf.PORT);
            maapi = new Maapi(s);
            maapi.startUserSession("ola",
                                   InetAddress.getLocalHost(),
                                   "maapi", new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            th = maapi.startTrans(Conf.DB_RUNNING,
                                  Conf.MODE_READ_WRITE);

            maapi.loadSchemas();
            ns = new config();
            ns2 = new testaction();

             root =
                new NavuContainer(new NavuContext(maapi,th));


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
        NavuCallAction sar =
            new NavuCallAction();
        sar.go();
    }


    public void go(){

        try{
            NavuContainer cfg =
                root.container(new config().hash());

            NavuContainer action = cfg.container(config._config)
                .list(config._server).elem("iolo").action(config._reset);

            ArrayList<ConfNamespace> ns_list = maapi.addNamespace( ns );

            ConfXMLParam[] params =
                new ConfXMLParam[] {
                new ConfXMLParamValue(ns.hash(),
                                      config._when,
                                      new ConfInt32(4))
            };

            ConfXMLParam[] res = action.call(params);
            LOGGER.info("res:" + res);

        }catch(ConfException e){
            LOGGER.error(e);
        }catch(Exception e){
            LOGGER.error(e);
        }
    }
}