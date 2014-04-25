package com.tailf.test.cdb1;

import java.net.Socket;
import java.net.InetAddress;
import java.util.Iterator;

import com.tailf.cdb.CdbSession;
import com.tailf.cdb.Cdb;
import com.tailf.maapi.MaapiUserSessionFlag;

import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuContext;
import com.tailf.navu.NavuException;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfException;


import com.tailf.test.cdb1.namespaces.optest;

import org.apache.log4j.Logger;

public class ExistsTest0 {
    private static Logger log =
        Logger.getLogger (  ExistsTest0.class );

    public static void
        main ( String[] arg )
    {

        try {
            Cdb cdb = new Cdb ( "test",
                                new Socket ("127.0.0.1", Conf.PORT ) );

            CdbSession sess = cdb.startSession ( );

            ConfPath path = new ConfPath ( "/optest/shirts/shirt");
            int num =
                sess.getNumberOfInstances ( path ) ;

            log.info ( " num :" + num );
            ConfPath pathIndx0 =
                new ConfPath ( path.toString() +  "[" + 0 + "]");

            String msg =  " the path " + pathIndx0 + " ";
            if ( sess.exists ( pathIndx0))
                log.info ( msg + "exists!");
            else
                log.info ( msg + " does not exists!");

        } catch (ConfException e ) {
            log.error("",e);
            System.exit(1);

        } catch ( Exception e ) {
            log.error("", e);
            System.exit(1);
        }
    }
}