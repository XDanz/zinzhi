package com.tailf.controller;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.net.ServerSocket;
import java.net.Socket;
import com.tailf.cdb.CdbTxId;
import com.tailf.ha.HaStateType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class HAControllerAcceptor {
    private static final Logger log =
        Logger.getLogger ( HAControllerAcceptor.class );

    private ServerSocket server = null;
    private static ExecutorService pool =
        Executors.newCachedThreadPool();

    private int port;
    private static HAControllerAcceptor acceptor;

    protected ServerSocket getServerSocket() {
        return server;
    }

    public static void execute () {
        if ( acceptor == null ) {
            pool.execute (  new Runnable () {
                    public void run () {
                        try {
                            acceptor =
                                new HAControllerAcceptor ( );
                        } catch ( Exception e ) {
                            log.error("",e );
                            acceptor.executorService().shutdownNow();
                        }
                    }
                }
                );
        }
    }

    class RequestHandler implements Runnable {
        private final Socket clientSock;
        RequestHandler ( Socket clientSock ) { this.clientSock = clientSock; }

        public void run () {
            try {
                HAControllerConnector cn =
                    HAControllerConnector.connector ( clientSock );

                String req = (String)cn.recv();
                Object resp = null;

                HAController ctrl = HAController.getController();
                HALocalNode node = (HALocalNode)ctrl.getLocalHANode();

                if ( req.equals("eventtxid")) {
                    CdbTxId txid = node.getEventTxId();
                    resp = txid;
                } else if ( req.equals ("txid") ) {
                    CdbTxId txid = node.getTxId();
                    resp = txid;
                } else if ( req.equals ("status") ) {
                    HaStateType type = node.getHaStatus();

                    switch ( type ) {
                    case NONE:
                        resp = "none";
                        break;
                    case MASTER:
                        resp = "master";
                        break;
                    case SLAVE:
                        resp = "slave";
                        break;
                    }
                } else if ( req.equals ("ping") ) {
                    resp = "pong";
                }
                cn.send (resp);
                cn.close ();
            } catch ( Exception e ) {
                log.error("",e );
            } finally {

            }
        }

    }

    public static void stopListening() throws Exception {
        if ( pool != null && pool.isShutdown() ) {
            pool.shutdownNow();
        }
        
        if ( acceptor != null && acceptor.getServerSocket() != null ) {
            acceptor.getServerSocket().close();
        }
    }

    public HAControllerAcceptor () {
        try {
            HAController controller = HAController.getController();
            int port = controller.getLocalHANode().getPort();
            InetAddress addr = controller.getLocalHANode()
                .getAddress().getAddress();

            ServerSocket srvSock = new ServerSocket ();
            srvSock.setReuseAddress(true);
            srvSock.bind(
                         new InetSocketAddress (addr , port));

            log.info ( "  XXX Acceptor Started! XXX ");
            log.info(" sock:" + srvSock );
            for ( ;; ) {
                this.pool.execute ( new RequestHandler ( srvSock.accept()) );
            }

        } catch (Throwable e) {
            log.error("",e);

        } finally {
            try {
		server.close();
            } catch (Exception e) {
		// do nothing - server failed
            }
        }
    }

    public ExecutorService executorService() {
        return this.pool;
    }
}
