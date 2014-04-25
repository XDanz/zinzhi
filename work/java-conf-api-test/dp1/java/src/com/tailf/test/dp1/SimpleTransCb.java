/*    -*- Java -*-
 *
 *  Copyright 2007 Tail-F Systems AB. All rights reserved.
 *
 *  This software is the confidential and proprietary
 *  information of Tail-F Systems AB.
 *
 *  $Id$
 *
 */
package com.tailf.test.dp1;

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.TransCallback;
import com.tailf.dp.proto.TransCBType;

import java.util.Iterator;
import java.io.*;

import org.apache.log4j.Logger;
import com.tailf.test.dp1.namespaces.*;

public class SimpleTransCb {

    private static Logger LOGGER = Logger.getLogger(SimpleTransCb.class);

    @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.info("START TRANS:" + trans.getTransaction());
        LOGGER.info("init(): userinfo= "+trans.getUserInfo());
    }

    @TransCallback(callType=TransCBType.TRANS_LOCK)
        public void transLock(DpTrans trans) throws DpCallbackException {
        LOGGER.info(" TRANS_LOCK:" + trans.getTransaction());
        LOGGER.info("transLock()");
        SimpleWithTrans.lock();
    }

    @TransCallback(callType=TransCBType.TRANS_UNLOCK)
        public void transUnlock(DpTrans trans) throws DpCallbackException {
        LOGGER.info("transUnlock()");
        SimpleWithTrans.unlock();
    }

    @TransCallback(callType=TransCBType.WRITE_START)
        public void writeStart(DpTrans trans) throws DpCallbackException {
        LOGGER.info("writeStart()");
    }

    @TransCallback(callType=TransCBType.PREPARE)
        public void prepare(DpTrans trans) throws DpCallbackException {
        LOGGER.info("prepare()");
        String name;
        Server s;
        for (Iterator<DpAccumulate> it = trans.accumulated(); it.hasNext(); ) {
            DpAccumulate ack= it.next();
            // check op
            switch (ack.getOperation()) {
            case DpAccumulate.SET_ELEM:
                LOGGER.info("SET_ELEM:");
                LOGGER.info(ack.getKP());
                name = ((ConfKey) ack.getKP()[1]).elementAt(0).toString();
                s = SimpleWithTrans.findServer( name );
                if (s==null) break;
                // check leaf tag
                ConfTag leaf = (ConfTag) ack.getKP()[0];
                switch (leaf.getTagHash()) {
                case smp._name:
                    s.name = ack.getValue().toString();
                    break;
                case smp._ip:
                    ConfIPv4 ip = (ConfIPv4) ack.getValue();
                    s.addr = ip.getRawAddress();
                    break;
                case smp._port:
                    s.port = (int) ((ConfUInt32)ack.getValue()).intValue();
                    break;
                case smp._macaddr:
                    s.macaddr = ((ConfBinary)ack.getValue()).bytesValue();
                    break;
                case smp._snmpref:
                    s.snmpref = ((ConfOID)ack.getValue()).longsValue();
                    break;
                case smp._prefixmask:
                    s.prefixmask = ((ConfBinary)ack.getValue()).bytesValue();
                    break;
                }
                break;
            case DpAccumulate.CREATE:
                LOGGER.info("CREATE:");
                LOGGER.info(ack.getKP());
                name = ((ConfKey) ack.getKP()[0]).elementAt(0).toString();
                s = new Server(name);
                SimpleWithTrans.newServer(s);
                break;
            case DpAccumulate.REMOVE:
                LOGGER.info("REMOVE:");
                LOGGER.info(ack.getKP());
                name = ((ConfKey) ack.getKP()[0]).elementAt(0).toString();
                SimpleWithTrans.removeServer(name);
                break;
            }
        }
        try {
            SimpleWithTrans.save("running.prep");
        } catch (IOException e) {
            throw new DpCallbackException("failed to save file: running.prep");
        }
    }

    @TransCallback(callType=TransCBType.ABORT)
        public void abort(DpTrans trans) throws DpCallbackException {
        LOGGER.info("abort()");
        SimpleWithTrans.restore("running.DB");
        SimpleWithTrans.unlink("running.prep");
    }

    @TransCallback(callType=TransCBType.COMMIT)
        public void commit(DpTrans trans) throws DpCallbackException {
        LOGGER.info("commit()");
        try {
            SimpleWithTrans.rename("running.prep","running.DB");
        } catch (DpCallbackException e) {
            throw new DpCallbackException("commit failed");
        }
    }

    @TransCallback(callType=TransCBType.FINISH)
        public void finish(DpTrans trans) throws DpCallbackException {
        LOGGER.info("finish()");
    }




}
