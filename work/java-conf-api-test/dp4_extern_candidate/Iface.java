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

public class Iface {

    public Iface(String name) {
        this.name= name;
    }

    public Iface(String name, int[] addr, int[] mask, boolean enabled) {
        this.name = name;
        this.addr = addr;
        this.mask = mask;
        this.enabled = enabled;
    }

    public String name;
    public int[] addr = new int[] { 0,0,0,0 };
    public int[] mask = new int[] { 0,0,0,0 };
    public boolean enabled = false;

    public String toString() {
        return new String( "iface{"+name + ", "+ addr[0]+"."+addr[1]+"."+addr[2]+"."+addr[3]+
                           "/"+ mask[0]+"."+mask[1]+"."+mask[2]+"."+mask[3]+ " : "+enabled+"}");
    }

}
