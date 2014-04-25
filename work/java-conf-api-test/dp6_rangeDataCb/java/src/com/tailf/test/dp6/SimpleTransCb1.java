package com.tailf.test.dp6;
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

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.TransCallback;
import com.tailf.dp.proto.TransCBType;
import com.tailf.test.dp6.namespaces.*;
import java.util.Iterator;
import java.io.*;
import org.apache.log4j.Logger;

public class SimpleTransCb1  {

    private static Logger LOGGER =
        Logger.getLogger(SimpleTransCb1.class);

    @TransCallback(callType=TransCBType.INIT)
        public void init(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("init(): userinfo= "+trans.getUserInfo());
    }

    @TransCallback(callType=TransCBType.TRANS_LOCK)
        public void transLock(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("transLock()");
        SimpleWithTrans1.lock();
    }

    @TransCallback(callType=TransCBType.TRANS_UNLOCK)
        public void transUnlock(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("transUnlock()");
        SimpleWithTrans1.unlock();
    }

    @TransCallback(callType=TransCBType.WRITE_START)
        public void writeStart(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("writeStart()");
    }

    @TransCallback(callType=TransCBType.PREPARE)
        public void prepare(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("prepare()");
        String name;
        Server s;
        for (Iterator<DpAccumulate> it = trans.accumulated(); it.hasNext(); ) {
            DpAccumulate ack= it.next();
            // check op
            switch (ack.getOperation()) {
            case DpAccumulate.SET_ELEM:
                LOGGER.debug("SET_ELEM:");
                LOGGER.debug(ack.getKP());
                name = ((ConfKey) ack.getKP()[1]).elementAt(0).toString();
                s = SimpleWithTrans1.findServer( name );
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
                LOGGER.debug("CREATE:");
                LOGGER.debug(ack.getKP());
                name = ((ConfKey) ack.getKP()[0]).elementAt(0).toString();
                s = new Server(name);
                SimpleWithTrans1.newServer(s);
                break;
            case DpAccumulate.REMOVE:
                LOGGER.debug("REMOVE:");
                LOGGER.debug(ack.getKP());
                name = ((ConfKey) ack.getKP()[0]).elementAt(0).toString();
                SimpleWithTrans1.removeServer(name);
                break;
            }
        }
        try {
            SimpleWithTrans1.save("running1.prep");
        } catch (IOException e) {
            throw new DpCallbackException("failed to save file: running1.prep");
        }
    }

    @TransCallback(callType=TransCBType.ABORT)
        public void abort(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("abort()");
        SimpleWithTrans1.restore("running1.DB");
        SimpleWithTrans1.unlink("running1.prep");
    }

    @TransCallback(callType=TransCBType.COMMIT)
        public void commit(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("commit()");
        try {
            SimpleWithTrans1.rename("running1.prep","running1.DB");
        } catch (DpCallbackException e) {
            throw new DpCallbackException("commit failed");
        }
    }

    @TransCallback(callType=TransCBType.FINISH)
        public void finish(DpTrans trans) throws DpCallbackException {
        LOGGER.debug("finish()");
    }



}
