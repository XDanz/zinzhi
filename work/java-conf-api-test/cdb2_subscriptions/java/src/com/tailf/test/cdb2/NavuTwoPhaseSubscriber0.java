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

import java.util.concurrent.Executor;


import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbExtendedException;
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

import com.tailf.navu.NavuCdbSubscriber;
import com.tailf.navu.NavuNode;
import com.tailf.navu.NavuListEntry;
import com.tailf.navu.NavuCdbTwoPhaseDiffIterate;
import com.tailf.navu.NavuCdbSubscriptionConfigContext;
import com.tailf.navu.NavuCdbSubscriptionTwoPhaseContext;
import com.tailf.navu.NavuCdbSubscribers;

import com.tailf.test.cdb2.namespaces.zub;
import org.apache.log4j.Logger;


public class NavuTwoPhaseSubscriber0
{

    private static Logger log =
        Logger.getLogger ( NavuTwoPhaseSubscriber0.class );

    static final String confdAddress = "127.0.0.1";
    static final int confdPort = Conf.PORT;
    static Cdb cdb = null;
    static final Cdb cdb2 = null;
    static CdbSubscription cdbSubscriber = null;

    public static void
        main ( String arg[] )

    {
        try {
            NavuCdbSubscriber twoPhaseSub =
                NavuCdbSubscribers.twoPhaseSubscriber ( confdAddress ,
                                                        confdPort ,
                                                        "2phase");

            twoPhaseSub.register ( new DiffIterateImpl () ,
                                   new ConfPath ("/zconfigzub/" +
                                                 "buzz-interfaces/buzz/" +
                                                 "servers/server" ));
            twoPhaseSub.subscriberStart();
            twoPhaseSub.awaitRunning();
            
            twoPhaseSub.awaitStopped();
            twoPhaseSub.executor().shutdownNow();

        } catch ( Exception e ) {
            log.error("",e);
        }

    }


    static class DiffIterateImpl implements NavuCdbTwoPhaseDiffIterate
    {

        public void iterate(NavuCdbSubscriptionConfigContext ctx) {
            log.info(" XXXXX iterate(" + ctx  + ")  =>" );
            log.info(new ConfPath( ctx.keyPath() ));


            log.info(" NOTIF-TYPE=" + ctx.getNotifType() );


            log.info(" XXXXX iterate(" + ctx  + " ) => ok");

        }

        public void prepare(NavuCdbSubscriptionTwoPhaseContext ctx) {
            log.info(" XXXX prepare(" + ctx + ") =>");

            log.info( new ConfPath( ctx.keyPath() ));

            if ( ctx.getNode().getChange() == DiffIterateOperFlag.MOP_CREATED )
                {
                    NavuNode node = ctx.getNode();
                    NavuListEntry entr = (NavuListEntry)node;
                    ConfKey key = entr.getKey();
                    ConfValue val = (ConfValue)key.elementAt(0);
                    log.info( " val= " + val);
                    if ( val.toString().equals("fail") ) {
                        ctx.abort ( new CdbExtendedException (4, null , null,
                                                  " Application Error")
                                    );
                    }
                    ctx.iterContinue();
                }


            log.info(" XX NOTIF-TYPE=" + ctx.getNotifType() );

            log.info(" XXXX prepare(" + ctx + ") => ok");
        }
    }
}
