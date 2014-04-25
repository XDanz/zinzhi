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

import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.test.dp1.namespaces.*;

public class Test1{

    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        new Test1().test();
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
            maapi.setElem(th, new ConfIPv4(192,168,0,7),
                        "/servers/server{ssh}/ip");
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