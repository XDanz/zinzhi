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



import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.*;
import java.io.IOException;
import org.xml.sax.SAXException;


public class Test6 {

    public static final int port = 4565;

    public static void main(String[] arg) throws Exception {
        new Test6().test1();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception {
        Socket s =
            new Socket("127.0.0.1", port);
        Maapi maapi = new Maapi(s);


        maapi.startUserSession("jb",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int th =   maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);


        ConfPath path =
            maapi.xpath2kpath("/mtest/servers/server[name=s1]/ip");

        System.out.println("path:"+ path);

        ConfPath path2 =
            maapi.xpath2kpath("/mtest/movables/movable[a='1'][*]");

        System.out.println(path2);
    }


    public void test1() throws Exception {
        DocumentBuilderFactory domFactory =
            DocumentBuilderFactory.newInstance();

        domFactory.setNamespaceAware(true);

        DocumentBuilder builder =
            domFactory.newDocumentBuilder();

        Document doc = builder.parse("servers.xml");
        XPath xpath =
            XPathFactory.newInstance().newXPath();

        XPathExpression expr =
            xpath.compile("//*[name()='name']");
        //Select all elements with name BBB, equivalent with //BBB
        System.out.println("Count all occurence of server,");

        Object result =
            expr.evaluate(doc, XPathConstants.NODESET);

        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println(nodes.item(i).getNodeName());
        }
        /*
        System.out.println("Select all elements name of which starts " +
                           "with letter B");

        expr = xpath.compile("//*[starts-with(name(),'a')]");
        //Select all elements name of which starts with letter B
        result = expr.evaluate(doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println(nodes.item(i).getNodeName());
        }
        System.out.println("Select all elements name of which contain" +
                           " letter C");
        expr = xpath.compile("//*[contains(name(),'b')]");
        //Select all elements name of which contain letter C
        result = expr.evaluate(doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println(nodes.item(i).getNodeName());
        }
        */
    }

}