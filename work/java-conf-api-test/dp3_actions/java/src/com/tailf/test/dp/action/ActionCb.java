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
package com.tailf.test.dp.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.io.IOException;

import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfInt32;

import com.tailf.dp.DpActionTrans;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.annotations.ActionCallback;
import com.tailf.dp.proto.ActionCBType;

import com.tailf.test.dp.action.namespaces.*;

import org.apache.log4j.Logger;

public class ActionCb {

    private static Logger LOGGER  = Logger.getLogger(ActionCb.class);

    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.INIT)
        public void init(DpActionTrans trans) throws DpCallbackException {
        LOGGER.info("userinfo= "+ trans.getUserInfo());

    }


    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.ACTION)
        public ConfXMLParam[] action(DpActionTrans trans, ConfTag name,
                                     ConfObject[] kp, ConfXMLParam[] params)
        throws DpCallbackException {

        try {
            // a primitive check that timeoutsetting calls works.
            trans.actionSetTimeout(10);

        } catch (IOException e) {
            LOGGER.error(e);
            throw new DpCallbackException(e.getMessage());
        }

        ConfXMLParam[] result = null;
        Date date = new Date();
        ConfNamespace ns = new config();

        ArrayList<ConfNamespace> ns_list =
            trans.getNsList();

        LOGGER.info("action(uinfo="+trans.getUserInfo()+", name="+name+")");
        /* check which action we should invoke */
        switch (name.getTagHash()) {

        case config._reboot:
            LOGGER.info("action: reboot");
            /* no return value for this action */
            return null;
        case config._restart:
            String mode = ((ConfBuf)params[0].getValue()).toString();
            LOGGER.info("action: restarting mode="+mode);
            LOGGER.info(Arrays.toString(params));
            result= new ConfXMLParam[] {
                new ConfXMLParamValue(ns,config._time,
                                      new ConfBuf( date.toString() ),
                                      ns_list) };
            break;
        case config._reset:
            String when = ((ConfBuf)params[0].getValue()).toString();
            //int when = ((ConfInt32)params[0].val).intValue();

            LOGGER.info("action: reset when: "+when);
            result= new ConfXMLParam[] {
                new ConfXMLParamValue(ns,
                                      config._time,
                                      new ConfBuf( date.toString() ),
                                      ns_list) };
            break;
        default:
            /* this happens only if we forget to update this code when the
               datamodel has changed. */
            throw new DpCallbackException("got bad operation: "+name);
        }
        return result;
    }


    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.COMMAND)
        public String[] command(DpActionTrans trans, String cmdname,
                                String cmdpath, String[] params)
        throws DpCallbackException {

        String[] result = null;
        Date date = new Date();
        ArrayList<ConfNamespace>  ns_list = trans.getNsList();

        LOGGER.info("command(uinfo="+trans.getUserInfo()+", cmdname="+cmdname+")");
        LOGGER.info("cmdpath= "+cmdpath);

        /* check which command we should invoke */
        if (cmdname.equals("reboot")) {
            LOGGER.info("command: reboot");
            /* no return value for this action */
            return null;
        } else if (cmdname.equals("restart")) {
            String mode = params[0];
            LOGGER.info("command: restarting mode="+mode);
            result= new String[] { date.toString() };
            return result;
        } else if (cmdname.equals("reset")) {
            String when = params[0];
            LOGGER.info("action: reset when: "+when);
            result= new String[] { date.toString() };
            return result;
        }
        else
            /* no command matched */
            throw new DpCallbackException("got bad command: "+cmdname);
    }


}
