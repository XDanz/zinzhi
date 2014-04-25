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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;


import java.net.UnknownHostException;
import java.io.*;

import java.util.List;

import com.tailf.dp.Dp;

import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.maapi.MaapiSchemas.CSNode;
import com.tailf.test.maapi1.namespaces.*;


import org.junit.runner.*;

import org.apache.log4j.Logger;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;

import org.apache.log4j.Logger;


public class Testsch{
    private Maapi maapi;
    private int th;
    private static Logger LOGGER =
        Logger.getLogger(Testsch.class);

    public static final int port = 4565;

    private Queue<CSNode> queue =
        new LinkedList<CSNode>();



    AssertionError throwException(Throwable t,String msg){
        AssertionError ae =
            new AssertionError(msg);
        ae.initCause(t);
        return ae;

    }


    @Before
    public void init(){
        try{
            Socket s =
                new Socket("127.0.0.1", port);
             maapi =
            new Maapi(s);
            //connect();
            maapi.loadSchemas();
            maapi.startUserSession("admin",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            th = maapi.startTrans(Conf.DB_CANDIDATE,
                                  Conf.MODE_READ_WRITE);


        }catch(ConfException e){
            throw throwException(e,"Test Failure!");
        }catch(UnknownHostException e){
            throw throwException(e,"Test Failure!");

        }catch(IOException e){
            throw throwException(e,"Test Failure!");
        }
    }


    public static void main(String[] arg) throws Exception{
        //new Testsch().test0();
        org.junit.runner.JUnitCore.main(Testsch.class.getName());

    }
    @Test
    public void test0() throws Exception{


        Set<Long> level1 = new HashSet<Long>();

        maapi.loadSchemas();
        MaapiSchemas sch = Maapi.getSchemas();

        // MaapiSchemas.CSSchema mtestSchema =
        //     sch.findCSSchema((long)new mtest().hash());

        Iterator<MaapiSchemas.CSSchema> iter0 =
            sch.getLoadedSchemas().iterator();

        int count= 0;

        while (iter0.hasNext()) {
            MaapiSchemas.CSSchema module =
                iter0.next();
            //LOGGER.debug("sch(" + (count++) +  "):" + sch);
            LOGGER.debug("URI:" + module.getURI());

        }

        mtest ns = new mtest();
        int hmtest = ns.hash();


        MaapiSchemas.CSSchema module1 = sch.findCSSchema(hmtest);
        LOGGER.info("module1:" + module1);
        MaapiSchemas.CSSchema module2 = sch.findCSSchema(new mtest().uri());
        LOGGER.info("module2:" + module2);
        MaapiSchemas.CSSchema module3 =
            sch.findCSSchemaByPrefix( new mtest().prefix());

        assertEquals(module1.getNSHash(),ns.hash());
        assertEquals(module1.getNSHash(),module2.getNSHash());

        assertEquals(module2.getNSHash(),module3.getNSHash());

        LOGGER.info("module3:" + module3);

        MaapiSchemas.CSNode rootNode = module1.getRootNode();
        LOGGER.info("rootNode:" + rootNode + ",hash:" + rootNode.getNSHash());

        assertEquals(rootNode.getTagHash(),
                     (long)ns.toHash(rootNode.getTag()));


        List<MaapiSchemas.CSNode> siblings = rootNode.getSiblings();

        LOGGER.info("-- siblings --");
        for(MaapiSchemas.CSNode sibl: siblings){
                        LOGGER.info("sibl:" + sibl.getTag()+ ",hash:" +
                                    sibl.getNSHash());
        }
        List<MaapiSchemas.CSNode> children = rootNode.getChildren();


        Iterator<MaapiSchemas.CSNode> iter2 =
            rootNode.getSiblings().iterator();
         while (iter2.hasNext()) {
             MaapiSchemas.CSNode n = iter2.next();
             LOGGER.info(n.getTag());
         }

         level1.add(new Long(ns.toHash("ilist")));
         level1.add(new Long(ns.toHash("slist")));

         level1.add(new Long(ns.toHash("firstname")));
         level1.add(new Long(ns.toHash("a_number")));
         level1.add(new Long(ns.toHash("b_number")));
         level1.add(new Long(ns.toHash("indexes")));
         level1.add(new Long(ns.toHash("aggetest")));
         level1.add(new Long(ns.toHash("movables")));
         level1.add(new Long(ns.toHash("servers")));
         level1.add(new Long(ns.toHash("food")));
         level1.add(new Long(ns.toHash("dks")));
         level1.add(new Long(ns.toHash("forest")));
         level1.add(new Long(ns.toHash("types")));
         level1.add(new Long(ns.toHash("ints")));

         LOGGER.info("-- children --");
        for(MaapiSchemas.CSNode child: children){
            assertTrue(level1.remove(child.getTagHash()));
            LOGGER.info("child:" + child.getTag());
        }
        assertTrue(level1.size() == 0);


        MaapiSchemas.CSNode ilistNode =
            sch.findCSNode(rootNode,ns.uri(),"ilist");
        assertNotNull(ilistNode);

        MaapiSchemas.CSNode ilistNode2 =
            sch.findCSNode(rootNode,ns.hash(),
                           ns.toHash("ilist"));

        assertNotNull(ilistNode2);

        MaapiSchemas.CSNode ilistNode3 =
            sch.findCSNode(ns.uri(),
                           "/mtest/ilist");

        assertNotNull(ilistNode3);



        //Logger.error("test failed..");


        MaapiSchemas.CSNode slistNode =
            sch.findCSNode(rootNode,ns.uri(),"slist");

        assertNotNull(slistNode);

        MaapiSchemas.CSNode slistNode2 =
            sch.findCSNode(rootNode,ns.hash(),
                           ns.toHash("slist"));
        assertNotNull(slistNode2);

        MaapiSchemas.CSNode slistNode3 =
            sch.findCSNode(ns.uri(),
                           "/mtest/slist");

        assertNotNull(slistNode3);


        //firstname

        MaapiSchemas.CSNode firstnameNode =
            sch.findCSNode(rootNode,ns.uri(),"firstname");

        assertNotNull(firstnameNode);

        MaapiSchemas.CSNode firstnameNode2 =
            sch.findCSNode(rootNode,ns.hash(),
                           ns.toHash("firstname"));

        assertNotNull(firstnameNode2);

        //etc..


    }

    // public boolean skip() { return true; }
    public void test() throws Exception {
        Socket s =
            new Socket("127.0.0.1", port);
        Maapi maapi =
            new Maapi(s);

        MaapiSchemas sch = Maapi.getSchemas();


        MaapiSchemas.CSSchema mtestSchema =
            sch.findCSSchema((long)new mtest().hash());

        Iterator<MaapiSchemas.CSSchema> iter0 =
            sch.getLoadedSchemas().iterator();

        int count= 0;
        while (iter0.hasNext()) {
            MaapiSchemas.CSSchema module =
                iter0.next();
            //LOGGER.debug("sch(" + (count++) +  "):" + sch);
            LOGGER.debug("URI:" + module.getURI());

        }

        //MaapiSchemas.CSNode root = sch.getRootNode();


        //        LOGGER.info("root:" +
        //                 mtestSchema);

        //MaapiSchemas.CSNode root =
        //  mtestSchema.getRootNode();

        // LOGGER.info("--- Root Node ---");
        // LOGGER.info("root:" + root.getTag());
        // queue.offer(root);

        // LOGGER.info(root);

        // LOGGER.info("--- Node Tree ---");
        // bfs();
    }


    public  void bfs(){

        Set<CSNode> visited =
            new HashSet<CSNode>();

        while(!queue.isEmpty()){

            CSNode next = queue.poll();

            if(next == null)
                LOGGER.info("child is null..");

            LOGGER.info("tag:" + next.getTag());

            //        if(hash == next.getTagHash())
            //  return next;


            Collection<CSNode> children =
                next.getChildren();


            if(children != null){
                LOGGER.info(next.getTag() + " have " + children.size() +
                            " children");

                for(CSNode node: children){
                    if(visited.contains(node)){
                        continue;
                    }
                    visited.add(node);
                    queue.offer(node);
                }
            }
        }
        //return null;
    }

    public static boolean isLeaf(CSNode node){

        boolean stat = false;

        int type = node.getNodeInfo().getShallowType();

        stat = (node.getChildren() != null);

        stat =  type > ShallowType.C_XMLTAG.getValue();

        stat = type != ShallowType.C_LIST.getValue();

        stat = type != ShallowType.C_XMLBEGIN.getValue();

        stat = type != ShallowType.C_XMLEND.getValue();

        stat = type != ShallowType.C_DEFAULT.getValue();

        return stat;
    }


    /**
     * Checks if the node is a leaf-list node.
     * @return true if the node is leaf-list node.
     */
    public static boolean isLeafList(CSNode node) {
        return node != null &&
            node.getNodeInfo().getShallowType() ==
            ShallowType.C_LIST.getValue();
    }

    /**
     * Checks if a node is a list node.
     * @return true if the node is list node.
     */
    public static boolean isList(CSNode node) {
        return node != null && (node.getNodeInfo().getFlags() &
                                NodeType.CS_NODE_IS_DYN.getValue()) > 0;
    }
    /**
     * Checks if a node is an action parameter node.
     * @return true if the node is an action parameter node.
     */
    public static boolean isActionParam(CSNode node) {

        return node != null &&(node.getNodeInfo().getFlags() &
                               NodeType.CS_NODE_IS_PARAM.getValue()) >0;
    }

    /**
     * Checks if a node is an action result node.
     * @return true if the node is action result node.
     */
    public static boolean isActionResult(CSNode node) {

        return node != null &&(node.getNodeInfo().getFlags() &
                               NodeType.CS_NODE_IS_RESULT.getValue()) >0;
    }


    /**
     * Checks if a node config data.
     * @return treu if the node is config data.
     */
    public static boolean isValid(CSNode node) {
        return node != null && ((node.getNodeInfo().getFlags() &
                                 NodeType.CS_NODE_IS_DYN.getValue()) >0 ||
                                ( node.getNodeInfo().getFlags() &
                                  NodeType.CS_NODE_IS_CDB.getValue()) >0 ||
                node.getNodeInfo()
                                .getShallowType() ==
                                ShallowType.C_LIST.getValue());

    }

    /**
     * Checks if the node is writable.
     * @return true if the node is writable.
     */
    public static boolean isWritable(CSNode node) {
        return node != null && (node.getNodeInfo().getFlags() &
                                NodeType.CS_NODE_IS_WRITE.getValue()) > 0;
    }

    /**
     * Checks if the node is a notification
     * @return true if the node a notification
     */
    public static boolean isNotif(CSNode node) {
        return node != null && (node.getNodeInfo().getFlags() &
                                NodeType.CS_NODE_IS_NOTIF.getValue()) > 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    /*
    public static String toString(CSNode node){
        String str = node.getNodeInfo().class.getSimpleName();
        if (node != null){
            str += "["+node.getTag()+","+
                    node.getNodeInfo().getFlags()+","+
                    node.getNodeInfo().getShallowType() +"]";
        } else if (root != null){
            str += "["+root+"]";
        } else if (module != null){
            str += "["+module.getNS()+"]";
        } else {
            str += "[null]";
        }
        return str;
    }
    */

    /**
     * @return true if node is OPER data.
     */
    public static boolean isOper(CSNode node) {
        return !isWritable(node);
    }

    public static String printNodeType(CSNode node) {
        StringBuffer stb = new StringBuffer();
        int flags = node != null ? node.getNodeInfo().getFlags() : 0;

        if ((flags & NodeType.CS_NODE_IS_DYN.getValue()) > 0) {
            stb.append("-DYN");
        }
        if ((flags & NodeType.CS_NODE_IS_WRITE.getValue()) > 0) {
            stb.append("-WRITE");
        }
        if ((flags & NodeType.CS_NODE_IS_CDB.getValue()) > 0) {
            stb.append("-CDB");
        }
        if ((flags & NodeType.CS_NODE_IS_ACTION.getValue()) > 0) {
            stb.append("-ACTION");
        }
        if ((flags & NodeType.CS_NODE_IS_PARAM.getValue()) > 0) {
            stb.append("-PARAM");
        }
        if ((flags & NodeType.CS_NODE_IS_RESULT.getValue()) > 0) {
            stb.append("-RESULT");
        }
        if ((flags & NodeType.CS_NODE_IS_NOTIF.getValue()) > 0) {
            stb.append("-NOTIF");
        }
        if ((flags & NodeType.CS_NODE_IS_CASE.getValue()) > 0) {
            stb.append("-CASE");
        }

        return stb.toString();
    }



}
