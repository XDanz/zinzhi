package com.tailf.test.dp7;
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
import java.util.NoSuchElementException;
import java.util.Iterator;


/**
 * My simple iterator for Servers in the database.
 *
 *
 */

public class SimpleIterator implements Iterator<Object> {

    ArrayList<Object> list;
    boolean sortOrderSnmp = false;
    Server current= null; // means first

    SimpleIterator( ArrayList<Object> list, String index ) {
        this.list= list;
        if (index != null && index.equals("1"))
            sortOrderSnmp = true;
    }

    public boolean hasNext() {
        if ( findNext() != null ) return true;
        else return false;
    }

    public Server next() throws NoSuchElementException {
        Server next  = findNext();
        if (next != null) current = next;
        return next;
    }


    private Server findNext() {
        Server bestSoFar = null;
        for (int i=0;i<list.size();i++) {
            Server obj = (Server) list.get(i);
            if (current==null || cmp(obj,current)>0)
                if (bestSoFar==null || cmp(obj,bestSoFar)<0)
                    bestSoFar = obj;
        }
        return bestSoFar;
    }

    private int cmp(Server x, Server y) {
        if (sortOrderSnmp) {
            if (x.name.length() < y.name.length()) return -1;
            else if (x.name.length() > y.name.length()) return 1;
        }
        return x.name.compareTo(y.name);
    }

    /**
     * Not supported.
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

