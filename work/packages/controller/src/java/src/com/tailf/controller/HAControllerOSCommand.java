package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

public abstract class HAControllerOSCommand {
    private static final Logger log = 
        Logger.getLogger ( HAControllerOSCommand.class );
    protected Process process;

    public void runCommand (List<String> cmd) throws HAControllerException {
        String[] cmdArr = cmd.<String>toArray(new String[0]);
        
        Runtime r = Runtime.getRuntime();
        try {
            process = r.exec ( cmdArr );
            int exitValue = process.waitFor();
        } catch ( Exception e ) {
            log.error("",e );
        }
    }


    void processExit ( int exitValue ) throws HAControllerException {
        if ( exitValue != 0 ) { 
            String errMsg = null;
            try {
                errMsg = retriveErrMsg(); 
            } catch ( Exception e ) {
                log.error("", e ) ;
                throw new HAControllerVipException ( errMsg);
            }
        }
    }

    String retriveErrMsg () throws Exception {
        BufferedInputStream bufIn = 
            new BufferedInputStream ( process.getErrorStream ());
            
        StringBuffer buf = new StringBuffer();
        byte[] chunk = new byte[1024];
        int read = -1;
            
        while ((read = bufIn.read(chunk,0,chunk.length )) != -1) {
            buf.append ( new String (chunk) );
        }
            
        String errMsg = buf.toString().trim();
        return errMsg;

    }
}
