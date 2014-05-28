package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.File;
import org.apache.log4j.Logger;

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
        try {
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
            runCommand ( cmd );
            log.warn ( " COMMAND COMPLETE ");
        } finally { 
            log.warn ( "Closing OutPutStream !");
            if ( process != null ) {
                try {
                    process.getErrorStream().close();
                } catch ( Exception e ) {
                    
                }
            }
        }
    }
   
}