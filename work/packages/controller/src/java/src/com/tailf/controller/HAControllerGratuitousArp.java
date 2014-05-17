package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;


public class HAControllerGratuitousArp extends HAControllerOSCommand {

    private HAControllerVip haCtrlVip;
    private List<String> cmd = 
        new ArrayList<String>();
    
    HAControllerGratuitousArp ( HAControllerVip haCtrlVip ) {
        this.haCtrlVip = haCtrlVip;
    }

    public void arpReply (int count) throws Exception {
        //sudo arpsend -P -i 192.168.60.33 -c 4 eth1:svip0
        addHead();
        cmd.add ("-P" );
        addTail ( count );
        runCommand ( cmd );
        cmd.clear();

    }

    public void arpResponse (int count) throws Exception {
        //sudo arpsend -U -i 192.168.60.33 -c 4 eth1:svip0
        addHead();
        cmd.add ("-U" );
        addTail ( count );
        // cmd.add ( "-i" );
        // cmd.add ( haCtrlVip.getInetAddress().getHostAddress() );
        // cmd.add ( "-c" );
        // cmd.add ( Integer.toBinaryString(count));
        // cmd.add ( haCtrlVip.getNetworkInterface ().getName() +":svip" + 
        //           haCtrlVip.getIndex());
        runCommand ( cmd );
        cmd.clear();

    }

    void addTail ( int count ) {
        cmd.add ( "-i" );
        cmd.add ( haCtrlVip.getInetAddress().getHostAddress() );
        cmd.add ( "-c" );
        cmd.add ( Integer.toBinaryString(count));
        cmd.add ( haCtrlVip.getNetworkInterface ().getName() +":svip" + 
                  haCtrlVip.getVipIndex());
    }

    void addHead ( ) {
        cmd.add ("sudo");
        cmd.add ("arpsend");
    }
}