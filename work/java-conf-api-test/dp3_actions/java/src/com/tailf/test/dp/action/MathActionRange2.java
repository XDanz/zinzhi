package com.tailf.test.dp.action;


import com.tailf.conf.*;
import com.tailf.dp.DpActionTrans;
import com.tailf.dp.DpCallbackException;
import com.tailf.dp.annotations.ActionCallback;
import com.tailf.dp.proto.ActionCBType;

import com.tailf.test.dp.action.namespaces.*;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Arrays;

import com.tailf.util.ConfXMLParamToXML;
import org.w3c.dom.Document;



public class MathActionRange2 {
    private static Logger LOGGER =
        Logger.getLogger(MathActionRange2.class);

    @ActionCallback(callPoint = cs.actionpoint_math_cs,
                    callType = ActionCBType.INIT)
        public void init(DpActionTrans trans) throws DpCallbackException {
        LOGGER.info("==> init");
        LOGGER.info("<== init");
    }


    @ActionCallback(callPoint = cs.actionpoint_math_cs,
                    callType = ActionCBType.ACTION)
        public ConfXMLParam[] action(DpActionTrans trans,
                                     ConfTag name,
                                     ConfObject[] kp,
                                     ConfXMLParam[] params)
        throws DpCallbackException {

        LOGGER.info("==> mathAction RANGE 2 INVOKED*****");

        ArrayList<ConfNamespace> ns_list = trans.getNsList();
        LOGGER.info("confTag:" + name);
        LOGGER.info("kp:" + Arrays.toString(kp));
        LOGGER.info("params:" + Arrays.toString(params));

        ConfXMLParam[] result = null;
        int sum = 0;

        for (ConfXMLParam p : params) {
            LOGGER.info("p.val:" + p.val);

            if(p.val instanceof ConfList){
                ConfList list = (ConfList)p.val;

                ConfObject[] obj = list.elements();

                for(int j = 0; j < obj.length; j++){
                    ConfObject val = obj[j];

                    if ((val != null) &&  (val instanceof ConfInt32)) {
                        ConfInt32 v = (ConfInt32)val;
                        sum += v.intValue();
                    }
                }
            }
        }

        LOGGER.info("sum " + sum);

        if (sum != 0) {
            result = new ConfXMLParam[]{
                new ConfXMLParamValue(new mathrpc().hash(),
                                      mathrpc._result,
                                      new ConfInt32(sum),
                                      ns_list)
            };
        }
        LOGGER.info("RES:" + Arrays.toString(result));

        LOGGER.info("<== mathAction");
        return result;
    }

}