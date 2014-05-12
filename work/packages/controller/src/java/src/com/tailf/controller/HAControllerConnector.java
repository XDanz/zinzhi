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

public class HAControllerConnector {
    private static final Logger log = 
        Logger.getLogger ( HAControllerConnector.class );

    private Socket socket = null;
    private int port;

    static HAControllerConnector connector ( ) throws Exception {
        HAController controller = 
            HAController.getController();

        HANode haNode = controller.getRemoteHANode();
        HAControllerConnector c = 
            new HAControllerConnector ( haNode
                                        .getAddress().getAddress(),
                                        haNode.getPort());
        
        return c;
    }

    static HAControllerConnector connector ( Socket sock ) throws Exception {
        return new HAControllerConnector ( sock );
    }

            
    private HAControllerConnector (InetAddress addr, int port) 
        throws Exception {
        this.socket = new Socket();
        this.socket.setSoTimeout(5000);
        this.socket.connect( new InetSocketAddress(addr,port) , 5000 );
    }

    private HAControllerConnector ( Socket sock ) throws Exception {
        this.socket = sock;
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
