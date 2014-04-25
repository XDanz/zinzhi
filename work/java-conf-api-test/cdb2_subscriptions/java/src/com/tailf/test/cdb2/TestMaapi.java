package com.tailf.test.cdb2;
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
import org.apache.log4j.Logger;
/*
 * The Dp transaction context
 * Each transaction is running in separate thread.
 *
 **/

public class TestMaapi extends Thread {

    private ArrayList<Object> cmds= new ArrayList<Object>();
    private static Logger LOGGER = Logger.getLogger(TestMaapi.class);
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
    public static final int REVERT_TRANS=12;
    public static final int NEW_USER_SESSION=13;



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

    public void revertTrans() {
        push( REVERT_TRANS );
    }


    public void startSession() {
        push ( NEW_USER_SESSION );
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
        LOGGER.debug("*TestMaapi: running thread");
        Socket s = null;
        try {

            String path;
            ConfValue value;
            s = new Socket("localhost",Conf.PORT);
            Maapi maapi= new Maapi( s );

            int th= -1;
            int op = ((Integer) pop()).intValue();
            while( op != -1) {

                try {
                    switch(op) {
                    case NEW_USER_SESSION:
                        maapi
                            .startUserSession("admin",
                                              InetAddress
                                              .getLocalHost(),
                                              "maapi",
                                              new String[] {"admin"},
                                              MaapiUserSessionFlag
                                              .PROTO_TCP);
                        break;
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
                    case REVERT_TRANS:
                        trace("REVERT_TRANS");
                        maapi.abortTrans(th);
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
                    default:
                        trace("UNKNOWN_OP: "+op);
                    }
                } catch (Exception e) {
                    trace("got exception: "+e);
                }

                op = ((Integer) pop()).intValue();
            }
        } catch (Exception e) {
            trace("got exception: "+e);
        } finally {
            try {
                s.close();
            } catch (Exception e2) {
                trace("got exception: " + e2);
            }
        }
    }



    /**
     * Trace function
     */
    private void trace(String str) {
        LOGGER.debug("*TestMaapi: "+str);
    }

}
