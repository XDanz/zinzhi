package com.tailf.test.maapi2;

import com.tailf.conf.*;
import com.tailf.dp.*;
import com.tailf.dp.annotations.DBCallback;
import com.tailf.dp.proto.DBCBType;

import com.tailf.test.maapi2.namespaces.*;

import org.apache.log4j.Logger;

public class SimpleDBCb {

    private static final Logger log =
        Logger.getLogger(SimpleDBCb.class);

    @DBCallback(callType=DBCBType.CANDIDATE_COMMIT)
        public void candidateCommit(DpDbContext dbx, int timeout)
        throws DpCallbackException {
        log.debug("dummycall candidateCommit");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_CONFIRMING_COMMIT)
        public void candidateConfirmingCommit(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall candidateConfirmingCommit");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_ROLLBACK_RUNNING)
        public void candidateRollbackRunning(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall candidateRollbackRunning");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_RESET)
        public void candidateReset(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall candidateReset");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_CHK_NOT_MODIFIED)
        public void candidateChkNotModified(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall candidateChkNotModified");
    }

    @DBCallback(callType=DBCBType.CANDIDATE_VALIDATE)
        public void candidateValidate(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall candidateValidate");
    }

    @DBCallback(callType=DBCBType.ADD_CHECKPOINT_RUNNING)
        public void addCheckpointRunning(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall addCheckpointRunning");
    }

    @DBCallback(callType=DBCBType.DEL_CHECKPOINT_RUNNING)
        public void delCheckpointRunning(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall delCheckpointRunning");
    }

    @DBCallback(callType=DBCBType.ACTIVATE_CHECKPOINT_RUNNING)
        public void activateCheckpointRunning(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall activateCheckpointRunning");
    }

    @DBCallback(callType=DBCBType.COPY_RUNNING_TO_STARTUP)
        public void copyRunningToStartup(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall copyRunningToStartup");
    }

    @DBCallback(callType=DBCBType.LOCK)
    public void lock(DpDbContext dbx, int dbname) throws DpCallbackException {
        log.debug("dummycall lock");
    }


    @DBCallback(callType=DBCBType.UNLOCK)
        public void unlock(DpDbContext dbx, int dbname)
        throws DpCallbackException {
        log.debug("dummycall unlock");
    }


    @DBCallback(callType=DBCBType.LOCK_PARTIAL)
        public void lockPartial(DpDbContext dbx, int dbname, int lockid,
                                ConfObject[][] paths)
        throws DpCallbackException {
        log.debug("dummycall lockPartial");
    }


    @DBCallback(callType=DBCBType.UNLOCK_PARTIAL)
        public void unlockPartial(DpDbContext dbx, int dbname, int lockid)
        throws DpCallbackException {
        log.debug("dummycall unlockPartial");
    }


    @DBCallback(callType=DBCBType.DELETE_CONFIG)
        public void deleteConfig(DpDbContext dbx, int dbname)
        throws DpCallbackException {
        log.debug("dummycall deleteConfig");
    }

    @DBCallback(callType=DBCBType.RUNNING_CHK_NOT_MODIFIED)
        public void runningChkNotModified(DpDbContext dbx)
        throws DpCallbackException {
        log.debug("dummycall runningChkNotModified");
    }

}
