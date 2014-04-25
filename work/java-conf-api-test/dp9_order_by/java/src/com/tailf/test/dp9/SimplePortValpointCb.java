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
import java.util.Properties;

import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfValue;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.DpTrans;
import com.tailf.dp.annotations.ValidateCallback;

/**
 * This is a simple validation point callback to
 * check the Port number of a Server
 *
 **/

public class SimplePortValpointCb {

    public String received_opaque = null;

    public SimplePortValpointCb() {
    }

    @ValidateCallback(callPoint="valp")
        public void validate(DpTrans trans, ConfObject [] kp, ConfValue newval)
        throws DpCallbackException {
        try {
            if (received_opaque == null) {
                received_opaque = trans.getOpaque();
                File f = new File("opaque.prop");
                Properties testprop = new Properties();

                if (f.exists()) {
                    testprop.load(new FileInputStream(f));
                }
                testprop.put("VALPOINT", received_opaque);
                FileOutputStream fw = new FileOutputStream(f);
                testprop.save(fw, "opaque test props");
                fw.close();
            }
        } catch (Exception e) {
            throw new DpCallbackException(e.getMessage());
        }
    }

}

