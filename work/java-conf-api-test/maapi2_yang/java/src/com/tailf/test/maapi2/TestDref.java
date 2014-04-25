/*
 */

package com.tailf.test.maapi2;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfDefault;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfIPv4;
import com.tailf.conf.ConfUInt16;
import com.tailf.conf.ConfKey;
import com.tailf.navu.NavuList;
import com.tailf.conf.ConfNoExists;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfUInt32;
import com.tailf.conf.ConfValue;
import com.tailf.conf.ErrorCode;
import com.tailf.maapi.Maapi;


import java.util.Collection;
import com.tailf.maapi.MaapiFlag;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuChange;

import com.tailf.navu.NavuLeaf;
import com.tailf.navu.NavuNode;
import com.tailf.navu.NavuContext;
import com.tailf.navu.PreparedXMLStatement;

import com.tailf.proto.ConfEObject;

import com.tailf.test.maapi2.namespaces.*;

import org.apache.log4j.Logger;


class TestDref{

    private static Logger LOGGER =
        Logger.getLogger(TestDref.class);

    private final String XML =
        "<servers>" +
        "<server>" +
        "<name>?</name>" +
        "<ip>?</ip>" +
        "<port>?</port>" +
        "</server>" +
        "</servers>";

    public static void main(String[] arg) throws Exception{
        new TestDref().test();
    }

    public void test() throws Exception {
        Socket s = new Socket("localhost", Conf.PORT);

        Maapi maapi = new Maapi(s);
        maapi.startUserSession("ola",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);
        // start transaction
        int th  = maapi.startTrans(Conf.DB_RUNNING,
                                   Conf.MODE_READ_WRITE);

        NavuList hosts =
            new NavuContainer(new NavuContext(maapi,th))
            .container(new smp().hash()).list(smp._host);

        StringBuffer buff = new StringBuffer();
        buff.append("<hosts>");
        buff.append("<name>host11</name>");
        buff.append("<server>");
        buff.append("<ip>192.168.0.1</ip>");
        buff.append("<port>80</port>");
        buff.append("<l1>10</l1>");
        buff.append("</server>");

        buff.append("<server>");
        buff.append("<ip>192.168.0.2</ip>");
        buff.append("<port>81</port>");
        buff.append("<l1>20</l1>");
        buff.append("</server>");

        buff.append("<server>");
        buff.append("<ip>192.168.0.3</ip>");
        buff.append("<port>81</port>");
        buff.append("<l1>20</l1>");
        buff.append("</server>");

        buff.append("</hosts>");

        /** "/smp:host **/
        hosts.set(buff.toString());

        NavuContainer entry =
            hosts.elem(new ConfKey(new ConfBuf("host11")));

        buff.delete(0,buff.capacity());
        buff.append("<server>");
        buff.append("<ip>192.168.0.1</ip>");
        buff.append("<port>81</port>");
        buff.append("<l1>10</l1>");
        buff.append("</server>");
        entry.set(buff.toString());


        NavuLeaf serverIP =
            ((NavuContainer)entry.getParent().getParent())
            .leaf(smp._serverip);
        LOGGER.info("serverIP:" + serverIP);
        serverIP.set("192.168.0.1");


        Collection<NavuNode> refs = serverIP.deref();

        for(NavuNode node: refs){
            LOGGER.info("path:" + node);
        }




        //maapi.setNamespace(th, "http://tail-f.com/test/smp");


        // maapi.setObject(th, new ConfObject[] {new ConfBuf("s44"),
        //                                       new ConfDefault(),
        //                                       new ConfDefault()},
        //            "/servers/server");


        NavuContainer smpModule = (NavuContainer)serverIP.getParent();
        NavuContainer servers = smpModule.container(smp._servers);
        LOGGER.info("servers:" + servers);
        PreparedXMLStatement stmt = servers.prepareXMLCall(XML);
        stmt.setBuf(0,"www1");
        stmt.setIPv4(1,new ConfIPv4("192.168.0.1"));
        stmt.setUInt16(2,new ConfUInt16(80));
        stmt.set();
        stmt.reset();

        stmt.setBuf(0,"www2");
        stmt.setIPv4(1,new ConfIPv4("192.168.0.7"));
        stmt.setUInt16(2,new ConfUInt16(8080));
        stmt.set();


        // // maapi.setObject(th, new ConfObject[] {new ConfBuf("s45"),
        // //                                       new ConfDefault(),
        // //                                       new ConfDefault()},
        // //     "/servers/server");


        // NavuContainer module = (NavuContainer)servers.getParent();
        // NavuLeaf leaf = module.container(smp._test).leaf(smp._ref);
        // //maapi.setElem(th, "www1", "/test/ref");
        // leaf.set("www1");

        // // maapi.setObject(th,
        // //                 new ConfObject[] {
        // //                     new ConfBuf("s45")},

        // //module.container(sm._test2).leaf(sm.
        // //
        // //              "/test2/server2");

        // // ConfObject[][] refs =
        // //     maapi.deref(th,  "/test/ref");

        // // LOGGER.info("refs = " +
        // //             Arrays.toString(refs[0]));

        // // ConfObject[] keypath = refs[0];
        // // String ss = Conf.kpToString(keypath);
        // // LOGGER.info("ss:" + ss);

        // // if (ss.compareTo("/smp:servers/server/{s44}/name") != 0) {
        // //     LOGGER.error("need path " + "smp:servers/server/{s44}/name" +
        // //                  " but got " + ss);
        // // }

        // // smp smp_cs = new smp();


        // // maapi.loadSchemas();

        // // NavuContainer tc  =
        // //     new NavuContainer(maapi, th, smp_cs.hash());

        // // NavuLeaf tl = tc.container("test2")
        // //     .list("server2").elem("s45").leaf("ref2");
        // Map<String,NavuChange> changes =
        //     servers.findChanges(new Integer[]{
        //             smp._servers,
        //             smp._server
        //         });

        // for(String key: changes.keySet()){
        //     NavuChange change = changes.get(key);
        //     LOGGER.info("SSSS****************");
        //     for(Iterator<NavuNode> it= change.iterator(); it.hasNext(); ){
        //         //LOGGER.info(":" + key);
        //         LOGGER.info(it.next());
        //     }
        //     LOGGER.info("EEEE****************");
        // }



        // ConfObject[] npath = new ConfObject[path.length-2];

        // System.arraycopy(path,0,npath,0,path.length-2);
        // LOGGER.info("mypath:" + Arrays.toString(npath));

        // Collection<NavuNode> tt =
        //     module.container(smp._servers).select(npath);

        // LOGGER.info("refs = " + Arrays.toString(refs[0]));
        // LOGGER.info("tt = " + tt);
        // keypath = refs[0];
        // ss = Conf.kpToString(keypath);
        // if (ss.compareTo("/smp:servers/server/{s45}/name") != 0) {
        //     LOGGER.error("need path " + "smp:servers/server/{s45}/name" +
        //            "but got " + ss);
        // }

        //maapi.applyTrans(th, true);

        maapi.validateTrans(th,false,true);
        maapi.prepareTrans(th);
        maapi.commitTrans(th);
        maapi.endUserSession();
        s.close();
    }


}