package com.tailf.controller;

import java.net.Socket;
import java.net.InetAddress;
import java.util.EnumSet;
import java.io.IOException;
import java.io.File;

import com.tailf.ncs.ApplicationComponent;
import com.tailf.ncs.NcsMain;
import com.tailf.ncs.annotations.Scope;

import com.tailf.ncs.annotations.Resource;
import com.tailf.ncs.annotations.ResourceType;


import com.tailf.notif.Notif;
import com.tailf.notif.HaNotification;
import com.tailf.notif.NotificationType;

import com.tailf.maapi.Maapi;
import com.tailf.maapi.MaapiUserSessionFlag;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfPath;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfValue;

import org.apache.log4j.Logger;

public class HAControllerAppCmp implements ApplicationComponent {

    private static final Logger log =
        Logger.getLogger ( HAControllerAppCmp.class );

    
    @Resource(type=ResourceType.MAAPI,qualifier="ctrl-m-read",
              scope=Scope.INSTANCE)
    private Maapi maapiSock;   // Read Maapi Socket to determine cur pkgs dir.
    private HAController haController;
    private boolean shouldRun = true;
    private Notif notif = null;
    private Socket notifSocket = null;
    
    public void init () throws Exception {
        log.info("\n ----- haController initializing  ----- ");
        haController = HAController.getController();

        NcsMain ncsMain = NcsMain.getInstance();
        notifSocket = new Socket (ncsMain.getNcsHost(),
                                  ncsMain.getNcsPort());
        notif =
            new Notif ( notifSocket ,
                        EnumSet.of(NotificationType.NOTIF_HA_INFO) );

        File currPkgDir = getCurrentPackageDirectory();
        log.info (" currPkgDir:" + currPkgDir );
        haController.addPackageDirectory ( currPkgDir );

        log.info ( " CALL initalDetermination () =>");
        haController.initialDetermination ();
        log.info ( " CALL initalDetermination () => ok");
        
    }

    public void run () {
        log.info ("\n ---- Running ---- START");
        try {

            while ( shouldRun ) {

                HaNotification notification =
                    (HaNotification)notif.read();

                haController.haEvent ( notification );

            }
        } catch ( ConfException e ) {
            log.warn (" Notif Socket broken!");
            if ( Thread.currentThread().isInterrupted() ) {
                log.info ( Thread.currentThread() + " interrupted!");
            }
        } catch ( Exception e ) {
            log.error("", e );
        } finally {
            try {
                notifSocket.close();
            } catch ( IOException e ) { }
        }
        log.info ("\n ---- Running ---- END");
    }

    public void finish() throws Exception {
        log.info ("\n ---- Finish ---- START");
        HAControllerAcceptor.stopListening();
        shouldRun = false;
        log.info ("\n ---- Finish ---- END");
    }

    File getCurrentPackageDirectory() {

        try {
            maapiSock.startUserSession ("admin",
                                        InetAddress
                                        .getByName(NcsMain.getInstance()
                                                   .getNcsHost()),
                                        "maapi",
                                        new String[] {"admin"},
                                        MaapiUserSessionFlag.PROTO_TCP);
            ConfPath path = 
                new ConfPath("/ncs:packages/package{controller}/directory" );
            int th = maapiSock.startTrans ( Conf.DB_RUNNING,
                                            Conf.MODE_READ);
            ConfValue pkgPath = maapiSock.getElem( th, path ) ;
            log.info ( " pkgPath :" + pkgPath );
            return new File(pkgPath.toString());
        } catch ( Exception e ) {
            log.error("",e);
            return null;
        }
    }
}
