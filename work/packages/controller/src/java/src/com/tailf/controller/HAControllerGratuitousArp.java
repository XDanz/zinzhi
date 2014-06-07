package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class HAControllerGratuitousArp extends HAControllerOSCommand
{
    private static final Logger log = 
        Logger.getLogger ( HAControllerGratuitousArp.class );

    private HAControllerVip haCtrlVip;
    private List<String> cmd = 
        new ArrayList<String>();
    
    HAControllerGratuitousArp ( HAControllerVip haCtrlVip ) {
        this.haCtrlVip = haCtrlVip;
    }

    /**
     *  Send count nummer of arpReply with arpsend os command
     *   @trows HAControllerException 
     */
    public void arpReply (int count) throws HAControllerException {
        log.info ( " arpReply () => ");
        //sudo arpsend -P -i 192.168.60.33 -c 4 eth1:svip0
        addHead();
        cmd.add ("-P" );
        addTail ( );
        for ( int i = 0 ; i < count ; i++ ) {
            log.info (" running command :" + cmd );
            runCommand ( cmd );
        }
        cmd.clear();
        log.info ( " arpReply () => ok");

    }

    public void arpResponse (int count) throws HAControllerException {
        log.info ( " arpResponse () => ");
        //sudo arpsend -U -i 192.168.60.33 -c 4 eth1:svip0
        addHead();
        cmd.add ("-U" );
        addTail ();
        for ( int i = 0 ; i < count ; i++ ) {
            log.info (" running command :" + cmd );
            runCommand ( cmd );
        }
        cmd.clear();
        log.info ( " arpResponse () => ok");

    }

    void addTail ( ) {
        cmd.add ( "-i" );
        cmd.add ( haCtrlVip.getInetAddress().getHostAddress() );
        cmd.add ( "-c" );
        cmd.add ( "1");
        cmd.add ( haCtrlVip.getNetworkInterface ().getName() +":svip" + 
                  haCtrlVip.getVipIndex());
    }

    void addHead ( ) {
        cmd.add ("sudo");
        cmd.add ("arpsend");
    }
}
