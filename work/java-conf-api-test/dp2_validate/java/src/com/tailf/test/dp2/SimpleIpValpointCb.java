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

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.ValidateCallback;
import com.tailf.maapi.*;
import org.apache.log4j.Logger;

/**
 * This is a simple validation point callback to
 * check the IP address of a Server
 *
 **/

public class SimpleIpValpointCb {

    private static Logger LOGGER = Logger.getLogger(SimpleIpValpointCb.class);
    Maapi maapi;

    public SimpleIpValpointCb(Maapi maapi) {
        this.maapi = maapi;
    }


    /**
     * The  validate()  callback  should validate the values and
     * throw a DpCallbackException if the validation fails.
     * Theres also a possibility to throw a DpCallbackWarningException
     * with message set to a string describing the warning.
     * The  warnings  will  get propagated  to  the  transaction  engine,
     * and depending on where the transaction originates,
     * ConfD may or may not act on the warnings. If
     * the  transaction  originates  from the CLI or the Web UI, ConfD will
     * interactively present the user with a choice - whereby the  transac-
     * tion can be aborted.
     * <p>
     * If the transaction originates from NETCONF - which does not have any
     * interactive capabilities, the warnings are ignored. The warnings are
     * primarily intended to alert inexperienced users that attempt to make
     * - dangerous - configuration changes. There can be multiple  warnings
     * from multiple validation points in the same transaction.
     *
     * @param trans The transaction
     * @param kp The keypath
     * @param newval The new value to validate
     */
    @ValidateCallback(callPoint= "validateIp")
        public void validate(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {

        LOGGER.info("SimpleIpValpointCB --> ");
        int[] addr =  ((ConfIPv4) newval).getRawAddress();

        if (addr[0] == 192 && addr[1]==168) {
            String str = "local address in subnet 192.168/16";
            throw new DpCallbackWarningException(str);
        }

        if (addr[0] == 10 && addr[1]==0 && addr[2]==0) {
            String str = "local address in subnet 10.0.0/24";
            throw  new DpCallbackWarningException(str);
        }

        LOGGER.info("SimpleIpValpointCB --> OK");
    }


    public void trace(String str) {

        LOGGER.info("*SimpleIpValpointCb: "+str);
    }

}

