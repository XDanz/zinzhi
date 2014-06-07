package com.tailf.controller;

import java.util.List;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.apache.log4j.Logger;

public abstract class HAControllerOSCommand {

    private static final Logger log =
        Logger.getLogger ( HAControllerOSCommand.class );

    protected Process process;
    protected String[] execCmd;

    /**
     *  Runs a native os command.
     *  @param cmd command parameter to exeute
     *  @throws HAControllerException
     */
    public void runCommand (List<String> cmd) throws HAControllerException {
        String[] cmdArr = cmd.<String>toArray(new String[0]);

        Runtime r = Runtime.getRuntime();
        try {
            process = r.exec ( cmdArr );
            processExit ( process.waitFor() ) ;

        } catch ( InterruptedException e ) {
            try {
                // When the current thread gets interrupted
                // should we close the outputstream? how does
                // this affect autoifdown.sh script if its
                // blocks on read
                process.getOutputStream().close();

            } catch ( IOException ee ) {
                // If an I/O error occurs when closing the
                // outputstream.
                log.error("",ee );
            }
        } catch ( HAControllerVipException e ) {
            throw e;
        } catch ( Exception e ) {
            log.error("",e );
            throw new HAControllerException ( e );
        }  finally {
            process.destroy();
        }
    }

    /**
     * Builds the and throws a HAControllerException
     * if error message if exitValue != 0 else return silently
     * otherwise throw a Exception.
     *
     * @throw  HAcontrollerException
     */
    void processExit ( int exitValue ) throws HAControllerException {
        log.info ( " osCommand " + process + " exit with " + exitValue );
        if ( exitValue != 0 ) {
            String errMsg = retriveErrMsg();
            log.info ( "ERROR-MSG :" + errMsg );
            if ( errMsg == null )
                errMsg = "OSError";
            throw new HAControllerVipException ( errMsg );
        }
    }

    // Reads the Error Stream for error msgs.
    // this is for building the Exception Message.
    protected String retriveErrMsg ()  {
        String errMsg = null;

        try {
            InputStreamReader iReader =
                new InputStreamReader(process
                                      .getErrorStream ());

            BufferedReader br =
                new BufferedReader ( iReader);

            StringBuffer buf = new StringBuffer();
            String line = null;

            while ( (line = br.readLine()) != null )
                buf.append(line);

            errMsg = buf.toString().trim();
        } catch ( IOException e ) {
            log.error("",e);
        }
        return errMsg;
    }
}
