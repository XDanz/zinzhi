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

import java.util.ArrayList;

public class Host implements java.io.Serializable {

    /**
         *
         */
        private static final long serialVersionUID = -972917996303025281L;

        public Host(String name) {
        this.name = name;
    }

    public Host(String name, String domain, int[] defgw, ArrayList<Object> ifaces) {
        this.name = name;
        this.domain = domain;
        this.defgw = defgw;
        this.ifaces = ifaces;
    }

    public String name;
    public String domain;
    public int[] defgw = new int[] { 0,0,0,0 };
    public ArrayList<Object> ifaces= new ArrayList<Object>();

    public String toString() {
        return new String( "host{"+name + ", "+ domain+
                           ", defgw="
                           +defgw[0]+"."+defgw[1]+"."+defgw[2]+"."+defgw[3]+
                           "}");
    }

}
