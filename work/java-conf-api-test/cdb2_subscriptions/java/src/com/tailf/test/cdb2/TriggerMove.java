package com.tailf.test.cdb2;

import java.net.Socket;
import java.net.InetAddress;

import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;

import com.tailf.conf.ConfUInt16;
import com.tailf.conf.Conf;


public class TriggerMove {
    //b = maapi.exists(th, "/mtest/indexes/index{4}");
    // if (b == true)
    //     failed("/mtest/indexes/index{3} should not exist");

    public static void
        main ( String [] arg )
    {

        try {
        Maapi maapi = new Maapi( new Socket("127.0.0.1", Conf.PORT));

        maapi.startUserSession("admin",
                               InetAddress.getLocalHost(),
                               "maapi", new String[] {"admin"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int th = maapi.startTrans(Conf.DB_RUNNING,
                              Conf.MODE_READ_WRITE);





        //    maapi.move(th, new ConfKey(new ConfObject[] {
    //                 new ConfUInt16(4)}),
    //         "/zb:zconfigzub/buzz-interfaces/buzz/servers/server{www}
        ///foo/mtest/indexes/index{2}");

    //     b = maapi.exists(th, "/mtest/indexes/index{4}");
    //     if (b == false)
    //          failed("/mtest/indexes/index{4} should now exist");

    //      maapi.move(th, "5", "/mtest/indexes/index{4}");

    //      b = maapi.exists(th, "/mtest/indexes/index{4}");
    //      if (b == true)
    //          failed("/mtest/indexes/index{4} should not exist");

    //      b = maapi.exists(th, "/mtest/indexes/index{5}");
    //      if (b == fa≤lse)
    //          failed("/mtest/indexes/index{5} should now exist");

        maapi.finishTrans(th);
        } catch ( Exception e ) {
            System.exit( 1 );
        }
    // close2();
    }
}