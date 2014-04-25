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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import com.tailf.test.dp9.namespaces.*;
import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpTransCallback;
import org.apache.log4j.Logger;

public class SimpleWithTrans {
    private static Logger LOGGER = Logger.getLogger(SimpleWithTrans.class);

    /**
     * This is our simple database.
     * Methods for accessing the database are
     * synchronized to allow access from several threads.
     */
    private static ArrayList<Object> running_db;
    private static boolean is_locked= false;


    /**
     * Locks the database
     */
    public synchronized static void lock() {
        is_locked = true;
    }


    /**
     * Unlocks the database
     */
    public synchronized static void unlock() {
        is_locked =false;
    }


    /**
     * Find server in database and return it.
     */
    public synchronized static Server findServer(String s) {
        for (int i=0; i<running_db.size(); i++) {
            Server x = (Server) running_db.get(i);
            if (x.name.equals(s)) return x;
        }
        return null;
    }


    /**
     * Gives iterator for our server table
     */
    public synchronized static Iterator<Object> iterator() {
        return running_db.iterator();
    }


    /**
     * Remove server from database
     */
    public synchronized static void removeServer(String s) {
        for (int i=0; i<running_db.size(); i++) {
            Server x = (Server) running_db.get(i);
            if (x.name.equals(s)) {
                running_db.remove(i);
                return;
            }
        }
    }


    /**
     * Create new server in database
     */
    public synchronized static void newServer(Server s) {
        running_db.add(s);
    }


    /**
     * number of servers
     */
    public synchronized static int numServers() {
        return running_db.size();
    }


    /**
     *
     */
    public synchronized static Server getServer(int index) {
        return (Server) running_db.get(index);
    }


    /**
     * Restores DB from FILE
     */
    public synchronized static boolean restore(String filename) {
        running_db = new ArrayList<Object>();  /* clear the DB */
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String str;
            LOGGER.debug("Restoring "+filename);
            while ((str = in.readLine()) != null) {
                String[] data = str.split("\t");
                String name = data[0];
                String[] addrStr = data[1].split("\\.");
                int[] addr = new int[4];
                addr[0]= new Integer(addrStr[0]).intValue();
                addr[1]= new Integer(addrStr[1]).intValue();
                addr[2]= new Integer(addrStr[2]).intValue();
                addr[3]= new Integer(addrStr[3]).intValue();
                int port = new Integer(data[2]).intValue();
                Server s= new Server(name,addr,port);
                running_db.add( s );
                LOGGER.debug("restored server: "+s);
            }
            in.close();
            return true;
        } catch (IOException e) {
            LOGGER.debug("restore failed: "+e.getMessage());
            return false;
        }
    }


    /**
     * Saves DB to FILE
     */
    public synchronized static void save(String filename) throws IOException {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            LOGGER.debug("saving: "+filename);
            for (int i=0; i<running_db.size(); i++) {
                Server s= (Server) running_db.get(i);
                out.write( s.name );
                out.write( "\t");
                out.write( new Integer( s.addr[0] ) +
                           "." + new Integer( s.addr[1] ) +
                           "." + new Integer( s.addr[2] ) +
                           "." + new Integer( s.addr[3] ));
                out.write( "\t");
                out.write( new Integer( s.port ).toString() );
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            LOGGER.debug("failed to save: "+filename+ " got: "+e.getMessage());
            throw e;
        }
    }


    /**
     * Deletes the DB file
     */
    public synchronized static void unlink(String filename) {
        File f = new File(filename);
        f.delete();
    }


    /**
     * Renames a DB file
     */
    public synchronized static void rename(String oldname, String newname)
        throws DpCallbackException {
        File file = new File(oldname);
        File file2 = new File(newname);
        boolean success = file.renameTo(file2);
        if (!success) {
            throw new DpCallbackException("failed to rename file");
        }
        LOGGER.debug("renaming: "+oldname+" --> "+newname);
    }


    /**
     * init DB
     */
    public static void initDb() throws IOException {
        if ( restore("running.DB")) return;
        running_db = new ArrayList<Object>();
        running_db.add( new Server("s1", new int[] {192,168,0,1}, 22));
        running_db.add( new Server("s2", new int[] {192,168,0,2}, 23));
        running_db.add( new Server("s3", new int[] {192,168,0,3}, 24));
        running_db.add( new Server("s4", new int[] {192,168,0,4}, 25));
        running_db.add( new Server("s5", new int[] {192,168,0,5}, 26));
        running_db.add( new Server("s6", new int[] {192,168,0,6}, 27));
        save("running.DB");
    }


    public synchronized static int findIndex(String s) {
        for (int i=0; i<running_db.size(); i++) {
            Server x = (Server) running_db.get(i);
            if (x.name.equals(s)) return i;
        }
        return -1;
    }

    public static void move(int from, int to) {
        Server s = (Server) running_db.get(from);

        ArrayList<Object> sortedList = new ArrayList<Object>();
        if (to < 0) {
            sortedList.add(s);
        }
        for (int i=0; i<running_db.size(); i++) {
            if (i==from) {
                continue;
            }
            sortedList.add(running_db.get(i));
            if (i==to){
                sortedList.add(s);
            }
        }

        running_db = sortedList;
    }

    /** ------------------------------------------------------------
     *   main
     *
     */
    static public void main(String args[]) {
        Dp dp = null;
        try {

            /* initialize our database */
            initDb();

            /* create the callbacks */
            SimpleTransCb trans_cb = new SimpleTransCb();
            SimpleDataCb data_cb = new SimpleDataCb();
            SimplePortValpointCb val_cb = new SimplePortValpointCb();
            SimpleTransValidateCb transval_cb = new SimpleTransValidateCb();
            SimpleActionCb action_cb = new SimpleActionCb();
            /* create new control socket */
            Socket ctrl_socket= new Socket("127.0.0.1",Conf.PORT);

            /* init and connect the control socket */
            dp = new Dp("server_daemon",ctrl_socket);
            // Dp dp = new Dp("server_daemon",ctrl_socket);

            /* register the callbacks */
            dp.registerAnnotatedCallbacks( trans_cb );
            dp.registerAnnotatedCallbacks( data_cb );
            dp.registerAnnotatedCallbacks( val_cb );
            dp.registerAnnotatedCallbacks( transval_cb );
            dp.registerAnnotatedCallbacks( action_cb );
            dp.registerDone();

            /* also need to provide namespace for the smp.cs
             * this is generated with --emit-java
             */
            dp.addNamespace( new smp() );

            /* read input from control socket */
            while (true) dp.read();

        } catch (Exception e) {
            LOGGER.error("(closing) "+e.getMessage(),e);
        } finally{
            dp.shutDownThreadPoolNow();
        }
    }


}