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

import com.tailf.util.Fxs;
import com.tailf.util.Cs;
import com.tailf.conf.*;

import java.util.Enumeration;

public class test {


    /** ------------------------------------------------------------
     *   main
     *
     */
    static public void main(String args[]) {

        trace("hello world!");
        try {

            trace("loading fxs...");
            Fxs fxs = Fxs.load("mtest.fxs");
            trace("done...");

            trace("Hashtable: ");

            for(Enumeration<Cs> e= fxs.table.elements(); e.hasMoreElements(); ) {
                System.err.println( e.nextElement() );
            }

        } catch (Exception e) {
            System.err.println("(closing) "+e.getMessage());
        }
    }

    static public void trace(String str) {
        System.err.println("*test: "+str);
    }

}