package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class HAControllerAutoIfDown extends HAControllerOSCommand {
    private InetAddress addr;
    private NetworkInterface ifc;
    private int index;
    private List<String> cmd = new ArrayList<String>();
    HAControllerAutoIfDown ( InetAddress addr , NetworkInterface ifc , 
                             int index ) 
    throws Exception {
        this.addr = addr;
        this.ifc = ifc;
        this.index = index;
        cmd.add ( "sudo");
        cmd.add ( "packages/scripts/autoifdown.sh");
        cmd.add ( addr.getHostAddress());
        cmd.add ( "32" );
        cmd.add ( ifc.getName());
        cmd.add ( Integer.toBinaryString(index));
        runCommand (cmd );
    }
}