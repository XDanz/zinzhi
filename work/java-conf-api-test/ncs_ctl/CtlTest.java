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

import java.net.Socket;

import com.tailf.conf.Conf;
import com.tailf.dp.Dp;
import com.tailf.dp.DpDataCallback;
import com.tailf.dp.DpTransCallback;
import com.tailf.maapi.Maapi;
import com.tailf.ncs.*;

public class CtlTest {


    /** ------------------------------------------------------------
     *   main
     *
     */
    static public void main(String args[]) {

        LogConfig.setFull();
        try {

            Socket s = new Socket("localhost", Conf.NCS_PORT);
            NcsCtl m = new NcsCtl(s);
            m.addNamespace(new smp());
            while (true) {
                NcsCtlCommand cmd = m.read();
                System.out.println("GOT " + cmd);
                cmd.reply(true, "AAAAA");
            }
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }
}
