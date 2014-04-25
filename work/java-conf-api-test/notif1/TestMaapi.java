/*    -*- JAVA -*-
 *
 *  Copyright 2007 Tail-F Systems AB. All rights reserved.
 *
 *  This software is the confidential and proprietary
 *  information of Tail-F Systems AB.
 *
 *  $Id$
 *
 */

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfValue;
import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;

/*
 * The Dp transaction context
 * Each transaction is running in separate thread.
 *
 **/

public class TestMaapi extends Thread {

    private ArrayList<Object> cmds= new ArrayList<Object>();

    public static final int CREATE= 1;
    public static final int SET_ELEM= 2;
    public static final int DELETE= 3;
    public static final int SET_NAMESPACE= 4;
    public static final int START_TRANS= 5;
    public static final int VALIDATE_TRANS= 6;
    public static final int PREPARE_TRANS= 7;
    public static final int COMMIT_TRANS= 8;
    public static final int APPLY_TRANS= 9;
    public static final int END_TRANS= 10;
    public static final int END_SESSION= 11;
    public static final int INIT_UPGRADE=12;
    public static final int PERFORM_UPGRADE=13;
    public static final int COMMIT_UPGRADE=14;
    public static final int ABORT_UPGRADE=15;
    public static final int ABORT_TRANS=16;



    public TestMaapi() {
    }

    public void create(String arg) {
        push( CREATE );
        push( arg );
    }

    public void delete(String arg) {
        push( DELETE );
        push( arg );
    }

    public void setElem(String path, ConfValue value) {
        push( SET_ELEM );
        push( path );
        push( value );
    }

    public void endTrans() {
        push( END_TRANS );
    }

    public void endSession() {
        push( END_SESSION );
    }

    public void setNamespace(String ns_uri) {
        push( SET_NAMESPACE );
        push( ns_uri );
    }

    public void startTrans() {
        push( START_TRANS );
    }

    public void validateTrans() {
        push( VALIDATE_TRANS );
    }

    public void prepareTrans() {
        push( PREPARE_TRANS );
    }

    public void commitTrans() {
        push( COMMIT_TRANS );
    }

    public void applyTrans() {
        push( APPLY_TRANS );
    }

    public void initUpgrade(int timeout, int flag) {
        push(INIT_UPGRADE);
        push(timeout);
        push(flag);
    }

    public void performUpgrade(String[] paths) {
        push(PERFORM_UPGRADE);
        push(paths);
    }

    public void commitUpgrade() {
        push(COMMIT_UPGRADE);
    }

    public void abortUpgrade() {
        push(ABORT_UPGRADE);
    }

    public void abortTrans() {
        push(ABORT_TRANS);
    }


    private void push(int x) {
        push( new Integer(x) );
    }

    private void push(Object x) {
        cmds.add( x );
    }

    private Object pop() {
        if (cmds.isEmpty()) return new Integer(-1);
        Object x = cmds.get(0);
        cmds.remove(0);
        return x;
    }

    /**
     * Runs the thread.
     *
     */
    public void run() {
    if (cmds.size() == 0) return;

        System.err.println("*TestMaapi: running thread");
        Socket s;
        try {
            String path;
            ConfValue value;
            s= new Socket("localhost",Conf.PORT);
            Maapi maapi= new Maapi( s );
            maapi.startUserSession("admin", InetAddress.getLocalHost(),
                                   "maapi", new String[] {"admin"},
                                   MaapiUserSessionFlag.PROTO_TCP);

            int th= -1;
            int op = ((Integer) pop()).intValue();
            while( op != -1) {

                switch(op) {
                case CREATE:
                    path = (String) pop();
                    trace("CREATE: "+path);
                    maapi.create(th,path);
                    break;
                case SET_ELEM:
                    path= (String) pop();
                    value= (ConfValue) pop();
                    trace("SET_ELEM: "+path+" -> "+value);
                    maapi.setElem(th,value,path);
                    break;
                case DELETE:
                    path= (String) pop();
                    trace("DELETE: "+path);
                    maapi.delete(th,path);
                    break;
                case START_TRANS:
                    trace("START_TRANS");
                    th = maapi.startTrans(Conf.DB_RUNNING,
                                          Conf.MODE_READ_WRITE);
                    break;
                case SET_NAMESPACE:
                    String ns= (String) pop();
                    trace("SET_NAMESPACE: "+ns);
                    maapi.setNamespace(th,ns);
                    break;
                case VALIDATE_TRANS:
                    trace("VALIDATE_TRANS");
                    maapi.validateTrans(th, false, true);
                    break;
                case PREPARE_TRANS:
                    trace("PREPARE_TRANS");
                    maapi.prepareTrans(th);
                    break;
                case COMMIT_TRANS:
                    trace("COMMIT_TRANS");
                    maapi.commitTrans(th);
                    break;
                case APPLY_TRANS:
                    trace("APPLY_TRANS");
                    maapi.applyTrans(th,false);
                    break;
                case END_TRANS:
                    trace("END_TRANS");
                    maapi.finishTrans(th);
                    th= -1;
                    break;
                case END_SESSION:
                    trace("END_SESSION");
                    maapi.endUserSession();
                    break;
                case INIT_UPGRADE:
                        int timeout = ((Integer) pop()).intValue();
                        int flag = ((Integer) pop()).intValue();
                        trace("INIT_UPGRADE");
                        maapi.initUpgrade(timeout, flag);
                    break;
                case PERFORM_UPGRADE:
                        String[] paths = (String[]) pop();
                        trace("PERFORM_UPGRADE");
                        maapi.performUpgrade(paths);
                    break;
                case COMMIT_UPGRADE:
                        trace("COMMIT_UPGRADE");
                        maapi.commitUpgrade();
                    break;
                case ABORT_UPGRADE:
                        trace("ABORT_UPGRADE");
                        maapi.abortUpgrade();
                    break;
                case ABORT_TRANS:
                        trace("ABORT_TRANS");
                        maapi.abortTrans(th);
                    break;
                default:
                    trace("UNKNOWN_OP: "+op);
                }
                op = ((Integer) pop()).intValue();
            }
            s.close();
        } catch (Exception e) {
            trace("got exception: "+e);
        }
        trace("end TestMaapi");
    }



    /**
     * Trace function
     */
    private void trace(String str) {
        System.err.println("*TestMaapi: "+str);
    }

}
