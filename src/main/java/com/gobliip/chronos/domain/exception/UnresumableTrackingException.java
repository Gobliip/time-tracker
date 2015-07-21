package com.gobliip.chronos.domain.exception;

import com.gobliip.chronos.server.entities.Tracking.TrackingStatus;

public class UnresumableTrackingException extends Exception {

    private static final long serialVersionUID = 2610267199055380979L;
    private Long trackingId;
    private TrackingStatus currentStatus;

    public UnresumableTrackingException(Long trackingId, TrackingStatus currentStatus) {
        this.trackingId = trackingId;
        this.currentStatus = currentStatus;
    }

    @Override
    public String getMessage() {
        return "Tracking with id: " + trackingId + " with status: " + currentStatus + " is unresumable";
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
