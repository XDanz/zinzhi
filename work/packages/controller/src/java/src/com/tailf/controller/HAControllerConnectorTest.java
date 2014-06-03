package com.tailf.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

public class HAControllerConnectorTest {
    private static final Logger log = 
        Logger.getLogger ( HAControllerConnectorTest.class );

    private Socket socket = null;
    public static void main ( String[] arg ) {
        try {
        String addr = arg[0];
        int port = Integer.parseInt(arg[1]);

        log.info("addr:" + addr);
        log.info("port:" + port);
        HAControllerConnector test = 
            HAControllerConnector
            .connector(
                       new Socket ( 
                                   InetAddress.getByName(addr),port));
        

        test.send("status");
        
        log.info("recv:" +  test.recv());
        } catch ( Exception e ) {
            log.error("",e);
            System.exit(1);
        }

    }
            
    public  HAControllerConnectorTest (InetAddress addr, int port) 
        throws Exception {
        log.info (" Socket =>");
        this.socket = new Socket ( addr , port );
        log.info (" Socket => ok");
    }

    public Object recv () throws Exception {
        ObjectInputStream in = 
            new ObjectInputStream ( this.socket.getInputStream());
        Object o = in.readObject();
        return o;
    }

    public void send ( Object req ) throws Exception {
        ObjectOutputStream out = 
            new ObjectOutputStream( this.socket.getOutputStream() );
        out.writeObject ( req );
        out.flush();
    }
    
    public void close() throws Exception {
        this.socket.close();
    }
}
