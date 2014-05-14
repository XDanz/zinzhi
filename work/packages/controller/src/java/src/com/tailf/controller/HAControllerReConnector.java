package com.tailf.controller;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.net.InetAddress;
import com.tailf.cdb.CdbTxId;

import org.apache.log4j.Logger;

public class HAControllerReConnector {
    private static final Logger log =
        Logger.getLogger ( HAControllerReConnector.class );

    private static final ExecutorService pool =
        Executors.newCachedThreadPool();

    public static  void execute () {
        pool.submit ( new Runnable () {

                public void run() {
                    log.info (" XXXXXX RECONNECTOR RUNNING!! XXXXXX ");
                    HANode remoteHaNode = null;
                    HAController controller =  null;
                    try {

                        controller = HAController.getController();

                        remoteHaNode =
                            controller.getRemoteHANode();
                    } catch ( Exception e ) {
                        log.error("",e );
                    }
                    log.info( " interrupted:" +
                              Thread.currentThread().isInterrupted() );
                    while ( !Thread.currentThread().isInterrupted() ) {
                        try {

                            if ( remoteHaNode.isReachable () ) {
                                controller.reConnected ();
                                Thread.currentThread().interrupt();
                            } else {
                                Thread thread = Thread.currentThread();
                                try {
                                    thread.sleep (3000);
                                } catch ( InterruptedException ee ) {
                                    log.error("",ee);
                                }
                            }
                        } catch ( EOFException e ) {
                            // other node died while reading
                            // from the stream
                            log.warn ("EOF while reConnected keep running!");
                        } catch ( SocketException e ) {
                            // Connection reset
                            log.warn ("Connection reset by peer keep running!");
                        } catch ( SocketTimeoutException e) {
                            // read timed out
                            log.warn ( "Read timed out on socket!");
                        } catch ( Exception e ) {
                            log.error("", e);
                            Thread thread = Thread.currentThread();
                            try {
                                thread.sleep (1000);
                            } catch ( InterruptedException ee ) {
                                log.error("",ee);
                            }
                        }
                    } // while
                }
            });
    }


    public ExecutorService executor () {
        return this.pool;
    }

}
