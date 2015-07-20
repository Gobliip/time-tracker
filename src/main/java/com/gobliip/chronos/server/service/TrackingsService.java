package com.gobliip.chronos.server.service;

import com.gobliip.chronos.domain.exception.*;
import com.gobliip.chronos.server.audit.ResourceAudit;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Moment.MomentType;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.entities.Tracking.TrackingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;

@Service
public class TrackingsService {

    @Autowired
    private EntityManager entityManager;

    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking createTracking(String ownerName) {
        final Tracking tracking = new Tracking();
        tracking.setStatus(TrackingStatus.RUNNING);
        tracking.setStart(Instant.now());
        tracking.setOwner(ownerName);

        final Moment moment = new Moment(MomentType.START);
        tracking.addMoment(moment);

        entityManager.persist(tracking);

        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking pauseTracking(String userName, Long trackingId) throws UnpausableTrackingException {
        final Tracking tracking = findTracking(userName, trackingId);

        // Check it is an valid status
        if (!TrackingStatus.RUNNING.equals(tracking.getStatus())) {
            throw new UnpausableTrackingException(trackingId, tracking.getStatus());
        }

        final Moment moment = new Moment(MomentType.PAUSE, tracking);
        entityManager.persist(moment);

        tracking.setLastMoment(moment);
        tracking.setStatus(TrackingStatus.PAUSED);
        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking resumeTracking(String userName, Long trackingId) throws UnresumableTrackingException {
        final Tracking tracking = findTracking(userName, trackingId);

        // Check it is an valid status
        if (!TrackingStatus.PAUSED.equals(tracking.getStatus())) {
            throw new UnresumableTrackingException(trackingId, tracking.getStatus());
        }

        final Moment moment = new Moment(MomentType.RESUME, tracking);
        entityManager.persist(moment);

        tracking.setLastMoment(moment);
        tracking.setStatus(TrackingStatus.RUNNING);

        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking stopTracking(String userName, Long trackingId) throws UnstopableTrackingException {
        Tracking tracking = findTracking(userName, trackingId);

        // Check it is an stopable status
        final TrackingStatus status = tracking.getStatus();
        if (!TrackingStatus.PAUSED.equals(status) && !TrackingStatus.RUNNING.equals(status)) {
            throw new UnstopableTrackingException(trackingId, tracking.getStatus());
        }

        // Stop tracking now
        final Moment stopMoment = new Moment(MomentType.STOP, tracking);
        entityManager.persist(stopMoment);

        tracking.setLastMoment(stopMoment);
        tracking.setStatus(TrackingStatus.STOPPED);
        tracking.setEnd(Instant.now());
        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    public Tracking findTracking(String userName, Long trackingId)
            throws ResourceNotFoundException, ResourceNotOwnedException {
        final Tracking tracking = entityManager.find(Tracking.class, trackingId);

        if (tracking == null) {
            throw new ResourceNotFoundException(trackingId, Tracking.class);
        }

        if (!tracking.getOwner().equals(userName)) {
            throw new ResourceNotOwnedException("Tracking", trackingId, userName);
        }
        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    public List<Moment> findMoments(String userName, Long trackingId) {
        // We check user owns the tracking using the findTracking
        final Tracking tracking = findTracking(userName, trackingId);
        return tracking.getMoments();
    }
}
