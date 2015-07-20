package com.gobliip.chronos.server.service;

import com.gobliip.chronos.domain.exception.*;
import com.gobliip.chronos.server.audit.ResourceAudit;
import com.gobliip.chronos.server.entities.Attachment;
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

    @Autowired
    private MomentsService momentsService;

    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking createTracking(final String ownerName,
                                   final byte[] attachmentBytes,
                                   final String memo) {
        final Tracking tracking = new Tracking();
        tracking.setStatus(TrackingStatus.RUNNING);
        tracking.setStart(Instant.now());
        tracking.setOwner(ownerName);

        entityManager.persist(tracking);

        final Moment moment = momentsService.createMoment(ownerName, tracking, MomentType.START, attachmentBytes, memo);
        tracking.setLastMoment(moment);

        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking pauseTracking(final String userName,
                                  final Long trackingId,
                                  final byte[] attachmentBytes,
                                  final String memo) throws UnpausableTrackingException {
        final Tracking tracking = findTracking(userName, trackingId);

        // Check it is an valid status
        if (!TrackingStatus.RUNNING.equals(tracking.getStatus())) {
            throw new UnpausableTrackingException(trackingId, tracking.getStatus());
        }

        final Moment moment = momentsService.createMoment(userName, tracking, MomentType.PAUSE, attachmentBytes, memo);
        entityManager.persist(moment);

        tracking.setLastMoment(moment);
        tracking.setStatus(TrackingStatus.PAUSED);
        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking resumeTracking(final String userName,
                                   Long trackingId,
                                   final byte[] attachmentBytes,
                                   final String memo) throws UnresumableTrackingException {
        final Tracking tracking = findTracking(userName, trackingId);

        // Check it is an valid status
        if (!TrackingStatus.PAUSED.equals(tracking.getStatus())) {
            throw new UnresumableTrackingException(trackingId, tracking.getStatus());
        }

        final Moment moment = momentsService.createMoment(userName, tracking, MomentType.RESUME, attachmentBytes, memo);
        entityManager.persist(moment);

        tracking.setLastMoment(moment);
        tracking.setStatus(TrackingStatus.RUNNING);

        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking stopTracking(final String userName,
                                 final Long trackingId,
                                 final byte[] attachmentBytes,
                                 final String memo) throws UnstopableTrackingException {
        final Tracking tracking = findTracking(userName, trackingId);

        // Check it is an stopable status
        final TrackingStatus status = tracking.getStatus();
        if (!TrackingStatus.PAUSED.equals(status) && !TrackingStatus.RUNNING.equals(status)) {
            throw new UnstopableTrackingException(trackingId, tracking.getStatus());
        }

        // Stop tracking now
        final Moment moment = momentsService.createMoment(userName, tracking, MomentType.STOP, attachmentBytes, memo);
        entityManager.persist(moment);

        tracking.setLastMoment(moment);
        tracking.setStatus(TrackingStatus.STOPPED);
        tracking.setEnd(Instant.now());
        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    public Tracking findTracking(final String userName, final Long trackingId)
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
    public List<Moment> findMoments(final String userName, final Long trackingId) {
        // We check user owns the tracking using the findTracking
        final Tracking tracking = findTracking(userName, trackingId);
        return tracking.getMoments();
    }
}
