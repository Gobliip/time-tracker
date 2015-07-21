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
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TrackingsService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MomentsService momentsService;

    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking createTracking(final String principal,
                                   final Optional<byte[]> attachmentBytes,
                                   final Optional<String> memo) {
        final Tracking tracking = new Tracking();
        tracking.setStatus(TrackingStatus.RUNNING);
        tracking.setStart(Instant.now());
        tracking.setOwner(principal);

        entityManager.persist(tracking);

        addMoment(principal, tracking, MomentType.START, attachmentBytes, memo);

        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking pauseTracking(final String principal,
                                  final Long trackingId,
                                  final Optional<byte[]> attachmentBytes,
                                  final Optional<String> memo) throws UnpausableTrackingException {
        final Tracking tracking = findTracking(principal, trackingId);
        return pauseTracking(principal, tracking, attachmentBytes, memo);
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking pauseTracking(final String principal,
                                  final Tracking tracking,
                                  final Optional<byte[]> attachmentBytes,
                                  final Optional<String> memo) throws UnpausableTrackingException {
        Assert.hasText(principal);
        Assert.state(principal.equals(tracking.getOwner()));

        // Check it is an valid status
        if (!TrackingStatus.RUNNING.equals(tracking.getStatus())) {
            throw new UnpausableTrackingException(tracking.getId(), tracking.getStatus());
        }

        addMoment(principal, tracking, MomentType.PAUSE, attachmentBytes, memo);
        tracking.setStatus(TrackingStatus.PAUSED);

        return tracking;
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking resumeTracking(final String principal,
                                   final Long trackingId,
                                   final Optional<byte[]> attachmentBytes,
                                   final Optional<String> memo) throws UnresumableTrackingException {
        final Tracking tracking = findTracking(principal, trackingId);
        return resumeTracking(principal, tracking, attachmentBytes, memo);
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking resumeTracking(final String principal,
                                   final Tracking tracking,
                                   final Optional<byte[]> attachmentBytes,
                                   final Optional<String> memo) throws UnresumableTrackingException {
        assertOwnership(principal, tracking);

        // Check it is an valid status
        if (!TrackingStatus.PAUSED.equals(tracking.getStatus())) {
            throw new UnresumableTrackingException(tracking.getId(), tracking.getStatus());
        }

        addMoment(principal, tracking, MomentType.RESUME, attachmentBytes, memo);
        tracking.setStatus(TrackingStatus.RUNNING);

        return tracking;
    }

    protected static void assertOwnership(final String principal, final Tracking tracking){
        Assert.hasText(principal);
        Assert.state(principal.equals(tracking.getOwner()));
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking stopTracking(final String principal,
                                 final Long trackingId,
                                 final Optional<byte[]> attachmentBytes,
                                 final Optional<String> memo) throws UnstopableTrackingException {
        final Tracking tracking = findTracking(principal, trackingId);
        return stopTracking(principal, tracking, attachmentBytes, memo);
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Tracking stopTracking(final String principal,
                                 final Tracking tracking,
                                 final Optional<byte[]> attachmentBytes,
                                 final Optional<String> memo) throws UnstopableTrackingException {

        assertOwnership(principal, tracking);

        // Check it is an stopable status
        final TrackingStatus status = tracking.getStatus();
        if (!TrackingStatus.PAUSED.equals(status) && !TrackingStatus.RUNNING.equals(status)) {
            throw new UnstopableTrackingException(tracking.getId(), tracking.getStatus());
        }

        addMoment(principal, tracking, MomentType.STOP, attachmentBytes, memo);
        tracking.setStatus(TrackingStatus.STOPPED);
        tracking.setEnd(Instant.now());

        return tracking;
    }


    @ResourceAudit
    @Secured("ROLE_USER")
    public Tracking doHeartbeat(String principal, Long trackingId, Optional<byte[]> attachmentBytes, Optional<String> memo) {
        return addMoment(principal, trackingId, MomentType.HEARTBEAT, attachmentBytes, memo);
    }

    @Deprecated
    @ResourceAudit
    @Secured("ROLE_USER")
    public Tracking addMemo(String principal, Long trackingId, Optional<byte[]> attachmentBytes, Optional<String> memo) {
        return addMoment(principal, trackingId, MomentType.HEARTBEAT, attachmentBytes, memo);
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    public List<Moment> findMoments(final String userName, final Long trackingId) {
        // We check user owns the tracking using the findTracking
        final Tracking tracking = findTracking(userName, trackingId);
        return tracking.getMoments();
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

    protected Tracking addMoment(String principal, Long trackingId, MomentType momentType, Optional<byte[]> attachmentBytes, Optional<String> memo) {
        final Tracking tracking = findTracking(principal, trackingId);
        addMoment(principal, tracking, momentType, attachmentBytes, memo);
        return tracking;
    }

    protected Moment addMoment(String principal, Tracking tracking, MomentType momentType, Optional<byte[]> attachmentBytes, Optional<String> memo) {
        Assert.notNull(principal);
        Assert.state(principal.equals(tracking.getOwner()));

        final Moment moment = momentsService.createMoment(principal, tracking, momentType, attachmentBytes, memo);
        entityManager.persist(moment);
        tracking.getMoments().add(moment);
        tracking.setLastMoment(moment);
        return moment;
    }


}
