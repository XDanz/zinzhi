package com.tailf.test.maapi1;

import com.tailf.maapi.MaapiXPathEvalResult;
import com.tailf.conf.ConfObject;
import com.tailf.maapi.XPathNodeIterateResultFlag;

import com.tailf.conf.ConfValue;

public class MyXPathResult2
    implements MaapiXPathEvalResult{


    public XPathNodeIterateResultFlag result(ConfObject[] kp,
                                      ConfValue value,
                                      java.lang.Object state){


        return XPathNodeIterateResultFlag.ITER_STOP;

    }




}