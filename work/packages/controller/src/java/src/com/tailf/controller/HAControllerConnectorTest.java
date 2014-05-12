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
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class HAControllerConnectorTest {
    private static final Logger log = 
        Logger.getLogger ( HAControllerConnectorTest.class );

    private Socket socket = null;
    private int port;

    // static HAControllerConnector connector ( ) throws Exception {
    //     log.info("connector() =>");
    //     HAController controller = 
    //         HAController.getController();
    //     log.info( "ctrl:" + controller );
    //     HANode haNode = controller.getRemoteHANode();
    //     log.info( "remote:" + haNode );
    //     log.info( "addr:" + haNode.getAddress() );
    //     log.info( "port:" + haNode.getPort() );
    //     HAControllerConnector c = 
    //         new HAControllerConnector ( haNode
    //                                     .getAddress().getAddress(),
    //                                     haNode.getPort());
        
    //     log.info(" connector() => "  + c );
    //     return c;
    // }

    public static void main ( String[] arg ) {
        try {
        String addr = arg[0];
        int port = Integer.parseInt(arg[1]);

        log.info("addr:" + addr);
        log.info("port:" + port);
        HAControllerConnectorTest test = 
            new HAControllerConnectorTest(InetAddress.getByName(addr),port);
        

        test.send("test");
        
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
