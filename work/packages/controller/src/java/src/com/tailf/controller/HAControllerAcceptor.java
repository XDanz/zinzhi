package com.tailf.controller;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class HAControllerAcceptor {
    private static final Logger log = 
        Logger.getLogger ( HAControllerAcceptor.class );

    private ServerSocket server = null;
    private ExecutorService pool = null;
    private int port;

    public static void main ( String arg[] ) {
        log.info(" starting acceptor..");
        new HAControllerAcceptor(4545);

    }

    class RequestHandler implements Runnable {
        private final Socket clientSock;
        // private static final Logger log = 
        //     Logger.getLogger ( RequestHandler.class );
        RequestHandler ( Socket clientSock ) { this.clientSock = clientSock; }
        
        public void run () {
            try {
                InputStream inStream = clientSock.getInputStream();
                OutputStream outStream = clientSock.getOutputStream();

                
                ObjectInputStream objInStream = 
                    new ObjectInputStream ( inStream );

                Object o = objInStream.readObject();
                log.info ( "Read object :" + o );

            
                ObjectOutputStream objOutStream = 
                    new ObjectOutputStream ( outStream );

                objOutStream.writeObject("ok");
                objOutStream.flush();

                inStream.close();
                outStream.close();
                
            } catch ( Exception e ) {
                log.error("",e );
            } finally {
            }
        }
    }
    
    public HAControllerAcceptor (int port) {
        this.port = port;

        try { 
            this.pool = Executors.newFixedThreadPool ( 5 );
            ServerSocket srvSock = new ServerSocket ();
            srvSock.setReuseAddress(true);
            srvSock.bind( 
                         new InetSocketAddress ( "192.168.60.3", port));

            for ( ;; ) {
                this.pool.execute ( new RequestHandler ( srvSock.accept()) );
            }
                                              
            
            
        } catch (Throwable e) { 
            log.error("",e);

        } finally {
            try {
		server.close();
            } catch (Exception e) {
		// do nothing - server failed
            }
        }
    }

    public ExecutorService executorService() {
        return this.pool;
    }
}