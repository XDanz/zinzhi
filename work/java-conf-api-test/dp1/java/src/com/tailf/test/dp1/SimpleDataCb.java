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
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import com.tailf.test.dp1.namespaces.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.log4j.Logger;

public class SimpleDataCb  {
    private static Logger LOGGER = Logger.getLogger(SimpleDataCb.class);
    private String theTag = "DumbTag";
    private String theAnnotation = "DumbAnnotation";
    private String theInactive = "DumbInactive";

    @DataCallback(callPoint="simplecp", callType=DataCBType.ITERATOR)
        public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        return SimpleWithTrans.iterator();
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT)
        public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj)
        throws DpCallbackException {
        LOGGER.info("getKey()");
        Server s = (Server) obj;
        return new ConfKey( new ConfObject[] { new ConfBuf(s.name) });
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
        public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("getElem()");

        try {
            // a primitive check that timeoutsetting calls works.
            trans.dataSetTimeout(10);
        } catch (IOException e) {
            throw new DpCallbackException(e.getMessage());
        }

        String name = ((ConfKey) kp[1]).elementAt(0).toString();
        Server s = SimpleWithTrans.findServer( name );
        if (s == null) return null; /* not found */

        /* switch on xml elem tag */
        ConfTag leaf = (ConfTag) kp[0];

        switch (leaf.getTagHash()) {
        case smp._name:
            return new ConfBuf( s.name );
        case smp._ip:
            return new ConfIPv4( s.addr );
        case smp._port:
            return new ConfUInt16( s.port );
        case smp._macaddr:
            return new ConfHexList( s.macaddr );
        case smp._snmpref:
            return new ConfOID( s.snmpref );
        case smp._prefixmask:
            return new ConfOctetList( s.prefixmask );
        default:
            throw new DpCallbackException("xml tag not handled");
        }
    }



    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
    public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        LOGGER.info("setElem()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
        public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("create()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.REMOVE)
        public int remove(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("remove()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.NUM_INSTANCES)
        public int numInstances(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("numInstances()");
        return SimpleWithTrans.numServers();
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_OBJECT)
        public ConfXMLParam[] getObject(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("getObject() returning ConfXMLParam");
        LOGGER.info(kp);
        String name = ((ConfKey) kp[0]).elementAt(0).toString();
        Server s = SimpleWithTrans.findServer( name );
        if (s == null) return null; /* not found */

        smp cs = new smp();

        ConfXMLParam[] xmlparams = new ConfXMLParam[] {
            new ConfXMLParam(cs, "ip", new ConfIPv4(s.addr)),
            new ConfXMLParam(cs, "port", new ConfUInt16(s.port)),
            new ConfXMLParam(cs, "macaddr", new ConfHexList(s.macaddr )),
            new ConfXMLParam(cs, "snmpref", new ConfOID(s.snmpref)),
            new ConfXMLParam(cs, "prefixmask", new ConfOctetList(s.prefixmask))
        };
        return xmlparams;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT_OBJECT)
        public ConfValue[] getObject(DpTrans trans, ConfObject[] kp, Object obj)        throws DpCallbackException {
        Server s = (Server) obj;
        return new ConfValue[] {
            new ConfBuf(s.name),
            new ConfIPv4( s.addr),
            new ConfUInt16( s.port),
            new ConfHexList( s.macaddr ),
            new ConfOID( s.snmpref ),
            new ConfOctetList( s.prefixmask )};
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.EXISTS_OPTIONAL)
        public boolean existsOptional(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        LOGGER.info("existsOptional()");
        return false;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_CASE)
        public ConfTag getCase(DpTrans trans, ConfObject[] kp, ConfTag choice)
        throws DpCallbackException{
        throw new DpCallbackException("getCase() not implemented");
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_CASE)
        public int setCase(DpTrans trans, ConfObject[] kp,
                           ConfTag choice, ConfTag caseval)
        throws DpCallbackException{
        throw new DpCallbackException("setCase() not implemented");
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ATTRS)
        public int getAttrs(DpTrans trans, ConfObject[] kp,
                            List<ConfAttributeValue> attrList)
        throws DpCallbackException {
        List<ConfAttributeValue> theAttList =
            new ArrayList<ConfAttributeValue>();

        if (attrList != null && attrList.size() > 0 &&
            !(attrList.size() == 1 &&
              attrList.get(0).getAttributeType() == null)) {
            for (ConfAttributeValue att : attrList) {
                ConfAttributeType typ = att.getAttributeType();
                if (ConfAttributeType.TAGS.equals(typ) && theTag != null) {
                    theAttList
                        .add(new ConfAttributeValue(ConfAttributeType.TAGS,
                                                    new ConfList(new ConfObject[] {new ConfBuf(theTag)})));
                } else if (ConfAttributeType.ANNOTATION.equals(typ) &&
                           theAnnotation != null) {
                    theAttList
                        .add(new ConfAttributeValue(ConfAttributeType.ANNOTATION, new ConfBuf(theAnnotation)));
                } else if (ConfAttributeType.INACTIVE.equals(typ) &&
                           theInactive != null) {
                    theAttList
                        .add(new ConfAttributeValue(ConfAttributeType.INACTIVE, new ConfBuf(theInactive)));
                }
            }
        } else {
            if (theTag != null) {
                theAttList
                    .add(new ConfAttributeValue(ConfAttributeType.TAGS,
                                                new ConfList(new ConfObject[] {new ConfBuf(theTag)})));
            }
            if (theAnnotation != null) {
                theAttList
                    .add(new ConfAttributeValue(ConfAttributeType.ANNOTATION,
                                                new ConfBuf(theAnnotation)));
            }
            if (theInactive != null) {
                theAttList
                    .add(new ConfAttributeValue(ConfAttributeType.INACTIVE,
                                                new ConfBuf(theInactive)));
            }
        }

        attrList.clear();
        attrList.addAll(theAttList);
        return Conf.REPLY_OK;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ATTR)
        public int setAttr(DpTrans trans, ConfObject[] kp, ConfAttributeValue attr)
        throws DpCallbackException {
        ConfAttributeType typ = attr.getAttributeType();
        if (ConfAttributeType.TAGS.equals(typ)) {
            if (attr.isRemoveValue()) {
                theTag = null;
            } else {
                theTag = attr.getAttributeValue().toString();
            }
        } else if (ConfAttributeType.ANNOTATION.equals(typ)) {
            if (attr.isRemoveValue()) {
                theAnnotation = null;
            } else {
                theAnnotation = attr.getAttributeValue().toString();
            }
        } else if (ConfAttributeType.INACTIVE.equals(typ)) {
            if (attr.isRemoveValue()) {
                theInactive = null;
            } else {
                theInactive = attr.getAttributeValue().toString();
            }
        }

        return Conf.REPLY_OK;
    }

    //    @DataCallback(callPoint="simplecp", callType=DataCBType.MOVE_AFTER)
    //    public ConfKey moveAfter(DpTrans trans, ConfObject[] kp, ConfKey key)
    //    throws DpCallbackException {
    //          throw new DpCallbackException("moveAfter() not implemented");
    //    }


    /**
     * LOGGER.info
     */

}
