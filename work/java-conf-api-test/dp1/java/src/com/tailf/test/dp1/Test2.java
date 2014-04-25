package com.tailf.test.dp1;

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

import org.apache.log4j.Logger;
import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.test.dp1.namespaces.*;

public class Test2{

    private static Logger LOGGER = Logger.getLogger(Test2.class);
    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        new Test2().test();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception{
        try{
            Socket s =
                new Socket("127.0.0.1", port);
            Maapi maapi = new Maapi(s);
            //maapi.addNamespace(new mtest());
            maapi.addNamespace(new smp());


            maapi.startUserSession("jb",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            int th =   maapi.startTrans(Conf.DB_RUNNING,
                                        Conf.MODE_READ_WRITE);
            // start transaction

            maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");

            //maapi.delete(th, "/servers/server{www}");
            //maapi.setElem(th, new ConfIPv4(192,168,0,7),
            //            "/servers/server{ssh}/ip");


            maapi.setElem(th, new ConfIPv4(192,168,0,2),
                          "/servers/server{ssh}/ip");

            maapi.setElem(th, new ConfUInt16(6666),
                          "/servers/server{ssh}/port");

            ConfHexList macaddr= new ConfHexList("4f:4c:41:00:00:01");
            LOGGER.info("MacAddr= "+macaddr);
            maapi.setElem(th, macaddr, "/servers/server{ssh}/macaddr");
            ConfOID oid = new ConfOID("1.1.6.1.24961.1.0");
            LOGGER.info("Oid= "+oid);
            maapi.setElem(th, oid, "/servers/server{ssh}/snmpref");
            ConfOctetList prefixmask=
                new ConfOctetList("255.255.224.0");
            LOGGER.info("OctetList= "+prefixmask);
            maapi.setElem(th, prefixmask,
                                  "/servers/server{ssh}/prefixmask");
            maapi.validateTrans(th, false, true);
            maapi.prepareTrans(th);
            maapi.commitTrans(th);
            maapi.endUserSession();
            s.close();
        } catch (MaapiException e) {
            e.printStackTrace();
        }

    }

}