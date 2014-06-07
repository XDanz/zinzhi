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

    Map<HAControllerVip, List<Future<Void>> >  vipsup =
        new HashMap<HAControllerVip, List<Future<Void>>> ();

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
            HAController.getController().getHAConfiguration()
            .getPackageDirectory();
        int index = 0;
        

        // if localNode does not have a network interface.
        if ( localNode.getNetworkInterface () != null ) {
            // initiallize garp for all vip ips
            for ( InetAddress addr : availVips ) {
                final HAControllerVip vip =
                    new HAControllerVip ( addr ,
                                          localNode.getNetworkInterface() ,
                                          index++);
                log.info (" Initialize Vip " + vip.getInetAddress() + 
                          " on " + vip.getNetworkInterface() );
                // throws if the operation could not be performed
                vip.bringInterfaceUp();
                
                List<Future<Void>> futures = new ArrayList<Future<Void>>();
                
                // arpsend
                Future<Void> future = 
                    pool.submit( createIfDownCallable( vip , path));
                
                futures.add( future );
                
                Future<Void> garpFuture = 
                    pool.submit( createGArpCallable(vip) );
                
                futures.add ( garpFuture );
                log.info (" putting " + futures );
                vipsup.put ( vip , futures );     
            }
            log.info (" All VIP up and running!");
        } else {
            log.info(" NCS_HA_NODE is set!");
        }
        log.info ("initializeAvailableVips () => ok");
    }

    /**
     * Create the Callable for this bring up the alias for 
     * the VIP. This is used for launching the shell script
     * which blocks on read.
     *
     * @param vip The VIP alias interface to bring up
     * @param path The package path for the package which is used
     *  for invoking the AutoIfDown.sh.
     */
    public Callable<Void> createIfDownCallable(final HAControllerVip vip,
                                               final File path) {
        return new Callable<Void>() {
            public Void call () {
                HAControllerAutoIfDown ifDown = null;
                try {
                    ifDown = 
                        new HAControllerAutoIfDown (
                                                    path,
                                                    vip.getInetAddress(),
                                                    vip.getNetworkInterface(),
                                                    vip.getVipIndex() );
                    
                    return null;
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
        };
    }
    
    public Callable<Void> createGArpCallable(final HAControllerVip vip) {
        return new Callable<Void>() {
            public Void call () {
                log.info ("garp() =>");
                HAControllerGratuitousArp garpProvider =
                    new HAControllerGratuitousArp ( vip );
                try {
                    // send arpReply 4 times
                    garpProvider.arpReply ( 4 );
                    
                    // send arpRespone 4 times
                    garpProvider.arpResponse ( 4 );

                    log.info (" Initialize Vip " + 
                              vip.getInetAddress() + 
                              " on " + 
                              vip.getNetworkInterface()  + 
                              " ok!");   
                    
                    log.info ("garp() => ok");
                    
                } catch ( HAControllerException e ) {
                    log.error("",e );
                }
                return null;
            }
        };
    }

    public void destroyVips () throws HAControllerException {
        log.info (  " destroying vips ");
        for (Map.Entry<HAControllerVip,List<Future<Void>>> e :
                 vipsup.entrySet() ) {
            List<Future<Void>> futures = e.getValue();
            log.info (" interrupting ..");
            for ( Future<Void> future : futures ) 
                future.cancel (true);
        }
        vipsup.clear();
    }
    
    public void destroy () {
        pool.shutdownNow();
    }
}
