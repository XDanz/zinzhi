package com.tailf.controller;

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
                                log.info ( " Not reachable !");
                                Thread thread = Thread.currentThread();
                                try {
                                    thread.sleep (3000);
                                } catch ( InterruptedException ee ) {
                                    log.error("",ee);
                                }
                            }

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
