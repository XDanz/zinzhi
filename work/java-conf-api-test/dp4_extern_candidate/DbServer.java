/**
 * Copyright 2005 Tail-F Systems AB
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpDbCallback;
import com.tailf.dp.DpTransCallback;

public class DbServer {

    /** This _is_ our database */

    private static ArrayList<Object> running_db = new ArrayList<Object>();
    private static ArrayList<Object> cand_db = new ArrayList<Object>();
    private static int lock = 0;
    static boolean candidate_modified = false;

    public static void show(int DBNAME) {
        ArrayList<Object> db = getDb(DBNAME);
        System.out.println(db);
    }

    public static void lock(int DBNAME) {
        lock = DBNAME;
    }

    public static void unlock(int DBNAME) {
        lock= 0;
    }

    private static ArrayList<Object> getDb(int DBNAME) {
        if (DBNAME == Conf.DB_CANDIDATE)
            return  cand_db;
        return running_db;
    }

    public static void clearDb(int DBNAME) {
        if (DBNAME== Conf.DB_CANDIDATE)
            cand_db = new ArrayList<Object>();
        else if (DBNAME == Conf.DB_RUNNING)
            running_db = new ArrayList<Object>();
    }

    public static Host findHost(int DBNAME,String name) {
        ArrayList<Object> l= getDb(DBNAME);
        for (int i=0 ; i<l.size(); i++) {
            Host h = (Host) l.get(i);
            if ( h.name.equals(name) ) return h;
        }
        return null;
    }

    public static boolean removeHost(int DBNAME, String name) {
        ArrayList<Object> l= getDb(DBNAME);
        for (int i=0 ; i<l.size(); i++) {
            Host h = (Host) l.get(i);
            if ( h.name.equals(name) ) {
                l.remove(i);
                return true;
            }
        }
        return false;
    }

    public static void addHost(int DBNAME, Host host) {
        ArrayList<Object> l= getDb(DBNAME);
        l.add( host );
    }

    public static Iface findIface(int DBNAME, String hostname, String name) {
        Host h= findHost(DBNAME,hostname);
        if (h == null) return null;
        ArrayList<Object> l = h.ifaces;
        for (int i=0; i<l.size(); i++) {
            Iface x = (Iface) l.get(i);
            if ( x.name.equals(name)) return x;
        }
        return null;
    }

    public static boolean removeIface(int DBNAME, String hostname, String name) {
        Host h= findHost(DBNAME,hostname);
        if (h == null) return false;
        ArrayList<Object> l = h.ifaces;
        for (int i=0; i<l.size(); i++) {
            Iface x = (Iface) l.get(i);
            if ( x.name.equals(name)) {
                l.remove(i);
                return true;
            }

        }
        return false;
    }

    public static void candCopy() {
        /* copy candidate into running, slow but correct */
        unlink("tmp_db");
        dumpDb(Conf.DB_CANDIDATE, "tmp_db");
        clearDb(Conf.DB_RUNNING);
        running_db = cand_db;  /* swap */
        cand_db = restore("tmp_db");
        unlink("tmp_db");
        candidate_modified = false;
    }

    public static Iterator<Object> iterator(int DBNAME) {
        if (DBNAME == Conf.DB_CANDIDATE)
            return cand_db.iterator();
        return running_db.iterator();
    }

    public static int numInstances(int DBNAME) {
        if (DBNAME== Conf.DB_CANDIDATE)
            return cand_db.size();
        return running_db.size();
    }

    public static int[] IP(int a, int b, int c, int d) {
        return new int[] {a,b,c,d};
    }

    public static void candidateReset() {
        trace("candidateReset():");
        unlink("tmp_db");
        dumpDb(Conf.DB_RUNNING,"tmp_db");
        cand_db = running_db; /* swap */
        running_db = restore("tmp_db");
    }

    private static void defaultDb() {
        ArrayList<Object> iflist = new ArrayList<Object>();
        iflist.add( new Iface("eth0", IP(192,168,1,61),IP(255,255,255,0),true));
        iflist.add( new Iface("eth1", IP(10,77,1,44),IP(255,255,0,0),false));
        iflist.add( new Iface("lo",IP(127,0,0,1),IP(255,0,0,0),true));
        running_db.add( new Host("buzz","tail-f.com",IP(192,168,1,1),iflist));
        iflist = new ArrayList<Object>();
        iflist.add( new Iface("eth0", IP(192,168,1,61),IP(255,255,255,0),true));
        iflist.add( new Iface("lo0", IP(127,0,0,1),IP(255,0,0,0),true));
        running_db.add( new Host("earth","tail-f.com",IP(192,168,1,1),iflist));
    }

    public static ArrayList<Object> restore(int dbname, String filename)  {
        ArrayList<Object> list = restore(filename);
        if (dbname == Conf.DB_CANDIDATE)
            cand_db = list;
        else
            running_db = list;
        return list;
    }

    public static ArrayList<Object> restore(String filename) {
        trace("restore: "+filename);
        ArrayList<Object> list = null;
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            trace("Loading ArrayList Object...");
            list = (ArrayList<Object>)in.readObject();
            in.close();
            fileIn.close();

        } catch (Exception e) {
            trace("failed restore: "+ e.getMessage());
        }
        return list;
    }

    public static void unlink(String filename) {
        File f = new File(filename);
        f.delete();
    }

    public static void dumpDb(int dbname, String filename) {
        if (dbname == Conf.DB_CANDIDATE) {
            trace("dumbDb: CANDIDATE: "+filename);
            dumpDb( cand_db, filename );
        } else {
            trace("dumpDb: RUNNING: "+filename);
            dumpDb( running_db, filename );
        }
    }

    public static void dumpDb(ArrayList<Object> list, String filename) {
        try {
            trace("dumpDb: "+ filename);
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(list);
            out.close();
            fileOut.close();

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static public void main(String args[]) {
        Dp dp = null;
        try {

            /* create the callbacks */
            TransCb trans_cb = new TransCb();
            IfaceDataCb if_cb = new IfaceDataCb();
            HostDataCb host_cb = new HostDataCb();
            DbCb db_cb = new DbCb();

            /* create new control socket */
            Socket ctrl_socket= new Socket("127.0.0.1",Conf.PORT);

            /* init and connect the control socket */
            dp = new Dp("extern_db_daemon",ctrl_socket);

            /* register the callbacks */
            dp.registerAnnotatedCallbacks( trans_cb );
            dp.registerAnnotatedCallbacks( if_cb );
            dp.registerAnnotatedCallbacks( host_cb );
            dp.registerAnnotatedCallbacks( db_cb );
            dp.registerDone();

            /* Initialize our simple database  */
            ArrayList<Object> list= null;
            if ((list = restore("RUNNING.ckp")) != null) {
                trace("Restoring running from checkpoint");
                unlink("RUNNING.ckp");
                running_db = list;
            }
            else if ((list = restore("RUNNING.db")) != null) {
                trace("Restoring from RUNNING\n");
                running_db = list;
            }
            else {
                trace("Starting with empty running DB");
                running_db = new ArrayList<Object>();
            }

            /* Initialize candidate */
            if ((list = restore("CANDIDATE.db")) != null) {
                trace("Restoring from CANDIDATE.db");
                cand_db = list;
            }
            else {
                trace("Starting with empty candidate DB");
                cand_db = new ArrayList<Object>();
            }

            /* also need to provide namespace for the hst.cs
             * this is generated with --emit-java
             */
            dp.addNamespace( new hst() );

            /* start thread to read from stdin */
            DbServerCLI cli = new DbServerCLI();
            cli.start();

            /* read input from control socket */
            while (true) dp.read();

        } catch (Exception e) {
            trace("(closing) "+e.getMessage());
        }finally{
            dp.shutDownThreadPoolNow();
        }
    }

    static public void trace(String str) {
        System.err.println("*DbServer: "+str);
    }

}

