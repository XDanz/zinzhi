package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.net.NetworkInterface;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class HAControllerVip extends HAControllerOSCommand {

    //sudo /sbin/ip addr add 192.168.60.33/32 dev eth1 label eth1:svip0
    //sudo /sbin/ip addr del 192.168.60.33/32 dev eth1:svip0

    private InetAddress addr;
    private NetworkInterface ifc;
    private int index;
    private List<String> cmd;

    HAControllerVip ( InetAddress addr , NetworkInterface ifc , int index ) {
        this.addr = addr;
        this.ifc = ifc;
        this.index = index;
        cmd = 
            new ArrayList<String>(
                                  Arrays.<String>asList ("sudo", 
                                                         "/sbin/ip" , 
                                                         "addr"));
    }

    void bringInterfaceUp() throws HAControllerException {
        cmd.add ( "add" );
        appendAddress ();
        cmd.add ( ifc.getName() );
        cmd.add ( "label" );
        cmd.add ( ifc.getName() + ":svip" + index);

        runCommand( cmd );
        cmd.clear();
    }

    void bringInterfaceDown() throws HAControllerException {
        cmd.add ("del");
        appendAddress ();
        cmd.add ( ifc.getName() + ":svip" + index );
        runCommand ( cmd );
        
        cmd.clear();
    }


    void appendAddress ( ) {
        cmd.add (addr.getHostAddress() + "/32");
        cmd.add ("dev");
    }


    InetAddress getInetAddress ( ) {
        return this.addr;
    }

    NetworkInterface getNetworkInterface () {
        return this.ifc;
    }
    
    int getVipIndex ( ) {
        return this.index;
    }
    
    public static void main (String[] arg ) {
        try {
            if ( arg[0].equals ( "up")) {
                InetAddress addr = InetAddress.getByName("192.168.60.33");
                NetworkInterface ifc = NetworkInterface.getByName("eth1");
                
                HAControllerVip hcv = 
                    new HAControllerVip (  addr, ifc, 0);
                
                hcv.bringInterfaceUp ();
            }  else if ( arg[0].equals("down")) {
                InetAddress addr = InetAddress.getByName("192.168.60.33");
                NetworkInterface ifc = NetworkInterface.getByName("eth1");
                
                HAControllerVip hcv = 
                    new HAControllerVip (  addr, ifc, 0 );
                
                hcv.bringInterfaceDown ();

            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
}