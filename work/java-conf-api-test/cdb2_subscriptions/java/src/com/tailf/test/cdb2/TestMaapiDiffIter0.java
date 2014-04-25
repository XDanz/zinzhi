package com.tailf.test.cdb2;

import com.tailf.navu.*;

import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiException;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.MaapiDiffIterate;

import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.ConfIterate;


import java.util.Arrays;
import org.apache.log4j.Logger;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfInternal;

import com.tailf.conf.*;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

import com.tailf.maapi.MaapiSchemas;


import java.io.IOException;

import java.net.*;

public class TestMaapiDiffIter0 {

    //private Maapi maapi;
    //private int th;

    private static Logger log =
        Logger.getLogger(TestMaapiDiffIter0.class);

    public static void
        main ( String arg[] )
    {

        try {

            Maapi maapi = new Maapi( new Socket("127.0.0.1",Conf.PORT));
            maapi.startUserSession("ola",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            int th = maapi.startTrans(Conf.DB_CANDIDATE,
                                      Conf.MODE_READ_WRITE);

            String strPath = "/zb:zconfigzub/buzz-interfaces/" +
                "buzz/servers-3keys/server{www1 127.0.0.1 81}";
            ConfPath path = new ConfPath ( strPath );
            maapi.create(th,path );

            maapi.setElem(th,new ConfIPv4("192.168.0.1"),
                          path.copyAppend("ip"));

            maapi.setElem(th,new ConfUInt16(1111),
                          path.copyAppend("port"));

            strPath = "/zb:zconfigzub/buzz-interfaces/" +
                "buzz/servers-3keys/server{www2 127.0.0.2 82}";

            path = new ConfPath ( strPath ) ;
            //Create a www2 new entry
            maapi.create(th, path );

            maapi.setElem(th,new ConfIPv4("192.168.0.2"),
                          path.copyAppend("ip"));
            //K"/servers/server{www1}/ip");

            maapi.setElem(th,new ConfUInt16(2222),
                          path.copyAppend("port"));

            ConfAttributeValue attr =
                new ConfAttributeValue ( ConfAttributeType.TAGS,
                                         new ConfBuf("hej"));

            // maapi.setAttr(th, attr,  path.toString()
            //               );
///servers-3keys/server{www2 127.0.0.2 82}/foo");
            //"/servers/server{www2}/port");

            maapi.diffIterate(th, new DiffIterate1());

        }catch(Exception e) {
            log.error("", e);
            System.exit (1);
        }
    }

    static class DiffIterate1 implements MaapiDiffIterate {

        public DiffIterateResultFlag iterate(ConfObject[] kp,
                                             DiffIterateOperFlag op,
                                             ConfObject oldValue,
                                             ConfObject newValue,
                                             Object state){
            log.info(" ** iterate -->");
            log.info( new ConfPath ( kp ).toString() + " --> " + op );

            if ( op == DiffIterateOperFlag.MOP_VALUE_SET) {
                log.info( new ConfPath ( kp ).toString() + " --> from " +
                          oldValue + " to " + newValue);
            }
                log.info(" ** iterate --> DONE");
            return DiffIterateResultFlag.ITER_RECURSE;
        }
    }
}


