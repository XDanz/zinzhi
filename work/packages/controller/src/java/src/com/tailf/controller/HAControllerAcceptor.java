package com.tailf.controller;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.tailf.cdb.CdbTxId;
import com.tailf.ha.HaStateType;

public class HAControllerAcceptor {
    private static final Logger log =
        Logger.getLogger ( HAControllerAcceptor.class );

    private ServerSocket server = null;
    private static ExecutorService pool =
        Executors.newCachedThreadPool();

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
                            acceptor.acceptHARemoteConnections();
                            
                        } catch ( Exception e ) {
                            log.error("",e );
                            acceptor.executorService().shutdownNow();
                        }
                    }
                }
                );
        }
        
    }
    // Handler of incoming request from the remote 
    // HAController NODE.
    class RequestHandler implements Runnable {
        private final Socket clientSock;
        RequestHandler ( Socket clientSock ) { this.clientSock = clientSock; }

        public void run () {
            try {
                log.debug(" incomming request from " + clientSock );
                HAControllerConnector cn =
                    HAControllerConnector.connector ( clientSock );

                String req = (String)cn.recv();
                Object resp = null;
                log.debug (" req:" + req );

                HAController ctrl = HAController.getController();
                HALocalNode node = (HALocalNode)ctrl.getLocalHANode();

                if (req.equals("eventtxid")) {
                    CdbTxId txid = node.getEventTxId();
                    resp = txid;
                } else if (req.equals ("txid")) {
                    CdbTxId txid = node.getTxId();
                    resp = txid;
                } else if (req.equals ("status")) {

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
                    default:
                        break;
                    }
                } else if (req.equals ("ping")) {
                    resp = "pong";
                } else if (req.equals ("slave")) {
                    ctrl.getLocalHANode().beSlave( ctrl.getRemoteHANode() );
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
        log.info ( " acceptor = " + acceptor );
        if ( acceptor != null && acceptor.getServerSocket() != null ) {
            log.info (" closing Server Socket!");
            acceptor.getServerSocket().close();
            log.info (" closing Server Socket! ok");
        }

        if ( pool != null ) {
            log.info (" pool shuting down =>");
            pool.shutdownNow();
            log.info (" pool shuting down => ok");
        }

    }

    public void acceptHARemoteConnections () {
        try {
            HAController controller = HAController.getController();
            int port = controller.getLocalHANode().getPort();
            InetAddress addr = controller.getLocalHANode()
                .getAddress().getAddress();

            ServerSocket srvSock = new ServerSocket ();
            this.server = srvSock;
            srvSock.setReuseAddress(true);
            srvSock.bind(
                         new InetSocketAddress (addr , port));

            log.info ( "  XXX Acceptor Started! XXX ");
            log.info(" sock:" + srvSock );
            for ( ;; ) {
                pool.execute ( new RequestHandler ( srvSock.accept()) );
            }
        } catch ( HAControllerDeterminationException e ) {
            log.error (" Please Configure /thc:ha-nodes/ha-node properly!");
            log.warn ( " HAControllerAcceptor Not running ");
           
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
        return pool;
    }
}
