package com.tailf.test.cdb3;

import java.net.Socket;
import java.net.InetAddress;
import java.util.List;

import com.tailf.cdb.*;
import com.tailf.conf.*;
import com.tailf.maapi.*;
import com.tailf.navu.*;

import com.tailf.test.cdb3.namespaces.*;
import org.apache.log4j.Logger;

public class TestNavuElementBlankSpaceKey {           
    private static final Logger log = 
        Logger.getLogger (  TestNavuElementBlankSpaceKey.class );
    public static void main (String [] arg ) {
        try {
            Socket socket = new Socket("127.0.0.1",
                                       Conf.PORT);
            Maapi maapi = new Maapi(socket);
            
            maapi.startUserSession("admin",
                                   InetAddress.getLocalHost(),
                                   "maapi",
                                   new String[] {"oper"},
                                   MaapiUserSessionFlag.PROTO_TCP);
            
            int th = maapi.startTrans(Conf.DB_RUNNING,
                                      Conf.MODE_READ_WRITE);

            NavuContainer root =
                new NavuContainer (
                                   new NavuContext(maapi, th) 
                                   );

            NavuList bklist = 
                root.container ( "http://tail-f.com/ns/example/bulk/1.0")
                .container("special").list("bklist");
            int size = bklist.elements().size();


        
            socket.close();
        } catch ( Exception e ) {
            log.info("",e);
        }
    }
}

