package com.gobliip.chronos.server.service.exception;

import com.gobliip.chronos.server.entities.Moment;

/**
 * Created by lsamayoa on 20/07/15.
 */
public class IllegalMomentTypeExcetion extends RuntimeException {
    public IllegalMomentTypeExcetion(Moment lastMoment, Moment.MomentType momentType) {
        super();
    }
}
