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

public class TestGetUserSessions{

    public static final int port = 4565;

    public static void main(String[] arg) throws Exception{
        new TestGetUserSessions().test();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception {
        Socket s =
            new Socket("127.0.0.1", port);

        Maapi maapi = new Maapi(s);
        maapi.startUserSession("jb", InetAddress.getLocalHost(),
                               "maapi", new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int usid = maapi.getMyUserSession();


        int[] who = maapi.getUserSessions();

        int i;
        boolean ok = false;

        for(i=0; i < who.length ; i++) {
            if (who[i] == usid)
                ok = true;
        }

        s.close();
    }

}