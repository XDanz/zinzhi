package com.tailf.controller;

import com.tailf.cdb.CdbTxId;
import com.tailf.conf.ConfIP;
import com.tailf.ha.HaStateType;

public interface  HANode {
	public static final long serialVersionUID = 42L;
    public ConfIP getAddress ();

    public boolean isPreferredMaster();
    
    public int getPort();

    public boolean isLocal();

    /**
     *  Return the name of the node. This is taken from 
     *  /thc:ha-controller/ha-nodes/ha-node/name
     * 
     *  @return Return name of the node
     */
    public String getName();

    /**
     *  Return the ha status of the node.
     *  @return HA Status of the node
     */
    public HaStateType getHaStatus() throws Exception;

    /**
     *  Command this node to be the master. Returns
     *   
     */
    public void beMaster () throws Exception;

    
    /**
     *  Command this node to be the none. Returns
     *   
     */
    public void beNone () throws Exception;
    
    /**
     *  Command this node to be the master. Returns
     */
    public void beSlave ( HANode master ) throws Exception ;

    /**
     *  Return the current transaction ID from CDB.
     *
     *  @return Return the current transaction ID 
     */
    public CdbTxId getTxId() throws Exception;

    /**
     *  Persist the current transaction ID from
     *  CDB to disk.
     * 
     *  @throws IOException if I/O error occured while
     *  writing the transactino ID to disk.
     *
     */
    public void saveTxId() throws Exception;
    
    /**
     * Returns true wether the current transaction ID 
     * differs from the transaction ID from disk.
     *
     * If there is no persist transaction ID on disk
     * the method returns false.
     * 
     * @return true wether there was a difference 
     * between the transaction ID stored in CDB and 
     * transaction ID from disk. false otherwise.
     * 
     * @throws IOException if the read operation failed.
     */
    public boolean txDiff () throws Exception ;

    /**
     * Return true wether the remote node is reachable which means
     * that the remotes acceptor answered for a ping request.
     *
     * @return true wether the remote node is reachable
     */
    public boolean isReachable();

}
