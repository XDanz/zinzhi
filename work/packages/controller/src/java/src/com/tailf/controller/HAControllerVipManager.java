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

import org.apache.log4j.Logger;

public class HAControllerVipManager {
    private static final Logger log = 
        Logger.getLogger ( HAControllerVipManager.class);
    static HAControllerVipManager inst;

    Map<HAControllerVip,Future> vipsup =
        new HashMap<HAControllerVip,Future> ();

    private ExecutorService pool = 
        Executors.newCachedThreadPool();
    
    public static HAControllerVipManager getManager() {
        if ( inst == null ) {
            inst = new HAControllerVipManager();
        }
        return inst;
    }

    public void initializeAvailableVips () throws Exception {
        List<InetAddress> availVips =
            HAController.getController().getVips();

        HALocalNode localNode =
            (HALocalNode)HAController.getController().getLocalHANode();
        int index = 0;

        if ( localNode.getNetworkInterface () != null ) {
            for ( InetAddress addr : availVips ) {
                final HAControllerVip vip =
                    new HAControllerVip ( addr ,
                                          localNode.getNetworkInterface() ,
                                          index++);

                vip.bringInterfaceUp();
                Future<Void> future = 
                    pool.submit( 
                                new Callable<Void>() {
                               
                                public Void call () {
                                    HAControllerAutoIfDown ifDown = null;
                                try {
                                    ifDown = 
                                        new HAControllerAutoIfDown (
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

                vipsup.put ( vip , future );                
                

                HAControllerGratuitousArp garpProvider =
                    new HAControllerGratuitousArp ( vip );

                garpProvider.arpReply ( 4 );
                garpProvider.arpResponse ( 4 );

            }
        }
    }

    public void destroyVips () throws HAControllerException {
        for (Map.Entry<HAControllerVip,Future> e : vipsup.entrySet() ) {
            Future future = e.getValue();
            future.cancel (true);
        }
        vipsup.clear();
    }
}