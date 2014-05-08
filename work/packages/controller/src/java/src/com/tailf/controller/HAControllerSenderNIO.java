package com.tailf.controller;

import java.net.InetSocketAddress;
import java.net.InetAddress;

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

public class HAControllerSenderNIO {
    private static final Logger log = 
        Logger.getLogger ( HAControllerSenderNIO.class );

    private Selector selector = null;
    private SocketChannel sockChannel = null;
    private InetSocketAddress addr = null;
    private int BUFSIZE = 20 * 1024 * 1024;
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

            
            InetSocketAddress inetSockAddr = 
                new InetSocketAddress(addr, 4545);
        
            
             HAControllerSenderNIO sender = new 
                 HAControllerSenderNIO ( inetSockAddr ) ;

            
            
            log.info (" writing request ..");
            sender.sendRequest ( new HAControllerRequest ("status"));
            log.info (" writing request ..ok");

            HAControllerResponse response = 
                sender.getResponse();

            log.info ( " got response :" + response.decode());
        } catch ( Exception e ) {
            log.error("",e);
            System.exit(1);
        }

    }

    
    public HAControllerResponse getResponse () 
        throws InterruptedException {
        return responseQueue.take();
    }

    

    public HAControllerSenderNIO (InetSocketAddress addr) {
        this.addr = addr;
    }


    public void sendRequest ( HAControllerRequest req ) {
        
        try { 
            selector = Selector.open(); 
            sockChannel = SocketChannel.open();

            sockChannel.connect(addr);
            sockChannel.configureBlocking(false); 
            
            sockChannel.register(selector, SelectionKey.OP_WRITE | 
                                 SelectionKey.OP_CONNECT);
                            
            while (true) {

                selector.select();
                
                for (Iterator<SelectionKey> i = 
                         selector.selectedKeys().iterator(); i.hasNext();) { 
                    SelectionKey key = i.next(); 
                    i.remove(); 
                    
                    if (key.isConnectable()) { 
                        ((SocketChannel)key.channel()).finishConnect();
                    } 

                    if ( key.isWritable() ) {
                        log.info (" channel is writable ");
                        log.info ( " take request ");
                        log.info ("  request was " + req );
                        ByteBuffer buf = ByteBuffer.allocate ( BUFSIZE );

                        SocketChannel channel = 
                            (SocketChannel)key.channel();
                        
                        log.info (buf);
                        buf.put( req.encode() );
                        buf.flip();                        
                        log.info (buf);
                        
                        int wrote = channel.write(buf);
                        log.info (" wrote:" + wrote );
                        key.interestOps( SelectionKey.OP_READ );
                    }

                    if (key.isReadable()) {
                        log.info (" -- readable -- ");
                        ByteBuffer buf = ByteBuffer.allocate ( BUFSIZE );
                        SocketChannel channel = 
                            (SocketChannel)key.channel();
                        
                        int read = channel.read(buf);
                        log.info(" read:" + read );

                        if (read == -1 ) {
                            log.info ( " returning ");
                            return;
                        }

                        log.info (" buf:" + buf);
                        buf.flip();
                        log.info (" buf:" + buf);
                        
                        HAControllerResponse response = 
                            new HAControllerResponse ( buf.array() );
                        
                        // Object o = response.decode();
                        responseQueue.offer ( response );
                        // ...read messages...
                        log.info (" -- readable -- done");
                        return;
                    }
		} // iterator.hasNext()
                
            } // while       
        } catch (Throwable e) { 
            log.error("",e );

        } finally {
            try {
		selector.close();
		sockChannel.socket().close();

            } catch (Exception e) {
		// do nothing - server failed
            }
        }
    }
}