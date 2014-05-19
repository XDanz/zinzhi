package com.tailf.controller;

import java.net.InetAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.io.IOException;
import java.io.File;

import org.apache.log4j.Logger;

public class HAControllerVipManager {
    private static final Logger log = 
        Logger.getLogger ( HAControllerVipManager.class);
    static HAControllerVipManager inst;

    Map<HAControllerVip,List<Future>> vipsup =
        new HashMap<HAControllerVip,List<Future>> ();

    private ExecutorService pool = 
        Executors.newCachedThreadPool();
    
    public static HAControllerVipManager getManager() {
        if ( inst == null ) {
            inst = new HAControllerVipManager();
        }
        return inst;
    }

    public void initializeAvailableVips () throws Exception {
        log.info ("initializeAvailableVips () =>");
        List<InetAddress> availVips =
            HAController.getController().getVips();

        HALocalNode localNode =
            (HALocalNode)HAController.getController().getLocalHANode();

        final File path = 
            HAController.getController().getPackageDirectory();
        int index = 0;
        log.info (" localNode If:" + localNode.getNetworkInterface() );
        if ( localNode.getNetworkInterface () != null ) {
            for ( InetAddress addr : availVips ) {
                final HAControllerVip vip =
                    new HAControllerVip ( addr ,
                                          localNode.getNetworkInterface() ,
                                          index++);
                log.info (" Initialize Vip " + vip.getInetAddress() + 
                          " on " + vip.getNetworkInterface() );
                vip.bringInterfaceUp();

                List<Future> futures = new ArrayList<Future>();
                Future<Void> future = 
                    pool.submit( 
                                new Callable<Void>() {
                               
                                public Void call () {
                                    HAControllerAutoIfDown ifDown = null;
                                try {
                                    ifDown = 
                                        new HAControllerAutoIfDown (
                                                                    path,
                                              vip.getInetAddress(),
                                              vip.getNetworkInterface(),
                                              vip.getVipIndex() );
                                   
                                    
                                } catch ( Exception e ) {
                                    log.error("",e);
                                    try {
                                        ifDown.process
                                            .getOutputStream().close();
                                    } catch ( IOException ie ) {
                                        
                                    }
                                }
                                    return null;
                                }
                                
                                });
                futures.add( future );


                
                Future<Void> garpFuture = 
                    pool.submit(
                               new Callable<Void>() {


                                   public Void call () {
                                       
                                       try {
                                           log.info (" a -> ");
                                       
                                       HAControllerGratuitousArp garpProvider =
                                       new HAControllerGratuitousArp ( vip );
                                       
                                       garpProvider.arpReply ( 4 );
                                       garpProvider.arpResponse ( 4 );
                                   
                                       log.info (" GARPING -> OK");
                                       log.info (" Initialize Vip " + 
                                                 vip.getInetAddress() + 
                                                 " on " + 
                                                 vip.getNetworkInterface()  + 
                                                 " DONE!");   
                                       } catch ( Exception e ) {
                                           log.error("",e);
                                       }
                                       return null;
                                   } 
                               }
                               );
                futures.add ( garpFuture );
                log.info (" putting " + futures );
                vipsup.put ( vip , futures );                

            }
            log.info (" All VIP up and running!");
        }
        log.info ("initializeAvailableVips () => ok");
    }


       

    public void destroyVips () throws HAControllerException {
        log.info (  " destroying vips ");
        for (Map.Entry<HAControllerVip,List<Future>> e : vipsup.entrySet() ) {
            List<Future> futures = e.getValue();
            log.info (" interrupting ..");
            for ( Future future : futures ) 
                future.cancel (true);
        }
        vipsup.clear();
    }

    public void destroy () {
        pool.shutdownNow();
    }
}
