package com.tailf.test.dp2;

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

import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpTransCallback;
import com.tailf.dp.DpTransValidateCallback;
import com.tailf.dp.DpValpointCallback;
import com.tailf.maapi.Maapi;

import org.apache.log4j.Logger;

import com.tailf.test.dp2.namespaces.smp;

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
            LOGGER.info("Restoring "+filename);
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
                LOGGER.info("restored server: "+s);
            }
            in.close();
            return true;
        } catch (IOException e) {
            LOGGER.info("restore failed: "+e.getMessage());
            return false;
        }
    }


    /**
     * Saves DB to FILE
     */
    public synchronized static void save(String filename) throws IOException {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            LOGGER.info("saving: "+filename);
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
            LOGGER.info("failed to save: "+filename+ " got: "+e.getMessage());
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
    public synchronized static void rename(String oldname, String newname) throws DpCallbackException {
        File file = new File(oldname);
        File file2 = new File(newname);
        boolean success = file.renameTo(file2);
        if (!success) {
            throw new DpCallbackException("failed to rename file");
        }
        LOGGER.info("renaming: "+oldname+" --> "+newname);
    }


    /**
     * init DB
     */
    public static void initDb() throws IOException {
        if ( restore("running.DB")) return;
        running_db = new ArrayList<Object>();
        running_db.add( new Server("ssh", new int[] {194,168,0,1}, 22));
        running_db.add( new Server("www", new int[] {194,168,128,11}, 80));
        running_db.add( new Server("smpt", new int[] {194,168,128,1}, 25));
        save("running.DB");
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

                /* create maapi instance to be used by the
                   validate callbacks */
                Maapi maapi =
                new Maapi( new Socket("localhost", Conf.PORT) );

                /* validation callbacks */
                SimpleTransValidateCb validate_cb =
                new SimpleTransValidateCb(maapi);
                SimpleIpValpointCb validateIp =
                new SimpleIpValpointCb(maapi);
                SimplePortValpointCb validatePort =
                new SimplePortValpointCb(maapi);

                /* create new control socket */
                Socket ctrl_socket= new Socket("127.0.0.1",Conf.PORT);

                /* init and connect the control socket */
                dp = new Dp("server_daemon",ctrl_socket);
                // Dp dp = new Dp("server_daemon",ctrl_socket);

                /* register the callbacks */
                dp.registerAnnotatedCallbacks( trans_cb );
                dp.registerAnnotatedCallbacks( data_cb );

                /* register the validation callbacks and valpoints */
                dp.registerAnnotatedCallbacks( validate_cb);
                dp.registerAnnotatedCallbacks( validateIp );
                dp.registerAnnotatedCallbacks( validatePort );

                /* all registrations done */
                dp.registerDone();

                /* also need to provide namespace for the smp.cs
                 * this is generated with --emit-java
                 */
                dp.addNamespace( new smp() );

                /* read input from control socket */
                while (true) dp.read();

            } catch (Exception e) {
            System.err.println("(closing) "+e.getMessage());
        }finally{
                dp.shutDownThreadPoolNow();
        }
    }


}