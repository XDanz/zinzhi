package com.tailf.controller.actions;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.tailf.conf.ConfEnumeration;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfXMLParamValue;
import com.tailf.controller.HAControllerVipManager;
import com.tailf.dp.DpActionTrans;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.annotations.ActionCallback;
import com.tailf.dp.proto.ActionCBType;

public class VipController {
    private static final Logger log =
        Logger.getLogger( VipController.class );

    @ActionCallback(callPoint="vipctrl",
                    callType=ActionCBType.INIT)
        public void init ( DpActionTrans trans )
        throws DpCallbackException {
        log.info (" init (th=" + trans.getTransaction() + ") => ok");
    }

    @ActionCallback(callPoint="vipctrl",
                    callType=ActionCBType.ACTION)
    public ConfXMLParam[] action( DpActionTrans trans,
                                  ConfTag name,
                                  ConfObject[] kp,
                                 ConfXMLParam[] params )
        throws DpCallbackException {
        log.info (" action (thandle=" + trans.getTransaction() +
                  ","  + name + "," +
                  new ConfPath ( kp )  + ") =>");

        log.info ("\n" +
                  Arrays.toString ( params ) );

        ConfXMLParamValue param = (ConfXMLParamValue)params[0];

        ConfEnumeration vipAction = (ConfEnumeration)param.getValue();

        try {
            String path = "/thc:ha-controller/vip/controll";
            if ( vipAction
                 .equals ( ConfEnumeration
                           .getEnumByLabel (
                                            new ConfPath (path ) ,
                                            "up")) ) {

                HAControllerVipManager.getManager().initializeAvailableVips();

            } else if ( vipAction
                        .equals ( ConfEnumeration
                                  .getEnumByLabel (
                                                   new ConfPath ( path ) ,
                                                   "down")) ) {
                HAControllerVipManager.getManager().destroyVips();
            }

        } catch ( ConfException e ) {
            log.error("",e );
        } catch ( Exception e ) {
            log.error("", e);
        }
        return null;
    }
}
