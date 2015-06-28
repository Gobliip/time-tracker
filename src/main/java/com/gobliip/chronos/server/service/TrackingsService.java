package com.gobliip.chronos.server.service;

import java.time.Instant;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobliip.chronos.domain.exception.ResourceNotFoundException;
import com.gobliip.chronos.domain.exception.ResourceNotOwnedException;
import com.gobliip.chronos.domain.exception.UnpausableTrackingException;
import com.gobliip.chronos.domain.exception.UnresumableTrackingException;
import com.gobliip.chronos.domain.exception.UnstopableTrackingException;
import com.gobliip.chronos.server.audit.ResourceAudit;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Moment.MomentType;
import com.gobliip.chronos.server.entities.MomentsRepository;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.entities.Tracking.TrackingStatus;
import com.gobliip.chronos.server.entities.TrackingsRepository;

@Service
public class TrackingsService {

//	private static final Logger logger = LoggerFactory
//			.getLogger(TrackingsService.class);

	@Autowired
	private Validator validator;
	
	@Autowired
	private MomentsRepository momentsRepo;

	@Autowired
	private TrackingsRepository trackingRepo;

	@Autowired
	private AuditEventRepository auditEventRepo;

	@Secured("ROLE_USER")
	@Transactional(readOnly = false)
	public Tracking createTracking(String ownerName) {
		Tracking tracking = new Tracking();
		tracking.setStatus(TrackingStatus.RUNNING);
		tracking.setStart(Instant.now());
		tracking.setOwner(ownerName);
		
		Moment moment = new Moment(MomentType.START, tracking);
		tracking.getMoments().add(moment);

		return trackingRepo.save(tracking);
	}
	
	@ResourceAudit
	@Secured("ROLE_USER")
	@Transactional(readOnly = false)
	public Tracking pauseTracking(String userName, Long trackingId) throws UnpausableTrackingException{
		Tracking tracking = findTracking(userName, trackingId);
		
		// Check it is an valid status
		TrackingStatus status = tracking.getStatus();
		if(!status.equals(TrackingStatus.RUNNING)){
			throw new UnpausableTrackingException(trackingId, tracking.getStatus());
		}
		
		Moment moment = new Moment(MomentType.PAUSE, tracking);
		momentsRepo.save(moment);
		
		tracking.setStatus(TrackingStatus.PAUSED);
		tracking.setEnd(Instant.now());
		return trackingRepo.save(tracking);

	}
	
	@ResourceAudit
	@Secured("ROLE_USER")
	@Transactional(readOnly = false)
	public Tracking resumeTracking(String userName, Long trackingId) throws UnresumableTrackingException{
		Tracking tracking = findTracking(userName, trackingId);
		
		// Check it is an valid status
		TrackingStatus status = tracking.getStatus();
		if(!status.equals(TrackingStatus.PAUSED)){
			throw new UnresumableTrackingException(trackingId, tracking.getStatus());
		}
		
		Moment moment = new Moment(MomentType.RESUME, tracking);
		momentsRepo.save(moment);
		
		tracking.setStatus(TrackingStatus.RUNNING);
		tracking.setEnd(Instant.now());
		return trackingRepo.save(tracking);
	}

	@ResourceAudit
	@Secured("ROLE_USER")
	@Transactional(readOnly = false)
	public Tracking stopTracking(String userName, Long trackingId) throws UnstopableTrackingException{
		Tracking tracking = findTracking(userName, trackingId);
		
		// Check it is an stopable status
		TrackingStatus status = tracking.getStatus();
		if(!status.equals(TrackingStatus.PAUSED) && !status.equals(TrackingStatus.RUNNING)){
			throw new UnstopableTrackingException(trackingId, tracking.getStatus());
		}
		
		// Stop tracking now
		Moment stopMoment = new Moment(MomentType.STOP, tracking);
		momentsRepo.save(stopMoment);
		
		tracking.setStatus(TrackingStatus.STOPPED);
		tracking.setEnd(Instant.now());
		return trackingRepo.save(tracking);
	}

	private Tracking findTracking(String userName, Long trackingId)
			throws ResourceNotFoundException, ResourceNotOwnedException {
		Tracking tracking = trackingRepo.findOne(trackingId);
		
		if(tracking == null){
			throw new ResourceNotFoundException(trackingId, Tracking.class);
		}

		if (!tracking.getOwner().equals(userName)) {
			throw new ResourceNotOwnedException("TRACKING", trackingId, userName);
		}
		return tracking;
	}
}
