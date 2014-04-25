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


public class MathAction {
    private static Logger LOGGER =
        Logger.getLogger(MathAction.class);

    @ActionCallback(callPoint = mathrpc.actionpoint_math,
                    callType = ActionCBType.INIT)
        public void init(DpActionTrans trans) throws DpCallbackException {
        LOGGER.info("==> init");
        LOGGER.info("<== init");
    }


    @ActionCallback(callPoint = mathrpc.actionpoint_math,
                    callType = ActionCBType.ACTION)
        public ConfXMLParam[] mathAction(DpActionTrans trans,
                                         ConfTag name,
                                         ConfObject[] kp,
                                         ConfXMLParam[] params)
        throws DpCallbackException {
        LOGGER.info("==> mathAction");

        ArrayList<ConfNamespace> ns_list = trans.getNsList();
        LOGGER.info("confTag:" + name);
        LOGGER.info("kp:" + Arrays.toString(kp));
        LOGGER.info("params:" + Arrays.toString(params));

        ConfXMLParam[] result = null;
        int sum = 0;

        for (ConfXMLParam p : params) {
            LOGGER.info("p.val:" + p.getValue());

            if(p.getValue() instanceof ConfList){
                ConfList list = (ConfList)p.getValue();

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
                                      new ConfInt32(sum));

            };
        }
        LOGGER.info("RES:" + Arrays.toString(result));

        LOGGER.info("<== mathAction");
        return result;
    }

}