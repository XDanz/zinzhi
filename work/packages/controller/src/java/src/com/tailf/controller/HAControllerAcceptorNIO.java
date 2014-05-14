package com.tailf.controller;

import java.net.InetSocketAddress;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class HAControllerAcceptorNIO {
    private static final Logger log = 
        Logger.getLogger ( HAControllerAcceptorNIO.class );

    private Selector selector = null;
    private ServerSocketChannel server = null;
    private int BUFSIZE = 20 * 1024 * 1024;
    private int port;
    
    public HAControllerAcceptorNIO (int port) {
        this.port = port;

        try { 
            selector = Selector.open(); 
            server = ServerSocketChannel.open(); 
            server.socket().bind(new InetSocketAddress(port)); 
            server.configureBlocking(false); 
            server.register(selector, SelectionKey.OP_ACCEPT);
            
            while (true) {
                selector.select();
                for (Iterator<SelectionKey> i = 
                         selector.selectedKeys().iterator(); i.hasNext();) { 
                    SelectionKey key = i.next(); 
                    i.remove(); 
                    
                    if (key.isConnectable()) { 
                        ((SocketChannel)key.channel()).finishConnect(); 
                    } else if (key.isAcceptable()) {
                        // accept connection 
                        SocketChannel client = server.accept();
                        log.info(" Accepted Client ..");
                        client.configureBlocking(false); 
                        client.socket().setTcpNoDelay(true); 
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) { 
                        log.info ( " -- is readable -- ");
                        ByteBuffer buf = ByteBuffer.allocate ( BUFSIZE );
                        SocketChannel channel = 
                            (SocketChannel)key.channel();
                        
                        int read = channel.read(buf);
                        log.info (" read:" + read);
                        log.info ( " buf:" + buf );
                        if ( read == -1 ) {
                            channel.close();
                            continue;
                        }
                        buf.flip();

                        HAControllerResponse request = 
                            new HAControllerResponse ( buf.array());

                        log.info(request.decode().toString());
                        key.interestOps ( SelectionKey.OP_WRITE );
                        
                        // ...read messages...
                    } else if ( key.isWritable() ) {
                        log.info (" -- is writable -- ");
                        log.info ( " take request ");

                        HAControllerRequest response = 
                            new HAControllerRequest("ok");
                        
                        ByteBuffer buf = ByteBuffer.allocate ( BUFSIZE );

                        SocketChannel channel = 
                            (SocketChannel)key.channel();
                        
                        log.info (buf);
                        buf.put( response.encode() );
                        buf.flip();                        
                        log.info (buf);
                        
                        int wrote = channel.write(buf);
                        log.info (" wrote:" + wrote );
                        
                        key.cancel();
                        // channel.close();
                        // key.interestOps( SelectionKey.OP_ACCEPT);
                    }
		}
            } // while       
        } catch (Throwable e) { 
            log.error("",e);

        } finally {
            try {
		selector.close();
		server.socket().close();
		server.close();

            } catch (Exception e) {
		// do nothing - server failed
            }
        }
    }
}
