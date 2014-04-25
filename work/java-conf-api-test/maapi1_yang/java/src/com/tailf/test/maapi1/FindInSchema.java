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


import java.util.List;

import com.tailf.dp.Dp;

import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.maapi.MaapiSchemas.CSNode;
import com.tailf.test.maapi1.namespaces.*;



import org.apache.log4j.Logger;


public class FindInSchema {

    private static Logger log =
        Logger.getLogger(FindInSchema.class);
    Socket s;
    Maapi maapi;
    MaapiSchemas sch;

    public static final int port = 4565;

    private Queue<CSNode> queue =
        new LinkedList<CSNode>();

    public static void main(String[] arg){

        String tagval = arg[0];
        int itagval = -1;
        try{
            itagval = Integer.parseInt(tagval);

        }catch(NumberFormatException e){
            e.printStackTrace();
        }

        FindInSchema f = new FindInSchema();
        System.out.println(f.find(itagval));


    }

    public CSNode find(int hash){
        return bfs(hash);
    }

    public FindInSchema()  {
        //log.info("FINDINSCHEMA");
        //System.out.println("findinshcmea");
        try{
            s =
                new Socket("127.0.0.1", port);
            maapi =
                new Maapi(s);

            maapi.loadSchemas();

        }catch(Exception e){
            e.printStackTrace();
        }
        MaapiSchemas sch =
            Maapi.getSchemas();


        MaapiSchemas.CSSchema mtestSchema =
            sch.findCSSchema((long)new mtest().hash());

        //        log.info("root:" +
        //                 mtestSchema);

        MaapiSchemas.CSNode root =
            mtestSchema.getRootNode();

        //log.info("--- Root Node ---");
        //log.info("root:" + root.getTag());
        queue.offer(root);


        //log.info(root);

        //log.info("--- Node Tree ---");


    }



    private CSNode bfs(int hash){

        Set<CSNode> visited =
            new HashSet<CSNode>();

        while(!queue.isEmpty()){

            CSNode next = queue.poll();

            if(next == null)
                log.info("child is null..");

            //log.info("tag:" + next.getTag());

            //log.info(next.getTagHash());

            if(hash == next.getTagHash())
                return next;


            Collection<CSNode> children =
                next.getChildren();


            if(children != null){
                //    log.info(next.getTag() + " have " + children.size() +
                //          " children");

                for(CSNode node: children){
                    if(visited.contains(node)){
                        continue;
                    }
                    visited.add(node);
                    queue.offer(node);
                }
            }
        }
        return null;
    }

    public static boolean isLeaf(CSNode node){

        boolean stat = false;


        int type = node.getNodeInfo().getShallowType();
        log.debug("type:" + type);

        log.debug("test 0:" + (node.getChildren() == null));
        log.debug("test 1: " +  (type > ShallowType.C_XMLTAG.getValue()));
        log.debug("test 2: " +  (type != ShallowType.C_LIST.getValue()));
        log.debug("test 3: " +  (type != ShallowType.C_XMLBEGIN.getValue()));
        log.debug("test 3: " +  (type != ShallowType.C_XMLEND.getValue()));
        log.debug("test 4: " +  (type != ShallowType.C_DEFAULT.getValue()));

        stat = (node.getChildren() == null) &&
            (type > ShallowType.C_XMLTAG.getValue()) &&
            (type != ShallowType.C_LIST.getValue()) &&
            (type != ShallowType.C_XMLBEGIN.getValue()) &&
            (type != ShallowType.C_XMLEND.getValue()) &&
            (type != ShallowType.C_DEFAULT.getValue());

        //log.info("stat:" + stat);

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
                                node.getNodeInfo().getShallowType() ==
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
