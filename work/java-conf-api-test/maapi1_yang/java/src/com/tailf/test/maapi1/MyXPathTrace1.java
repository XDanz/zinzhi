package com.tailf.test.maapi1;

import com.tailf.maapi.MaapiXPathEvalTrace;
import com.tailf.maapi.XPathNodeIterateResultFlag;

public class MyXPathTrace1
    implements MaapiXPathEvalTrace{

    public XPathNodeIterateResultFlag trace(String tracestr){

        return XPathNodeIterateResultFlag.ITER_CONTINUE;
    }


}