package com.gobliip.chronos.server.service.exception;

import com.gobliip.chronos.server.entities.WorkSession;

/**
 * Created by lsamayoa on 20/07/15.
 */
public class WorkSessionAlreadyInProgressException extends RuntimeException {
    private WorkSession workSession;
    public WorkSessionAlreadyInProgressException(WorkSession workSession) {
        super("Work session already in progress, can not start a new work session");
        this.workSession = workSession;
    }
}
