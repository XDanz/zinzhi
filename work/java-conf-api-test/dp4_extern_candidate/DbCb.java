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
import com.tailf.dp.annotations.DBCallback;
import com.tailf.dp.proto.DBCBType;

public class DbCb {

    public int lock = 0;

    @DBCallback(callType=DBCBType.CANDIDATE_COMMIT)
    public void candidateCommit(DpDbContext dbx, int timeout) throws DpCallbackException {
        trace("candidateCommit():");
        if (timeout != 0) {
            /* we must be prepared to rollback running */
            DbServer.unlink("rollback");
            DbServer.dumpDb(Conf.DB_RUNNING, "rollback");
        }
        /* now copy candidate to running */
        DbServer.candCopy();
    }

    @DBCallback(callType=DBCBType.CANDIDATE_CONFIRMING_COMMIT)
    public void candidateConfirmingCommit(DpDbContext dbx) throws DpCallbackException {
        trace("candidateConfirmingCommit():");
        DbServer.unlink("rollback");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_ROLLBACK_RUNNING)
    public void candidateRollbackRunning(DpDbContext dbx) throws DpCallbackException {
        trace("candidateRollbackRunning():");
        // DbServer.clearDb(Conf.DB_RUNNING);  -- no need
        DbServer.restore(Conf.DB_RUNNING, "rollback");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_RESET)
    public void candidateReset(DpDbContext dbx) throws DpCallbackException {
        trace("candidateReset():");
        DbServer.candidateReset();
    }


    /* has anything been written into the candidate that hasn't yet */
    /* been copied into running trough either an invocation of      */
    /* candidate_commit() callback or invocation in our own CLI of */
    /* copytorunning command */
    @DBCallback(callType=DBCBType.CANDIDATE_CHK_NOT_MODIFIED)
    public void candidateChkNotModified(DpDbContext dbx) throws DpCallbackException {
        trace("candidateChkNotModified():");
        if (DbServer.candidate_modified)
            throw new DpCallbackException("candidate is modified");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_VALIDATE)
    public void candidateValidate(DpDbContext dbx) throws DpCallbackException {
        trace("candidateValidate():");
    }

    @DBCallback(callType=DBCBType.ADD_CHECKPOINT_RUNNING)
    public void addCheckpointRunning(DpDbContext dbx) throws DpCallbackException {
        trace("addCheckpointRunning():");
        DbServer.dumpDb(Conf.DB_RUNNING,"DB_RUNNING.ckp");
    }

    @DBCallback(callType=DBCBType.DEL_CHECKPOINT_RUNNING)
    public void delCheckpointRunning(DpDbContext dbx) throws DpCallbackException {
        trace("delCheckpointRunning():");
        DbServer.unlink("DB_RUNNING.ckp");
    }

    @DBCallback(callType=DBCBType.ACTIVATE_CHECKPOINT_RUNNING)
    public void activateCheckpointRunning(DpDbContext dbx) throws DpCallbackException {
        trace("activateCheckpointRunning():");
        DbServer.restore(Conf.DB_RUNNING,"DB_RUNNING.ckp");
    }

    @DBCallback(callType=DBCBType.COPY_RUNNING_TO_STARTUP)
    public void copyRunningToStartup(DpDbContext dbx) throws DpCallbackException {
        trace("copyRunningToStartup():");
    }

    @DBCallback(callType=DBCBType.LOCK)
    public void lock(DpDbContext dbx, int dbName) throws DpCallbackException {
        trace("lock("+Conf.dbnameToString(dbName)+"):");
        lock = dbName;
    }

    @DBCallback(callType=DBCBType.UNLOCK)
    public void unlock(DpDbContext dbx, int dbName) throws DpCallbackException {
        trace("unlock("+Conf.dbnameToString(dbName)+"):");
        lock = 0;
    }

    @DBCallback(callType=DBCBType.LOCK_PARTIAL)
    public void lockPartial(DpDbContext dbx, int dbName, int lockid, ConfObject[][] kps)
        throws DpCallbackException {
        trace("lockPartial($dbx, "+Conf.dbnameToString(dbName)+",lockid="+lockid+")");
        for (int i=0;i<kps.length;i++) {
            trace("kp["+i+"]: "+ Conf.kpToString(kps[i]));
        }
    }

    @DBCallback(callType=DBCBType.UNLOCK_PARTIAL)
    public void unlockPartial(DpDbContext dbx, int dbName, int lockid)
        throws DpCallbackException {
        trace("unlockPartial($dbx, "+Conf.dbnameToString(dbName)+",lockid="+lockid+")");

    }

    @DBCallback(callType=DBCBType.DELETE_CONFIG)
    public void deleteConfig(DpDbContext dbx, int dbName) throws DpCallbackException {
        trace("deleteConfig("+Conf.dbnameToString(dbName)+"):");
        DbServer.clearDb(dbName);
    }

    public void trace(String str) {
        System.err.println("*DbCb: "+str);
    }

    private void trace(ConfObject[] kp) {
        String s= "";
        for (int i=0;i< kp.length; i++)
            s= s + kp[i].toString() + "/" ;
        trace("kp = "+s);
    }

}
