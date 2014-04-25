package com.tailf.test.cdb1;

import java.net.Socket;
import java.net.InetAddress;
import java.util.Iterator;

import com.tailf.maapi.Maapi;
import com.tailf.cdb.Cdb;
import com.tailf.maapi.MaapiUserSessionFlag;

import com.tailf.navu.NavuContainer;
import com.tailf.navu.NavuContext;
import com.tailf.navu.NavuException;
import com.tailf.conf.Conf;

import com.tailf.test.cdb1.namespaces.optest;

import org.apache.log4j.Logger;

public class TestListIterCdb {

    private static Logger log = Logger.getLogger (  TestListIterCdb.class );


    public static void
        main ( String[] arg )
    {

        try {
            Cdb cdb = new Cdb ( "test", new Socket ("127.0.0.1", Conf.PORT ) );

            NavuContainer rootContainer =
                new NavuContainer ( new NavuContext (cdb));

            NavuContainer mtestModule =
                rootContainer.container ( optest.hash);

            NavuContainer operContainer =
                mtestModule.container(optest._optest)
                .container(optest._optest_stats);

            log.info(" before getting it ");
            Iterator<NavuContainer> it =
                operContainer.container("keyless").list("list1").iterator();

            log.info ( " it :" + it);

            // while (it.hasNext() ) {
            //     log.info(" c :" + it.next() );
            // }
        } catch (NavuException e ) {
            //log.error(e.getMessage(), e);
            //Throwable t = e.getCause();
            //log.error("",t);
            System.exit(1);

        } catch ( Exception e ) {
            log.error("", e);
            System.exit(1);
        }
    }
}