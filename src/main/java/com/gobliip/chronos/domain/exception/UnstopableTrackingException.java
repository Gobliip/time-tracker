package com.gobliip.chronos.domain.exception;

import com.gobliip.chronos.server.entities.Tracking.TrackingStatus;


public class UnstopableTrackingException extends Exception {

    private static final long serialVersionUID = 507934711869725618L;

    private Long trackingId;
    private TrackingStatus currentStatus;

    public UnstopableTrackingException(Long trackingId, TrackingStatus currentStatus) {
        this.trackingId = trackingId;
        this.currentStatus = currentStatus;
    }

    @Override
    public String getMessage() {
        return "Tracking with id: " + trackingId + " with status: " + currentStatus + " is unstopable";
    }

    public Long getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Long trackingId) {
        this.trackingId = trackingId;
    }

    public TrackingStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(TrackingStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

}
