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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfTag;
import com.tailf.dp.DpActionTrans;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.annotations.ActionCallback;
import com.tailf.dp.proto.ActionCBType;
import org.apache.log4j.Logger;
public class SimpleActionCb {

    private static Logger LOGGER = Logger.getLogger(SimpleActionCb.class);
    public String received_opaque = null;

    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.INIT)
        public void init(DpActionTrans trans) throws DpCallbackException {
    }


    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.ACTION)
        public ConfXMLParam[] action(DpActionTrans trans, ConfTag name,
                                     ConfObject[] kp, ConfXMLParam[] params)
        throws DpCallbackException {
        try {
            if (received_opaque == null) {
                received_opaque = trans.getOpaque();
                File f = new File("opaque.prop");
                Properties testprop = new Properties();

                if (f.exists()) {
                    testprop.load(new FileInputStream(f));
                }
                testprop.put("ACTION", received_opaque);
                FileOutputStream fw = new FileOutputStream(f);
                testprop.save(fw, "opaque test props");
                fw.close();
            }
        } catch (Exception e) {
            throw new DpCallbackException(e.getMessage());
        }

        return null;
    }


    @ActionCallback(callPoint="reboot-point", callType=ActionCBType.COMMAND)
        public String[] command(DpActionTrans trans, String cmdname,
                                String cmdpath, String[] params)
        throws DpCallbackException {

        return null;
    }

}
