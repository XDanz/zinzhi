package com.tailf.test.dp9;
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


public class Server {

    public Server(String name) {
        this.name = name;
    }

    public Server(String name, int[] addr, int port) {
        this.name = name;
        this.addr = addr;
        this.port = port;
    }

    public String name;
    public int[] addr = new int[] { 0,0,0,0 };
    public int port = 0;

    public String toString() {
        return new String( "server{"+name + ", "+ addr[0]+"."+addr[1]+"."+addr[2]+"."+addr[3]+
                           ", "+port+"}");
    }

}
