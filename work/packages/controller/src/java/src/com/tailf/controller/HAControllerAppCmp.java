package com.tailf.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.EnumSet;

import org.apache.log4j.Logger;

import com.tailf.conf.ConfException;
import com.tailf.ncs.ApplicationComponent;
import com.tailf.ncs.NcsMain;
import com.tailf.notif.HaNotification;
import com.tailf.notif.Notif;
import com.tailf.notif.NotificationType;

public class HAControllerAppCmp implements ApplicationComponent {

    private static final Logger log =
        Logger.getLogger ( HAControllerAppCmp.class );
    
    private HAController haController;
    private volatile boolean shouldRun = true;
    private Notif notif = null;
    private Socket notifSocket = null;

    // The invoking thread has the monitor lock on this object
    public void init () throws Exception {
        log.info("\n ----- HAController initializing  ----- ");
        try {
            Thread.currentThread().dumpStack();
            haController = HAController.getController();
            notif = createNotifSocket ();
            log.info ( " CALL initalDetermination () =>");
            haController.initialDetermination ();
             log.info ( " CALL initalDetermination () => ok");
        } catch ( HAControllerException e ) {
            log.error("", e );
            shouldRun = false;
            log.info("shouldRun:" + shouldRun );
            log.warn ( " HAController NOT Running! ");
        }
        log.info("\n ----- HAController initializing  END ----- ");        
    }

    public void run () {

        try {
            log.info("shouldRun:" + shouldRun );
            while ( shouldRun ) {
                log.info ("\n ---- Running ---- START");

                HaNotification notification =
                    (HaNotification)notif.read();

                haController.haEvent ( notification );

            }
        } catch ( ConfException e ) {
            log.warn (" Notif Socket broken!");
            log.error("",e );
            if ( Thread.currentThread().isInterrupted() ) {
                log.info ( Thread.currentThread() + " interrupted!");
            }
        } catch ( Exception e ) {
            log.error("", e );
        } finally {
            try {
                if ( notifSocket != null ) 
                    notifSocket.close();
            } catch ( IOException e ) { }
        }
        log.info ( " run method ended! "  );
    }

    public void finish() throws Exception {
        log.info ("\n ---- Finish ---- START");
        HAControllerAcceptor.stopListening();
        shouldRun = false;
        HAControllerVipManager vipMngr = 
            HAControllerVipManager.getManager();
        log.info ( " destroy All vips ");
        vipMngr.destroyVips();
        vipMngr.destroy();
        log.info ( " destroy All vips ok");

        
        log.info ("\n ---- Finish ---- END");
    }

    Notif createNotifSocket ( ) throws Exception {
        NcsMain ncsMain = NcsMain.getInstance();
        notifSocket = new Socket (ncsMain.getNcsHost(),
                                  ncsMain.getNcsPort());
        notif =
            new Notif ( notifSocket ,
                        EnumSet.of(NotificationType.NOTIF_HA_INFO) );
        
        return notif;
    }
}
