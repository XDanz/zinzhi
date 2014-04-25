package com.tailf.test.dp2;

/*    -*- Java -*-
 *
 *  Copyright 2007 Tail-F Systems AB. All rights reserved.
 *
 *  This software is the confidential and proprietary
 *  information of Tail-F Systems AB.
 *
 *  $Id$
 *
 */

import java.net.InetAddress;
import java.net.Socket;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfValue;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiCursor;
import com.tailf.maapi.MaapiUserSessionFlag;
import org.apache.log4j.Logger;

import com.tailf.test.dp2.namespaces.smp;

public class SimpleMaapi {

    private static Logger LOGGER =
        Logger.getLogger(SimpleMaapi.class);

    public static void main(String args[]) {

        try {
            LOGGER.info("SimpleMaapi");
            Maapi maapi = new Maapi( new Socket("localhost",Conf.PORT));
            maapi.addNamespace(new smp());
            maapi.startUserSession("ola", InetAddress.getLocalHost(),
                                   "maapi", new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);
            // start transaction
            int th   = maapi.startTrans(Conf.DB_RUNNING,
                                        Conf.MODE_READ_WRITE);
            maapi.setNamespace(th, "http://tail-f.com/test/smp/1.0");
            ConfKey next;
            MaapiCursor c =  maapi.newCursor(th, "/servers/server");
            LOGGER.info("maapi.newCursor --> "+c);
            next = maapi.getNext(c);

            LOGGER.info("maapi.getNext --> "+next);

            while (next != null) {
                ConfValue port=
                    maapi.getElem(th, "/servers/server{%x}/port",
                                  next.elementAt(0));

                LOGGER.info("maapi.getElem --> "+port);
                next = maapi.getNext(c);
            }
        }catch (Exception e) {
            LOGGER.info("got exception: "+e);
        }
    }
}



