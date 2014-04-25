package com.tailf.test.cdb1;

import java.net.Socket;

import com.tailf.test.cdb1.namespaces.*;

import com.tailf.cdb.Cdb;
import com.tailf.cdb.CdbDBType;
import com.tailf.cdb.CdbPhase;
import com.tailf.cdb.CdbSession;
import com.tailf.cdb.CdbTxId;
import com.tailf.conf.*;
import java.util.Arrays;
import com.tailf.test.cdb1.namespaces.*;
import org.apache.log4j.Logger;


public class Test0 {
    String host = "localhost";
    private static Logger LOGGER =
        Logger.getLogger(Test0.class);

    public static void main(String[] arg) throws Exception {
        //new Test6().test();
        new Test0().test2();

    }

    public void test() throws Exception {
        Socket s = new Socket(host, Conf.PORT);
        Cdb cdb = new Cdb("test",s);

        CdbSession sess= cdb.startSession();
        sess.setNamespace( new mtest());
        sess.endSession();
        s.close();

    }

    public void test1() throws Exception {
        Socket s = new Socket(host, Conf.PORT);
        Cdb cdb = new Cdb("test",s);
        CdbSession sess = cdb.startSession();
        sess.setNamespace( new mtest());
        sess.cd( new ConfPath("/mtest/a_number"));
        s.close();
    }

    public void test2() throws Exception {
          Socket s = new Socket(host, Conf.PORT);
          Cdb cdb = new Cdb("test",s);
          CdbSession sess= cdb.startSession();
          sess.setNamespace( new mtest());
          sess.cd( new ConfPath("/mtest/a_number"));
          String cwd = sess.getcwd();
          LOGGER.info("cwd= "+cwd);

          sess.pushd( new ConfPath("/mtest/servers/server"));
          LOGGER.info("cwd= "+ sess.getcwd());
          sess.popd();
          LOGGER.info("cwd= "+ sess.getcwd());
          try {
              sess.popd();
              LOGGER.info("cwd= "+ sess.getcwd());
              LOGGER.info("popd() on empty stack should have failed");
              System.exit(1);
          } catch (ConfException e) {
              //LOGGER.info("***"+e.getMessage());
              if (!e.getMessage().equals("Path stack empty")) {
                  LOGGER.error("Some error occured");
              }
          }
          s.close();
    }

}