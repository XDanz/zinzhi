package com.tailf.controller;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

// calls the bash script autoifdown.sh which blocks on bash built-in read
// command. This class is used by the HAControllerVipManager
public class HAControllerAutoIfDown extends HAControllerOSCommand {
    private static final Logger log =
        Logger.getLogger ( HAControllerAutoIfDown.class );
    private InetAddress addr;
    private NetworkInterface ifc;
    private int index;
    private File path;
    private List<String> cmd = new ArrayList<String>();
    HAControllerAutoIfDown ( File path,  InetAddress addr ,
                             NetworkInterface ifc ,
                             int index )
        throws Exception {
        this.path = path;
        this.addr = addr;
        this.ifc = ifc;
        this.index = index;
        cmd.add ( "sudo");
        File script = new File ( path , "scripts" );
        File shellCmd = new File ( script , "autoifdown.sh" );
        cmd.add ( shellCmd.getPath() );
        cmd.add ( addr.getHostAddress());
        cmd.add ( "32" );
        cmd.add ( ifc.getName());
        cmd.add ( Integer.toBinaryString(index));
        log.info (" executing command : " +
                  cmd +
                  " in " + path ) ;
        // blocking
        runCommand ( cmd );
        log.warn ( " COMMAND "  +  cmd + "  COMPLETE ");
    }
}
