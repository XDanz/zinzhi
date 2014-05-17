package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public abstract class HAControllerOSCommand {
    protected Process process;
    public void runCommand (List<String> cmd) throws HAControllerException {
        String[] cmdArr = cmd.<String>toArray(new String[0]);
        
        try {
            Runtime r = Runtime.getRuntime();
            process = r.exec ( cmdArr );
            InputStream errStream = process.getErrorStream ();
            BufferedInputStream bufIn = 
                new BufferedInputStream ( errStream );
            
            
            int exitValue = process.waitFor();

            if ( exitValue != 0 ) { 
                
                StringBuffer buf = new StringBuffer();
                byte[] chunk = new byte[1024];
                int read = -1;

                while ((read = bufIn.read(chunk,0,chunk.length )) != -1) {
                    buf.append ( new String (chunk) );
                }

                String errMsg = buf.toString().trim();
                
                throw new HAControllerVipException ( errMsg );
            }
        } catch ( Exception e ) {
            throw new HAControllerVipException ( e.getMessage() );
        }    
    }
}