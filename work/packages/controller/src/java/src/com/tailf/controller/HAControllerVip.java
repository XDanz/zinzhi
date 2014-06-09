package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.net.InetAddress;

import java.net.NetworkInterface;

public class HAControllerVip extends HAControllerOSCommand {

    //sudo /sbin/ip addr add 192.168.60.33/32 dev eth1 label eth1:svip0
    //sudo /sbin/ip addr del 192.168.60.33/32 dev eth1:svip0

    private InetAddress addr;
    private NetworkInterface ifc;
    private int index;
    private List<String> cmd = new ArrayList<String>();

    HAControllerVip ( InetAddress addr , NetworkInterface ifc , int index ) {
        this.addr = addr;
        this.ifc = ifc;
        this.index = index;
    }

    void bringInterfaceUp() throws HAControllerException {
        addHead ();
        cmd.add ( "add" );
        appendAddress ();
        cmd.add ( ifc.getName() );
        cmd.add ( "label" );
        cmd.add ( ifc.getName() + ":svip" + index);
        List<String> cpyCmd =
            new ArrayList<String>( cmd );
        cmd.clear();

        runCommand( cpyCmd );

    }

    String getName () {
        return ifc.getName() + ":svip" + index;
    }

    /**
     *  Bring down the Subinterface if exist
     *
     *  @throws HAControllerException
     */
    void bringInterfaceDown() throws HAControllerException {
        
        if ( ifc != null ) {
            Enumeration<NetworkInterface> 
                subIfc = ifc.getSubInterfaces () ;
            
            while ( subIfc.hasMoreElements() ) {
                NetworkInterface subInterface = 
                    subIfc.nextElement();

                if (  subInterface.getName().equals(getName()) ) {
                    addHead ();
                    cmd.add ("del");
                    appendAddress ();
                    cmd.add ( getName() );
                    
                    List<String> cpyCmd =
                        new ArrayList<String>( cmd );

                    cmd.clear();
                    runCommand ( cpyCmd );
                }
            }
        }
    }

    void addHead ( ) {
        cmd.add ("sudo");
        cmd.add ("/sbin/ip");
        cmd.add ("addr");
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
