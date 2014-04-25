package com.tailf.test.dp1;

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.AuthCallback;
import com.tailf.dp.proto.AuthCBType;

import org.apache.log4j.Logger;
import com.tailf.test.dp1.namespaces.*;

public class SimpleAuthCb {

    private static Logger LOGGER  =
        Logger.getLogger(SimpleAuthCb.class);

    @AuthCallback(callType=AuthCBType.AUTH)
        public boolean auth(DpAuthContext atx) throws DpCallbackException {
        LOGGER.info("Result = "+atx.isSuccess() +
                    ", method="+atx.getMethod() +
                    ", userinfo= "+atx.getUserInfo());

        if (atx.getUserInfo().getUserName().equals("oper")) {
            atx.setError("BAD BAD user %s", atx.getUserInfo().getUserName());
            return false;
        } else if (atx.getUserInfo().getUserName().equals("xxx")) {
            throw new DpCallbackExtendedException(DpCallbackExtendedException
                                                  .ERRCODE_ACCESS_DENIED,
                                                  new smp(),
                                                  "server",
                                                  "VERY BAD user %s",
                                                  atx.getUserInfo().getUserName()
                                                  );
        }
        return true;
    }
}
