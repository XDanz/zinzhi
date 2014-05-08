package com.tailf.controller;

import java.net.Socket;
import java.util.EnumSet;
import com.tailf.ncs.ApplicationComponent;
import com.tailf.notif.Notif;
import com.tailf.notif.HaNotification;
import com.tailf.notif.NotificationType;
import com.tailf.ncs.NcsMain;

import org.apache.log4j.Logger;

public class HAControllerAppCmp implements ApplicationComponent {

    private static final Logger log =
        Logger.getLogger ( HAControllerAppCmp.class );

    HAController haController;
    boolean shouldRun = true;
    Notif notif = null;
    Socket notifSocket = null;
    public void init () throws Exception {
        haController = HAControllerFactory.getController();
        log.info(" ----- haController initialized ----- ");

        NcsMain ncsMain = NcsMain.getInstance();
        notifSocket = new Socket (ncsMain.getNcsHost(),
                                  ncsMain.getNcsPort());
        notif =
            new Notif ( notifSocket ,
                        EnumSet.of(NotificationType.NOTIF_HA_INFO) );


    }

    public void run () {
        log.info (" ---- Running ---- ");
        try {

            while ( shouldRun ) {

                HaNotification notification =
                    (HaNotification)notif.read();

                haController.haEvent ( notification );

            }
        } catch ( Exception e ) {
            log.error("",e );
        }

    }

    public void finish() throws Exception {
        shouldRun = false;
    }
}