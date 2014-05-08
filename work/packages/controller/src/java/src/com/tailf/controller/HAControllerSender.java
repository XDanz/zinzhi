package com.tailf.controller;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.concurrent.ArrayBlockingQueue;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

public class HAControllerSender {
    private static final Logger log = 
        Logger.getLogger ( HAControllerSender.class );

    private Socket socket = null;
    private int port;

    private ArrayBlockingQueue<HAControllerRequest>  requestQueue = 
        new ArrayBlockingQueue<HAControllerRequest>(10);

    private ArrayBlockingQueue<HAControllerResponse>  responseQueue = 
        new ArrayBlockingQueue<HAControllerResponse>(10);

    
    public static void main ( String arg[] ) {
        log.info(" starting send..");

        try {
            InetAddress addr = 
                InetAddress
                .getByAddress (
                               new byte[] { (byte)192, (byte)168, 60, 3 });

            
            
            HAControllerSender sender = 
                new  HAControllerSender ( addr , 4545 ) ;

            
            
            log.info (" writing request ..");
            sender.sendRequest ( new HAControllerRequest ("status"));

            log.info (" writing request ..ok");

            Object response = 
                sender.readResponse();

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
