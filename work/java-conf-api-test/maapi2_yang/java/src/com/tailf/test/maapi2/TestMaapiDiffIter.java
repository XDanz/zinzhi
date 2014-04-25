
package com.tailf.test.maapi2;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;


import com.tailf.conf.Conf;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfDefault;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfKey;
import com.tailf.conf.ConfNoExists;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfUInt32;
import com.tailf.conf.ConfValue;
import com.tailf.conf.ErrorCode;
import com.tailf.maapi.Maapi;


import java.util.Collection;
import com.tailf.maapi.MaapiFlag;
import com.tailf.maapi.MoveWhereFlag;

import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.maapi.MaapiDiffIterate;
import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuChange;

import com.tailf.navu.NavuLeaf;
import com.tailf.navu.NavuNode;
import com.tailf.navu.NavuContext;
import com.tailf.navu.PreparedXMLStatement;

import com.tailf.proto.ConfEObject;

import com.tailf.test.maapi2.namespaces.*;

import org.apache.log4j.Logger;


class TestMaapiDiffIter implements MaapiDiffIterate {

    private static Logger log =
        Logger.getLogger(TestMaapiDiffIter.class);

    private final String XML =
        "<servers>" +
        "<server>" +
        "<name>?</name>" +
        "<ip>?</ip>" +
        "<port>?</port>" +
        "</server>" +
        "</servers>";

    public static void main(String[] arg) throws Exception{
        new TestMaapiDiffIter().test();
    }

    public void test() throws Exception {
        Socket s = new Socket("localhost", Conf.PORT);
        //((ocket s2 = new Socket("localhost", Conf.PORT);

        Maapi maapi = new Maapi(s);
        //Maapi maapi2 = new Maapi(s2);

        //maapi.loadSchemas();


        maapi.startUserSession("ola",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);
        // start transaction
        int th  = maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);

        // maapi2.startUserSession("ola",
        //                        InetAddress.getLocalHost(),
        //                        "maapi",
        //                        new String[] {"oper"},
        //                        MaapiUserSessionFlag.PROTO_TCP);

        // int th2  = maapi2.startTrans(Conf.DB_RUNNING,
        //                              Conf.MODE_READ_WRITE);


        // NavuContainer root =
        //     new NavuContainer(new NavuContext(maapi,th));

        // NavuContainer rwcont =
        //     new NavuContainer(maapi,th,maapi2,th2,new smp().hash());

        // NavuContainer servers =
        //     rwcont.container(smp._servers);


        //maapi.setNamespace(th, "http://tail-f.com/test/smp");


        // maapi.setObject(th, new ConfObject[] {new ConfBuf("s44"),
        //                                       new ConfDefault(),
        //                                       new ConfDefault()},
        //            "/servers/server");

        //PreparedXMLStatement stmt = servers.prepareXMLCall(XML);
        //maapi.create(th,"/servers/server{www1}");
        //stmt.setBuf(0,"www1");
        maapi.setElem(th,new ConfIPv4("192.168.0.3"),
                      "/servers/server{www1}/ip");
        maapi.setElem(th,new ConfUInt16(9992),
                      "/servers/server{www1}/port");

        //maapi.create(th,"/servers/server{www2}");

        // maapi.setElem(th,new ConfUInt16(88),
        //               "/servers/server{www2}/port");

        maapi.move(th,
                   new ConfKey(new ConfBuf("www3")),
                   "/servers/server{www1}");



        // //stmt.setIPv4(1,new ConfIPv4("192.168.0.1"));
        // stmt.setUInt16(2,new ConfUInt16(80));
        // stmt.set();

        // stmt.reset();

        // stmt.setBuf(0,"www2");
        // stmt.setIPv4(1,new ConfIPv4("192.168.0.7"));
        // stmt.setUInt16(2,new ConfUInt16(8080));
        // stmt.set();


        // maapi.setObject(th, new ConfObject[] {new ConfBuf("s45"),
        //                                       new ConfDefault(),
        //                                       new ConfDefault()},
        //     "/servers/server");


        // NavuContainer module = (NavuContainer)servers.getParent();
        // NavuLeaf leaf = module.container(smp._test).leaf(smp._ref);
        // //maapi.setElem(th, "www1", "/test/ref");
        // leaf.set("www1");

        // maapi.setObject(th,
        //                 new ConfObject[] {
        //                     new ConfBuf("s45")},

        //module.container(sm._test2).leaf(sm.
        //
        //              "/test2/server2");

        // ConfObject[][] refs =
        //     maapi.deref(th,  "/test/ref");

        // log.info("refs = " +
        //             Arrays.toString(refs[0]));

        // ConfObject[] keypath = refs[0];
        // String ss = Conf.kpToString(keypath);
        // log.info("ss:" + ss);

        // if (ss.compareTo("/smp:servers/server/{s44}/name") != 0) {
        //     log.error("need path " + "smp:servers/server/{s44}/name" +
        //                  " but got " + ss);
        // }

        // smp smp_cs = new smp();


        // maapi.loadSchemas();

        // NavuContainer tc  =
        //     new NavuContainer(maapi, th, smp_cs.hash());

        // NavuLeaf tl = tc.container("test2")
        //     .list("server2").elem("s45").leaf("ref2");

        // ConfObject[][] refs = leaf.deref();

        // ConfObject[] path = refs[0];
        // log.info("path:" + Arrays.toString(refs[0]));

        // HashMap<String,NavuChange> changes =
        //     servers.findChanges(new Integer[]{
        //             smp._servers,
        //             smp._server
        //         });

        // for(String key: changes.keySet()){
        //     NavuChange change = changes.get(key);
        //     log.info("SSSS****************");
        //     for(Iterator<NavuNode> it= change.iterator(); it.hasNext(); ){
        //         //log.info(":" + key);
        //         log.info(it.next());
        //     }
        //     log.info("EEEE****************");
        // }



        // ConfObject[] npath = new ConfObject[path.length-2];

        // System.arraycopy(path,0,npath,0,path.length-2);
        // log.info("mypath:" + Arrays.toString(npath));

        // Collection<NavuNode> tt =
        //     module.container(smp._servers).select(npath);

        // log.info("refs = " + Arrays.toString(refs[0]));
        // log.info("tt = " + tt);
        // keypath = refs[0];
        // ss = Conf.kpToString(keypath);
        // if (ss.compareTo("/smp:servers/server/{s45}/name") != 0) {
        //     log.error("need path " + "smp:servers/server/{s45}/name" +
        //            "but got " + ss);
        // }

        //maapi.applyTrans(th, true);
        maapi.diffIterate(th,this);

        maapi.validateTrans(th,false,true);
        maapi.prepareTrans(th);
        maapi.commitTrans(th);
        maapi.endUserSession();
        s.close();
    }

    public DiffIterateResultFlag iterate( ConfObject[] kp,
                                          DiffIterateOperFlag op,
                                          ConfObject old_value,
                                          ConfObject new_value,
                                          Object state ) {

        log.info("***********START ITER*****************");
        log.info("kp:" + Arrays.toString(kp));
        log.info("op:" + op);
        log.info("old:" + old_value);
        log.info("new:" + new_value);
        log.info("***********END ITER*****************");

        // if(op == MaapiDiffIterateOperFlag.MOP_VALUE_SET)
        //     return MaapiDiffIterateResultFlag.ITER_CONTINUE;

        return DiffIterateResultFlag.ITER_CONTINUE;
    }


}