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
import java.util.Date;
import java.io.IOException;

import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfUInt16;

import com.tailf.dp.DpActionTrans;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.annotations.ActionCallback;
import com.tailf.dp.proto.ActionCBType;
import org.w3c.dom.Document;
import com.tailf.util.*;

import com.tailf.test.dp.action.namespaces.*;

import org.apache.log4j.Logger;

public class ActionCbTest {

    private static Logger LOGGER  = Logger.getLogger(ActionCbTest.class);


    @ActionCallback(callPoint="test-action", callType=ActionCBType.INIT)
        public void init(DpActionTrans trans) throws DpCallbackException {
        LOGGER.info("userinfo= "+ trans.getUserInfo());

    }


    @ActionCallback(callPoint="test-action", callType=ActionCBType.ACTION)
        public ConfXMLParam[] action(DpActionTrans trans, ConfTag name,
                                     ConfObject[] kp,
                                     ConfXMLParam[] params)
        throws DpCallbackException,ConfException {


        LOGGER.info("Param:" + ConfXMLParam.toXML(params));

        //Date date = new Date();
        ConfNamespace ns = new testaction();//new config();

        LOGGER.info("action(uinfo="+trans.getUserInfo()+", name="+name+")");

        /* check which action we should invoke */
        //
        ConfXMLParam[] result =
            new ConfXMLParam[] {

            new ConfXMLParamValue(ns.hash(),
                                  testaction._result,
                                  new ConfBuf("Ok") ),
            new ConfXMLParamValue(ns.hash()
                                  , testaction._purged_alarms,
                                  new ConfUInt16(10))
        };

        LOGGER.info("XML:" + ConfXMLParam.toXML(result));
        return result;
    }


    /*

    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.COMMAND)
        public String[] command(DpActionTrans trans, String cmdname, String cmdpath, String[] params)
        throws DpCallbackException {

        String[] result = null;
        Date date = new Date();
        ArrayList<ConfNamespace>  ns_list = trans.getNsList();

        LOGGER.info("command(uinfo="+trans.uinfo+", cmdname="+cmdname+")");
        LOGGER.info("cmdpath= "+cmdpath);


        if (cmdname.equals("reboot")) {
            LOGGER.info("command: reboot");
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
            throw new DpCallbackException("got bad command: "+cmdname);
    }

    ***/


}
