package com.tailf.test.cdb2;

import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.net.Socket;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;


import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbSubscriptionSyncType;
import com.tailf.cdb.CdbGetModificationFlag;
import com.tailf.cdb.CdbSubscriptionType;
import com.tailf.cdb.CdbSubscription;
import com.tailf.cdb.CdbSession;

import com.tailf.conf.Conf;
import com.tailf.conf.ConfXMLParam;
import com.tailf.conf.ConfInt64;
import com.tailf.conf.ConfTag;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfKey;
import com.tailf.cdb.CdbDiffIterate;

import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.DiffIterateFlags;
import com.tailf.conf.DiffIterateResultFlag;

import com.tailf.conf.ConfObject;
import com.tailf.conf.ConfException;
import com.tailf.conf.ConfValue;
import com.tailf.conf.ConfInt64;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfUInt64;

import com.tailf.test.cdb2.namespaces.zub;
import org.apache.log4j.Logger;

public class Subscriber4 {

    private static Logger log = Logger.getLogger ( Subscriber0.class );

    static String confdAddress = "127.0.0.1";
    static int confdPort = Conf.PORT;


    public static void
         main ( String arg[] )

    {
        log.info(" starting ..");
        boolean threadStarted = false;
        while ( true ) {

            if ( !threadStarted) {
                threadStarted = !threadStarted;

                new Thread( new Runnable ( ) {

                public void run() {
                    CdbSubscription cdbSubscriber = null;
                    log.warn (" thread in run () -->");
                    SocketChannel sockChannel = null;
                    try {
                        sockChannel = SocketChannel.open ();
                        sockChannel.configureBlocking ( false ) ;



                        SocketAddress sockAddress =
                            new InetSocketAddress ( confdAddress,
                                                    confdPort );
                        sockChannel.connect( sockAddress );

                        log.warn ( " sockChannel:" + sockChannel );

                    } catch ( IOException e ) {
                        log.error("Could not open SocketChannel ");
                        e.printStackTrace();
                        System.exit(1);
                    }

                    try {
                        log.warn (" sock:" + sockChannel.socket() );
                        Cdb cdb = new Cdb ("Subscription-Sock4",
                                       sockChannel );
                        log.warn ( " sockChannel:" + sockChannel );
                         cdbSubscriber = cdb.newSubscription();

                        int subPoint = cdbSubscriber
                            .subscribe( CdbSubscriptionType.SUB_RUNNING, 1,
                                        zub.hash,
                 "/zconfigzub/buzz-interfaces/buzz/servers/server");
                        cdbSubscriber.subscribeDone();

                            } catch ( Exception e ) {
                                log.error( "Could not connect to cdb", e);
                                System.exit(1);
                            }



                            Selector selector = null;
                            SelectionKey keys = null;
                            try {
                                selector = Selector.open();
                                keys = sockChannel
                                    .register( selector,
                                               SelectionKey.OP_READ );

                            } catch ( IOException e ) {
                                log.error("", e);
                                System.exit(1);
                            }

                            //                while ( true ) {

                            try {
                                selector.select();
                            } catch ( IOException e ) {
                                log.error("Poll failed:");
                                System.exit( 1 ) ;
                            }

                            Iterator<SelectionKey>  selectedKeys =
                                selector.selectedKeys().iterator();

                        while ( selectedKeys.hasNext() ) {
                            SelectionKey key = (SelectionKey) selectedKeys
                                .next();
                            selectedKeys.remove();

                            if( !key.isValid())
                                continue;

                            try {
                                if (key.isReadable()) {
                                    int[] point = cdbSubscriber.read();

                                    EnumSet<DiffIterateFlags> enumSet =
                                        EnumSet
                                        .<DiffIterateFlags>of(DiffIterateFlags
                                                              .ITER_WANT_PREV);
                                    cdbSubscriber
                                        .diffIterate(point[0],
                                 new DiffIterateImpl(cdbSubscriber));

                                    cdbSubscriber.sync(CdbSubscriptionSyncType.
                                                       DONE_PRIORITY);

                                }
                            } catch (Exception e) {
                                log.error("",e ) ;
                                //continue;
                            }
                        } // while ( selectedKeys.hasNext())
                        log.warn (" thread leaving run() ");
                        }
                    }).start();
                log.warn( "thread started !");
            }
            try {
                Thread.sleep(2000);
            } catch ( InterruptedException e ) { }
            System.gc();
            log.warn (" Done gc !");
        }
    }

    static class DiffIterateImpl
        implements CdbDiffIterate
    {
        CdbSubscription cdbSub;

        DiffIterateImpl ( CdbSubscription sub ) {
            this.cdbSub = sub;
        }

        public DiffIterateResultFlag  iterate ( ConfObject[] kp,
                                                DiffIterateOperFlag op,
                                                ConfObject oldValue,
                                                ConfObject newValue,
                                                Object initstate )
        {
            log.info ( " *** iterations START*** ");
            log.info ( " kp : "  + Arrays.toString ( kp ) );

            // try {
            //     List<ConfXMLParam > ret =
            //         cdbSub.getModifications ( CdbGetModificationFlag.
            //                                   CDB_GET_MODS_INCLUDE_LISTS);

            //     for ( ConfXMLParam p : ret )
            //         log.info ( " p : " + p);

            //     log.info ( " *** iterations END  *** ");

            // } catch ( Exception e ) {
            //     log.error ("", e);
            // }

            return DiffIterateResultFlag.ITER_RECURSE;
        }
    }
}