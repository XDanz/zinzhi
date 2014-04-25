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

import java.util.ArrayList;

import com.tailf.conf.*;


/**
 * Implements a simple read line command interface.
 * Runs in a separate thread.
 * Will parse input and send notifications to ConfD.
 */
class CmdReader extends Thread {

    public Notifier notif;
    private notif cs = new notif();

    String namespace() {
        return "http://tail-f.com/test/notif/1.0";
    }

    /**
     * Constructs a new linkUp event
     */
    ConfXMLParam[] notifup_1(int index, int flags1, int flags2) {
        ConfXMLParam[] params = new ConfXMLParam[] {
                        new ConfXMLParamStart(cs, "linkUp"),
                        new ConfXMLParamValue(cs, "ifIndex", new ConfUInt32(index)),
                        new ConfXMLParamStart(cs, "linkProperty"),
                        new ConfXMLParamValue(cs, "flags",new ConfUInt32(flags1)),
                        new ConfXMLParamStop(cs, "linkProperty"),
                        new ConfXMLParamStart(cs, "linkProperty"),
                        new ConfXMLParamValue(cs, "flags",new ConfUInt32(flags2)),
                        new ConfXMLParamStop(cs, "linkProperty"),
                        new ConfXMLParamStop(cs, "linkUp")
        };

        return params;
//      return new ConfXMLTag(namespace(),"linkUp", new ConfXMLTag[] {
//              new ConfXMLTag("ifIndex",new ConfUInt32(index)),
//              new ConfXMLTag("linkProperty", new ConfXMLTag[] {
//                      new ConfXMLTag("flags",new ConfUInt32(flags1))}),
//              new ConfXMLTag("linkProperty", new ConfXMLTag[] {
//                      new ConfXMLTag("flags",new ConfUInt32(flags2))})
//          });
    }


    /**
     * Constructs a new linkUp event with additional args
     */
    ConfXMLParam[] notifup_2(int index,int flags1,int val1,int val2) {
        ConfXMLParam[] params = new ConfXMLParam[] {
                        new ConfXMLParamStart(cs, "linkUp"),
                        new ConfXMLParamValue(cs, "ifIndex", new ConfUInt32(index)),
                        new ConfXMLParamStart(cs, "linkProperty"),
                        new ConfXMLParamLeaf(cs, "newlyAdded"),
                        new ConfXMLParamValue(cs, "flags",new ConfUInt32(flags1)),
                        new ConfXMLParamStart(cs, "extensions"),
                        new ConfXMLParamValue(cs,"name", new ConfUInt32(1)),
                        new ConfXMLParamValue(cs,"value",new ConfUInt32(val1)),
                        new ConfXMLParamStop(cs, "extensions"),
                        new ConfXMLParamStart(cs, "extensions"),
                        new ConfXMLParamValue(cs,"name", new ConfUInt32(2)),
                        new ConfXMLParamValue(cs,"value",new ConfUInt32(val2)),
                        new ConfXMLParamStop(cs, "extensions"),
                        new ConfXMLParamStop(cs, "linkProperty"),
                        new ConfXMLParamStop(cs, "linkUp")
        };
        return params;
//      return new ConfXMLTag(namespace(),"linkUp", new ConfXMLTag[] {
//              new ConfXMLTag("ifIndex",index),
//              new ConfXMLTag("linkProperty", new ConfXMLTag[] {
//                      new ConfXMLTag("newlyAdded"),
//                      new ConfXMLTag("flags",new ConfUInt32(flags1)),
//                      new ConfXMLTag("extensions", new ConfXMLTag[] {
//                              new ConfXMLTag("name", new ConfUInt32(1)),
//                              new ConfXMLTag("value",new ConfUInt32(val1)) }),
//                      new ConfXMLTag("extensions", new ConfXMLTag[] {
//                              new ConfXMLTag("name", new ConfUInt32(2)),
//                              new ConfXMLTag("value", new ConfUInt32(val2)) })
//                  })});
    }


    /**
     * Constructs a new linkDown event
     */
    ConfXMLParam[] notifdown(int index) {
        ConfXMLParam[] params = new ConfXMLParam[] {
                        new ConfXMLParamStart(cs, "linkDown"),
                        new ConfXMLParamValue(cs, "ifIndex", new ConfUInt32(index)),
                        new ConfXMLParamStop(cs, "linkDown")
        };

        return params;
//      return new ConfXMLTag(namespace(),"linkDown", new ConfXMLTag[] {
//              new ConfXMLTag("ifIndex",new ConfUInt32(index))});
    }


    /**
     * Constructor for the command reader.
     * store the stream to send events to,
     * and the class instance which is the logger.
     * In this case we use the Notifier class.
     * (send_notification method will log)
     */
    public CmdReader(Notifier notif,String[] cmdBuf) {
        this.cmdBuf = new ArrayList<String>();
        for (int i=0;i<cmdBuf.length;i++)
            this.cmdBuf.add( cmdBuf[i] );
        this.notif = notif;
    }

    ArrayList<String> cmdBuf;

    public void run() {
        try {
            while(true) {

                String line;
                // Eat cmd buffer first
                if (cmdBuf.size() >0){
                    line = (String) cmdBuf.get(0);
                    cmdBuf.remove(0);
                } else {
                    System.out.print(">");
                    java.io.BufferedReader
                        stdin =
                        new java.io.BufferedReader
                        (new java.io.InputStreamReader(System.in));
                    line = stdin.readLine();
                }
                // System.out.println(line);
                if (line.equals("u")) {
                    System.out.println("-> sending linkUp notification");
                    ConfXMLParam[] event = notifup_1(1,2112,32);
                    notif.send_notification(event);
                    System.out.println("-> flushing");
                    notif.flush();
                } else if (line.equals("i")) {
                    System.out.println("-> sending linkUp notification");
                    ConfXMLParam[] event = notifup_1(2,42,4668);
                    // System.out.println("event: "+event);
                    notif.send_notification(event);
                    System.out.println("-> flushing");
                    notif.flush();
                } else if (line.equals("y")) {
                    System.out.println("-> sending linkUp2 notification");
                    ConfXMLParam[] event = notifup_2(2,42,3,4668);
                    // System.out.println("event: "+event);
                    notif.send_notification(event);
                    System.out.println("-> flushing");
                    notif.flush();
                } else if (line.equals("d")) {
                    System.out.println("-> sending linkDown notification");
                    ConfXMLParam[] event = notifdown(1);
                    // System.out.println("event: "+event);
                    notif.send_notification(event);
                    System.out.println("-> flushing");
                    notif.flush();
                } else if (line.equals("q")) {
                    System.out.println("quit");
                    System.exit(0);
                } else if (line.equals("s")) {
                    System.out.println("sleep");
                    Thread.sleep(2000);
                }  else
                    System.out.println("Unknown character: "+line);

            }
        } catch (Exception e) {
            System.err.println("cmdReader: got exception: "+e);
            e.printStackTrace();

        }
    }
} // end of CmdReader class


