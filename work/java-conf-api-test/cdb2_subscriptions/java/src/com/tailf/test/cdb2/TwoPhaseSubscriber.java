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
import com.tailf.cdb.CdbNotificationType;
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


public class TwoPhaseSubscriber {

 private static Logger log = Logger.getLogger ( Subscriber0.class );

    static final String confdAddress = "127.0.0.1";
    static final int confdPort = Conf.PORT;
    static Cdb cdb = null;
    static final Cdb cdb2 = null;
    static CdbSubscription cdbSubscriber = null;

    public static void
         main ( String arg[] )

    {
        SocketChannel sockChannel = null;
        
        try {
            sockChannel = SocketChannel.open ();
            sockChannel.configureBlocking ( false ) ;

            SocketAddress sockAddress =
                new InetSocketAddress ( confdAddress, confdPort );
            
            sockChannel.connect( sockAddress );

        } catch ( IOException e ) {
            log.error("Could not open SocketChannel ");
            e.printStackTrace();
            System.exit(1);

        }

        try {
            cdb = new Cdb ("Subscription-Sock",  sockChannel );
            cdbSubscriber = cdb.newSubscription();

            int subPoint = cdbSubscriber
                .subscribe( CdbSubscriptionType.SUB_RUNNING_TWOPHASE, 1,
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
            keys = sockChannel.register( selector,
                                         SelectionKey.OP_READ );

        } catch ( IOException e ) {
            log.error("", e);
            System.exit(1);
        }

        while ( true ) {
            try {
                selector.select();
            } catch ( IOException e ) {
                log.error("Poll failed:");
                System.exit( 1 ) ;
            }

            Iterator<SelectionKey>  selectedKeys =
                selector.selectedKeys().iterator();

            while ( selectedKeys.hasNext() ) {
                SelectionKey key = (SelectionKey) selectedKeys.next();
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
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }



    static class DiffIterateImpl implements CdbDiffIterate 
    {
        private CdbSubscription cdbSubscription;
        private final String[] nTypes = new String[] {"unused",
                                                      "Prepare",
                                                      "Commit",
                                                      "Abort", 
                                                      "Oper"};
        
        
        public DiffIterateImpl (CdbSubscription cdbSubscription ) {
            this.cdbSubscription = cdbSubscription;
        }

        public DiffIterateResultFlag iterate( ConfObject[] kp, 
                                              DiffIterateOperFlag op,
                                              ConfObject oldValue,
                                              ConfObject newValue ,
                                              Object initstate )
        {

            //try {
            log.info( new ConfPath( kp ) + " => " + op );

            CdbNotificationType notifType =
                cdbSubscription.getLatestNotificationType();
                    
            log.info(" NOTIF-TYPE=" + notifType );
            log.info("----- Notification Type : " +
                     nTypes[notifType.getValue()]);

                
            return DiffIterateResultFlag.ITER_RECURSE;
                    
            //         /* diff iterate ! */
            //         sub.diffIterate(subid, new test());
            //         if (ii == 0) {
            //             sub
            //                 .abortTransaction(
            //                                   new CdbExtendedException(CdbExtendedException
            //                                                            .ERRCODE_APPLICATION,  null, null,
            //                                                            "T E S T   A B O R T !"));
            //         } else {
            //             sub.sync(CdbSubscriptionSyncType.DONE_PRIORITY);
            //         }
            //     } catch (MaapiException e) {
            //         e.printStackTrace();
            //         break;
            //     }
            // }

            //s.close();
            //}
    }

    }
}
