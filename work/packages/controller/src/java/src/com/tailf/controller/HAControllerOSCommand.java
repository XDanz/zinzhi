package com.tailf.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.IOException;
import org.apache.log4j.Logger;

public abstract class HAControllerOSCommand {

    private static final Logger log =
        Logger.getLogger ( HAControllerOSCommand.class );

    protected Process process;
    protected String[] execCmd;

    public void runCommand (List<String> cmd) throws HAControllerException {
        String[] cmdArr = cmd.<String>toArray(new String[0]);

        Runtime r = Runtime.getRuntime();
        try {
            process = r.exec ( cmdArr );
            processExit ( process.waitFor() ) ;

        } catch ( InterruptedException e ) {
            try {
                process.getOutputStream().close();
            } catch ( IOException ee ) {
                log.error("",ee );
            }
        } catch ( Exception e ) {
            log.error("",e );
            throw new HAControllerException ( e );
        }  finally {
            process.destroy();
        }

    }


    void processExit ( int exitValue ) throws HAControllerException {
        log.info ( " osCommand " + process + " exit with " + exitValue );
        if ( exitValue != 0 ) {
            String errMsg = null;
            try {
                errMsg = retriveErrMsg();
                log.info ( "ERROR-MSG :" + errMsg );
            } catch ( Exception e ) {
                log.error("", e ) ;
                throw new HAControllerVipException ( errMsg);
            }
        }
    }

    String retriveErrMsg () throws Exception {
        BufferedReader br =
            new BufferedReader (
                                new InputStreamReader(process
                                                      .getErrorStream ()));

        StringBuffer buf = new StringBuffer();
        String line = null;

        while ( (line = br.readLine()) != null )
            buf.append(line);

        String errMsg = buf.toString().trim();
        return errMsg;
    }
}
