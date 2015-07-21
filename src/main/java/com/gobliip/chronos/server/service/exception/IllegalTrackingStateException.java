package com.gobliip.chronos.server.service.exception;

import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.service.MomentsService;

/**
 * Created by lsamayoa on 19/07/15.
 */
public class IllegalTrackingStateException extends RuntimeException {
    private Tracking tracking;
    private String action;

    public IllegalTrackingStateException(Tracking tracking, String action) {
        super("Tracking with Id " + tracking.getId() + " is invalid to take action: " + action);
        this.action = action;
        this.tracking = tracking;
    }

    public IllegalTrackingStateException(Tracking tracking, MomentsService.TrackingAction action) {
        super("Tracking with Id " + tracking.getId() + " is invalid to take action: " + action);
        this.action = action.toString();
        this.tracking = tracking;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }
}
