
package com.tailf.test.dp1;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.test.dp1.namespaces.smp;
import org.apache.log4j.Logger;
public class Test5{

    private static Logger LOGGER = Logger.getLogger(Test5.class);

    public static void main(String[] arg) throws Exception{

        new Test5().test();

    }
    // public boolean skip() { return true; }
    public void test() throws Exception {
        Socket s = new Socket("localhost", Conf.PORT);
        Maapi maapi = new Maapi(s);
        maapi.addNamespace(new smp());
        maapi.startUserSession("admin", InetAddress.getLocalHost(),
                               "maapi", new String[] {"admin"},
                               MaapiUserSessionFlag.PROTO_TCP);

        MaapiAuthentication mauth =
            maapi.authenticate("xxx", "oper");
        LOGGER.info ("user xxx status valid = " + mauth.valid);
        LOGGER.info ("user xxx reason = " + mauth.reason);
        maapi.endUserSession();
        s.close();
    }

}