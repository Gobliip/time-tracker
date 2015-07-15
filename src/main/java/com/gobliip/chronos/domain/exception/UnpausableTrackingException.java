package com.gobliip.chronos.domain.exception;

import com.gobliip.chronos.server.entities.Tracking.TrackingStatus;

public class UnpausableTrackingException extends Exception {

	private static final long serialVersionUID = 8341936779233448784L;
	
	
	private Long trackingId;
	private TrackingStatus currentStatus;
	
	public UnpausableTrackingException(Long trackingId, TrackingStatus currentStatus) {
		this.trackingId = trackingId;
		this.currentStatus = currentStatus;
	}
	
	@Override
	public String getMessage() {
		return "Tracking with id: " + trackingId +" with status: "+currentStatus+" is an unpausable.";	
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
