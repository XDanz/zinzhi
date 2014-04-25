package com.tailf.test.maapi1;

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.*;
import com.tailf.dp.proto.*;
import com.tailf.maapi.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Arrays;

import com.tailf.test.maapi1.namespaces.*;
import org.apache.log4j.Logger;



/**
 * This is a simple validation point callback to
 * check the Port number of a Server
 *
 *
 */
public class ValidationPointCb {
    private static Logger log =
        Logger.getLogger(ValidationPointCb.class);
    Maapi maapi;

    public ValidationPointCb(Maapi maapi) {
        this.maapi= maapi;
    }


    /**
     * The  validate()  callback  should validate the values and
     * throw a DpCallbackException if the validation fails.
     *
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
    @ValidateCallback(callPoint = mtest.validate_validateint,
                      callType = { ValidateCBType.VALIDATE })
        public void validate(DpTrans trans,
                             ConfObject [] kp,
                             ConfValue newval)
        throws DpCallbackException {

        log.info("kp:" + Arrays.toString(kp));
        log.info("newvalue:" + newval);
        /*
          ConfKey serverName =  (ConfKey) kp[1];
          int th = trans.thandle;
          ConfKey next;
          ConfKey this_key= (ConfKey) kp[1];
          ConfUInt16 pVal = (ConfUInt16)newval;
          if (pVal.intValue() < 1025 || pVal.intValue() > 2048)
          throw new DpCallbackException("Port value not in range");
          try {
          MaapiCursor c=  maapi.newCursor(th, "/servers/server");
          next = maapi.getNext(c);
          while (next != null) {
          if ( ! next.equals(this_key)) {
          // dont compare to our own value
          ConfValue port=
          maapi.getElem(
          th, "/servers/server{%x}/port",next);
          if (port.equals(newval))
          throw new DpCallbackException("port number: "+
          newval+
          " is already in use");
          }
          next = maapi.getNext(c);
          }
          } catch (Exception e) {
          e.printStackTrace();
          throw new DpCallbackException("failed to validate: "+
          e.getMessage());
          }
          }*/
    }
}
