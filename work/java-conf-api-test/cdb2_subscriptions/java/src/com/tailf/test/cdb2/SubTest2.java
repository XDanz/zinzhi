package com.tailf.test.cdb2;

import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

import com.tailf.cdb.CdbSubscription;
import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbDiffIterate;
import com.tailf.cdb.CdbSubscriptionSyncType;

import com.tailf.conf.DiffIterateOperFlag;
import com.tailf.conf.DiffIterateResultFlag;
import com.tailf.conf.ConfPath;
import com.tailf.conf.ConfBuf;
import com.tailf.conf.ConfNamespace;
import com.tailf.conf.ConfValue;
import com.tailf.conf.Conf;
import com.tailf.conf.ConfObject;

import org.apache.log4j.Logger;

import com.tailf.test.cdb2.namespaces.zub;

public class SubTest2 {
    private static Logger log =
        Logger.getLogger(SubTest2.class);

    public static void
        main ( String [] arg )
    {
        try {
            new SubTest2().test();
        } catch ( Exception e ) {
            log.error ("", e);
            System.exit ( 1 );
        }
    }

    public void test() throws Exception {
        // Socket s = new Socket("127.0.0.1", Conf.PORT);
        // Cdb cdb = new Cdb("test",s);
        // CdbSubscription sub = cdb.newSubscription();
        // int subid =
        //     sub.subscribe(1,zub.hash,
        //                 "/zconfigzub/buzz-interfaces/buzz/servers/server");
        // sub.subscribeDone();

        // /* add new entry from maapi
        //  * cant run this in the same thread since it
        //  * will deadlock the system
        //  */
        // TestMaapi maapi = new TestMaapi();
        // ConfNamespace n = new zub();
        // maapi.startSession();
        // maapi.startTrans();
        // maapi.setNamespace( n.uri());
        // //      try { maapi.delete("/system/computer{fred}"); }
        // //catch (Exception ignore) {}
        // //   try { maapi.commitTrans();} catch (Exception ignore) {}
        // //     try { maapi.endTrans();} catch (Exception ignore) {}
        // //    try { maapi.startTrans();} catch (Exception ignore) {}
        // maapi.create("/zconfigzub/buzz-interfaces/buzz/servers/server{ftp}");
        // maapi.setElem("/zconfigzub/buzz-interfaces/buzz/servers" +
        //               "/server{ftp}/ip", new ConfBuf("127.0.0.1"));

        // // maapi.validateTrans();
        // // maapi.prepareTrans();
        // // maapi.commitTrans();

        // maapi.applyTrans();
        // maapi.endTrans();
        // maapi.endSession();

        // maapi.startSession();
        // maapi.startTrans();
        // maapi.setNamespace( n.uri());

        // maapi.create (" /zconfigzub/buzz-interfaces/buzz/servers" +
        //               "/server{ftp}/pres-foo");
        // maapi.applyTrans();
        // maapi.endTrans();
        // maapi.endSession();

        // maapi.startSession();
        // maapi.startTrans();
        // maapi.delete ("/zconfigzub/buzz-interfaces/buzz/servers/
        //server{ftp}");
        // maapi.applyTrans();
        // maapi.endTrans();
        // maapi.endSession();

        // maapi.start();
        // /* now do the blocking read */
        // int[] points = sub.read();

        // DiffIterateImpl0 diff = new DiffIterateImpl0 ();
        // sub.diffIterate ( points[0], diff);
        // showResult ( diff.results() );

        // List<Result> expectedValues =
        //     new ArrayList<Result>();
        // int h = zub.hash;

        // ConfObject[] ekp0 =
        //     new ConfObject[]
        //     {
        //         new ConfKey ( new ConfBuf("ftp")),
        //         new ConfTag( h, zub._server),
        //         new ConfTag( h, zub._servers),
        //         new ConfTag( h, zub._buzz),
        //         new ConfTag( h, zub._buzz_interfaces),
        //         new ConfTag( h, zub._buzz_zconfigzub)
        //     };
        // DiffIterateOperFlag eop0 =
        //     DiffIterateOperFlag.MOP_CREATED;

        // expectedValues.add (
        //                     new Result (new ConfPath ( ekp ),
        //                                 eop0,
        //                                 null,
        //                                 null));

        // ConfObject[] ekp0 =
        //     new ConfObject[]
        //     {
        //         new ConfTag( h, zub._srv_name),
        //         new ConfKey ( new ConfBuf("ftp")),
        //         new ConfTag( h, zub._server),
        //         new ConfTag( h, zub._servers),
        //         new ConfTag( h, zub._buzz),
        //         new ConfTag( h, zub._buzz_interfaces),
        //         new ConfTag( h, zub._buzz_zconfigzub)
        //     };
        //    DiffIterateOperFlag eop1 =
        //     DiffIterateOperFlag.MOP_VALUE_SET;

        // // expectedValues.add(
        // //                    new Result ( new ConfPath ( ekp1 ),













        // // expected (
        // sub.sync(CdbSubscriptionSyncType.DONE_PRIORITY);


        // points = sub.read();
        // diff = new DiffIterateImpl0 ();
        // sub.diffIterate ( points[0], diff);
        // showResult ( diff.results() );
        // sub.sync(CdbSubscriptionSyncType.DONE_PRIORITY);



        // points = sub.read();
        // diff = new DiffIterateImpl0 ();
        // sub.diffIterate ( points[0], diff);
        // showResult ( diff.results() );
        // sub.sync(CdbSubscriptionSyncType.DONE_SOCKET);

        // s.close();
    }

    public void showResult ( List< Result > results ) {
        int i = 0;
        log.info ( " Results --> ");
        for ( Result res : results ) {
            log.info ("###### iter " +  (i++) + " Result ");
            log.info ( res.getPath().toString() + " --> " + res.getOp());
            if ( res.getOp() == DiffIterateOperFlag.MOP_VALUE_SET ) {
                log.info ( res.getPath().toString() + " --> " +
                           res.getNewValue());
            }

        }

    }

    class DiffIterateImpl0 implements CdbDiffIterate {
        // private  Logger sublog =
        //     Logger.getLogger(DiffIterateImpl0.class);
        List<Result> results = new ArrayList<Result>();

        public DiffIterateResultFlag iterate(ConfObject[] kp,
                                             DiffIterateOperFlag op,
                                             ConfObject old_value,
                                             ConfObject new_value,
                                             Object initstate) {
            log.debug ( "adding -->" ) ;
            results.add ( new Result ( new ConfPath ( kp ),
                                       op,
                                       old_value,
                                       new_value));
            log.debug (" results.size(): " + results.size());
            log.debug ( "adding --> ADDED! " ) ;

            return DiffIterateResultFlag.ITER_RECURSE;
        }

        public List<Result> results() {
            return results;
        }
    }

    class Result {
        private ConfPath path;
        private DiffIterateOperFlag op;
        private ConfObject oldValue;
        private ConfObject newValue;

        public ConfPath getPath () {
            return this.path;
        }

        public DiffIterateOperFlag getOp() {
            return this.op;
        }

        public ConfObject getNewValue () {
            return this.newValue;
        }

        public ConfObject getOldValue() {
            return this.oldValue;
        }

        Result ( ConfPath path , DiffIterateOperFlag op,
                 ConfObject old_value, ConfObject new_value) {
            this.path = path;
            this.op = op;
            this.oldValue = old_value;
            this.newValue = new_value;
        }
    }
}

