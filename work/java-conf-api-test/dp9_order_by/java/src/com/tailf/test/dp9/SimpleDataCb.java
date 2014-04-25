package com.tailf.test.dp9;

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
import com.tailf.dp.annotations.DataCallback;
import com.tailf.dp.proto.DataCBType;
import com.tailf.test.dp9.namespaces.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class SimpleDataCb  {

    private String theTag = "DumbTag";
    private String theAnnotation = "DumbAnnotation";
    private String theInactive = "DumbInactive";

    public String received_opaque = null;

    @DataCallback(callPoint="simplecp", callType=DataCBType.ITERATOR)
        public Iterator<Object> iterator(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        return SimpleWithTrans.iterator();
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT)
        public ConfKey getKey(DpTrans trans, ConfObject[] kp, Object obj)
        throws DpCallbackException {
        //      trace("getKey()");
        Server s = (Server) obj;
        return new ConfKey( new ConfObject[] { new ConfBuf(s.name) });
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_ELEM)
        public ConfValue getElem(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        //      trace("getElem()");
        try {
            if (received_opaque == null) {
                received_opaque = trans.getOpaque();
                File f = new File("opaque.prop");
                Properties testprop = new Properties();

                if (f.exists()) {
                    testprop.load(new FileInputStream(f));
                }
                testprop.put("DATA", received_opaque);
                FileOutputStream fw = new FileOutputStream(f);
                testprop.save(fw, "opaque test props");
                fw.close();
            }
        } catch (Exception e) {
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
        default:
            throw new DpCallbackException("xml tag not handled");
        }

    }



    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ELEM)
        public int setElem(DpTrans trans, ConfObject[] kp, ConfValue newval)
        throws DpCallbackException {
        //      trace("setElem()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.CREATE)
    public int create(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
//      trace("create()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.REMOVE)
        public int remove(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        //      trace("remove()");
        return Conf.REPLY_ACCUMULATE;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.NUM_INSTANCES)
    public int numInstances(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        //      trace("numInstances()");
        return SimpleWithTrans.numServers();
    }


    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_OBJECT)
    public ConfValue[] getObject(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        //      trace("getObject()");
        //      trace(kp);
        String name = ((ConfKey) kp[0]).elementAt(0).toString();
        Server s = SimpleWithTrans.findServer( name );
        if (s == null) return null; /* not found */
        return getObject(trans, kp, s);
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.GET_NEXT_OBJECT)
        public ConfValue[] getObject(DpTrans trans, ConfObject[] kp,
                                     Object obj)
        throws DpCallbackException {
        Server s = (Server) obj;
        return new ConfValue[] {
            new ConfBuf(s.name),
            new ConfIPv4( s.addr),
            new ConfUInt16( s.port)};
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.EXISTS_OPTIONAL)
    public boolean existsOptional(DpTrans trans, ConfObject[] kp)
        throws DpCallbackException {
        //      trace("existsOptional()");
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
            !(attrList.size() == 1 && attrList.get(0)
              .getAttributeType() == null)) {
            for (ConfAttributeValue att : attrList) {
                ConfAttributeType typ = att.getAttributeType();
                if (ConfAttributeType.TAGS.equals(typ) && theTag != null) {
                    theAttList.add(new ConfAttributeValue(ConfAttributeType.TAGS, new ConfList(new ConfObject[] {new ConfBuf(theTag)})));
                } else if (ConfAttributeType.ANNOTATION.equals(typ) &&
                           theAnnotation != null) {
                    theAttList.add(new ConfAttributeValue(ConfAttributeType.ANNOTATION, new ConfBuf(theAnnotation)));
                } else if (ConfAttributeType.INACTIVE.equals(typ) && theInactive != null) {
                    theAttList.add(new ConfAttributeValue(ConfAttributeType.INACTIVE, new ConfBuf(theInactive)));
                }
            }
        } else {
            if (theTag != null) {
                theAttList.add(new ConfAttributeValue(ConfAttributeType.TAGS, new ConfList(new ConfObject[] {new ConfBuf(theTag)})));
            }
            if (theAnnotation != null) {
                theAttList.add(new ConfAttributeValue(ConfAttributeType.ANNOTATION, new ConfBuf(theAnnotation)));
            }
            if (theInactive != null) {
                theAttList.add(new ConfAttributeValue(ConfAttributeType.INACTIVE, new ConfBuf(theInactive)));
            }
        }

        attrList.clear();
        attrList.addAll(theAttList);
        return Conf.REPLY_OK;
    }

    @DataCallback(callPoint="simplecp", callType=DataCBType.SET_ATTR)
        public int setAttr(DpTrans trans, ConfObject[] kp,
                           ConfAttributeValue attr)
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

    @DataCallback(callPoint="simplecp", callType=DataCBType.MOVE_AFTER)
        public int moveAfter(DpTrans trans, ConfObject[] kp, ConfKey prevkey)
        throws DpCallbackException {

        String name = ((ConfKey) kp[0]).elementAt(0).toString();
        int from = SimpleWithTrans.findIndex( name );

        int to = -1;
        if (prevkey != null) {
            String prevname = prevkey.elementAt(0).toString();
            to = SimpleWithTrans.findIndex(prevname);
        }

        SimpleWithTrans.move(from, to);

        return Conf.REPLY_OK;
    }


}
