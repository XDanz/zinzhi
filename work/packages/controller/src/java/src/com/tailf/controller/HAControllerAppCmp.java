package com.tailf.controller;

import java.net.Socket;
import java.util.EnumSet;
import java.io.IOException;

import com.tailf.ncs.ApplicationComponent;
import com.tailf.notif.Notif;
import com.tailf.notif.HaNotification;
import com.tailf.notif.NotificationType;
import com.tailf.ncs.NcsMain;
import com.tailf.conf.ConfException;

import org.apache.log4j.Logger;

public class HAControllerAppCmp implements ApplicationComponent {

    private static final Logger log =
        Logger.getLogger ( HAControllerAppCmp.class );

    HAController haController;
    boolean shouldRun = true;
    Notif notif = null;
    Socket notifSocket = null;
    public void init () throws Exception {
        log.info("\n ----- haController initializing  ----- ");
        haController = HAController.getController();

        NcsMain ncsMain = NcsMain.getInstance();
        notifSocket = new Socket (ncsMain.getNcsHost(),
                                  ncsMain.getNcsPort());
        notif =
            new Notif ( notifSocket ,
                        EnumSet.of(NotificationType.NOTIF_HA_INFO) );

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
}
