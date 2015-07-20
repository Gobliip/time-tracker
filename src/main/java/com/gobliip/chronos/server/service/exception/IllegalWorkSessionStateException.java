package com.gobliip.chronos.server.service.exception;

import com.gobliip.chronos.server.entities.WorkSession;

/**
 * Created by lsamayoa on 20/07/15.
 */
public class IllegalWorkSessionStateException extends RuntimeException {
    public IllegalWorkSessionStateException(WorkSession workSession, WorkSession.WorkSessionStatus status) {
        super();
    }
}
