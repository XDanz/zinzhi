package com.tailf.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

public class HAControllerSender {
    private static final Logger log = 
        Logger.getLogger ( HAControllerSender.class );

    private Socket socket = null;
    
    public static void main ( String arg[] ) {
        log.info(" starting send..");
        
        try {

           String strAddr = arg[0];
           int port = Integer.parseInt(arg[1]);
           InetAddress addr = 
               InetAddress.getByName(strAddr);

            HAControllerConnector sender = 
                HAControllerConnector.connector(new Socket ( addr, port));
            
            log.info (" writing request ..");
            sender.send("status");
            log.info (" writing request ..ok");

            Object response = 
                sender.recv();

            log.info ( " got response :" + response);
        } catch ( Exception e ) {
            log.error("",e);
            System.exit(1);
        }

    }
    

    public HAControllerSender (InetAddress addr, int port) throws Exception {
        this.socket = new Socket ( addr , port );
    }


    public Object readResponse () throws Exception {
        ObjectInputStream in = 
            new ObjectInputStream ( this.socket.getInputStream());
        Object o = in.readObject();
        in.close();
        return o;
        
    }


    public void sendRequest ( Object req ) throws Exception {
        
        ObjectOutputStream out = 
            new ObjectOutputStream( this.socket.getOutputStream() );
        out.flush();
        out.writeObject ( req );
        // out.close();
    }
}
