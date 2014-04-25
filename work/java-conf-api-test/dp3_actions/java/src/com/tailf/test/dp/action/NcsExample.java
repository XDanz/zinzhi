package com.tailf.test.dp.action;


import com.tailf.conf.Conf;
import com.tailf.conf.ConfException;
import com.tailf.dp.Dp;
import com.tailf.dp.DpException;
import com.tailf.test.dp.action.namespaces.*;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;


class NcsExample {

    /**
     * @param args command line arguments.
     */
    private static Logger LOG =
        Logger.getLogger("NcsExample");

    public static void main(String[] args) {
        LOG.info("==> main");
        System.out.println("Hello NcsExample");
        try {
            // init and connect the control socket
            Socket ctrlSocket = new Socket("127.0.0.1", Conf.NCS_PORT);
            Dp dp = new Dp("NcsExample", ctrlSocket);
            // register the callbacks
            dp.registerAnnotatedCallbacks(new MathAction());
            dp.registerDone();
            dp.addNamespace(new mathrpc());
            while (true) dp.read();
        } catch (DpException e) {
            e.printStackTrace();
        } catch (ConfException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("<== main");
    }

}