package com.tailf.test.maapi1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.List;

import com.tailf.dp.Dp;

import com.tailf.conf.*;
import com.tailf.proto.*;
import com.tailf.maapi.*;
import com.tailf.test.maapi1.namespaces.*;
import org.apache.log4j.Logger;

public class TestQuery{

    private static Logger LOGGER = Logger.getLogger(TestQuery.class);
    public static final int port = 4565;


    public static void main(String[] arg) throws Exception{
        new TestQuery().test();
    }

    // public boolean skip() { return true; }
    public void test() throws Exception {
        Socket s =
            new Socket("127.0.0.1", port);
        Maapi maapi = new Maapi(s);

        maapi.loadSchemas();

        maapi.startUserSession("jb",
                               InetAddress.getLocalHost(),
                               "maapi",
                               new String[] {"oper"},
                               MaapiUserSessionFlag.PROTO_TCP);

        int th =  maapi.startTrans(Conf.DB_RUNNING,
                                    Conf.MODE_READ_WRITE);


        //Alt 1. CONFD_QUERY_STRING
        // Class<ResultTypeString> cls = ResultTypeString.class;
        // LOGGER.info("cls:" + cls);
        //Class<?> cls = QueryResult.getResultTypeString();

        QueryResult<ResultTypeString> qR1 =
            maapi.<ResultTypeString>queryStart(th,
                                     "/sys/host[enabled='true']",
                                  null,
                                               3,1,
                                  Arrays.asList("name","number","enabled"),
                                 ResultTypeString.class);

        for(QueryResult.Entry entry : qR1){
            List<ResultTypeString> rsValue = entry.value();
            for(ResultTypeString typ: rsValue){
                String str = typ.stringValue();
                LOGGER.info("string:" + str);

                // ConfValue str = typ.confValue();
                // ConfObject[] path = typ.keyPath();
            }
        }

        // // Alt 2.     CONFD_QUERY_HKEYPATH
        // QueryResult<ResultTypeKeyPath> qR2 =
        //     maapi.<ResultTypeKeyPath>queryStart(th,
        //                                   "/sys/host[enabled='true']",
        //                                         null,
        //                                  3,1,
        //                           Arrays.asList("name","number","enabled"));


        // for(QueryResult.Entry entry : qR2){
        //     List<ResultTypeKeyPath> rsValue = entry.value();
        //     for(ResultTypeKeyPath typ: rsValue){
        //         ConfObject[] v0 = typ.keyPath();
        //     }
        // }

        // //Alt 3.  CONFD_QUERY_HKEYPATH_VALUE
        // QueryResult<ResultTypeKeyPathValue> qR3=
        //     maapi.<ResultTypeKeyPathValue>queryStart(th,
        //                              "/sys/host[enabled='true']",
        //                           null,
        //                           3,1,
        //                        Arrays.asList("name","number","enabled"));


        // for(QueryResult.Entry entry : qR3){
        //     List<ResultTypeKeyPathValue> rsValue = entry.value();
        //     for(ResultTypeKeyPathValue typ: rsValue){
        //         ConfObject[] v0 = typ.keyPath();
        //         ConfValue v1 = typ.confValue();
        //     }
        // }



        // //Alt 4.  CONFD_QUERY_TAG_VALUE
        // QueryResult<ResultTypeTag> qR4 =
        //     maapi.<ResultTypeTag>queryStart(th,
        //                        "/sys/host[enabled='true']",
        //                              null,
        //                                 3,1,
        //                          Arrays.asList("name","number","enabled"));


        // for(QueryResult.Entry entry : qR4){
        //     List<ResultTypeTag> rsValue = entry.value();
        //     for(ResultTypeTag typ: rsValue){
        //         ConfXMLParam v0 = typ.tag();
        //     }
        // }


        s.close();
    }

}
