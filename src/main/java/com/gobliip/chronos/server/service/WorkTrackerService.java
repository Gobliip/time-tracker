package com.gobliip.chronos.server.service;

import com.gobliip.chronos.domain.exception.*;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Moment.MomentType;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.entities.WorkPeriod;
import com.gobliip.chronos.server.entities.WorkSession;
import com.gobliip.chronos.server.entities.WorkSession.WorkSessionStatus;
import com.gobliip.chronos.server.service.exception.IllegalMomentTypeExcetion;
import com.gobliip.chronos.server.service.exception.IllegalWorkSessionStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Created by lsamayoa on 19/07/15.
 */
@Service
public class WorkTrackerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkTrackerService.class);

    public static final String QL_FIND_WORKK_SESSION_BY_OWNER_AND_STATUS =
            "SELECT ws " +
            "FROM com.gobliip.chronos.server.entities.WorkSession ws " +
            "WHERE ws.status = :status AND ws.owner = :owner";

    @Autowired
    private TrackingsService trackingsService;

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = false)
    public WorkSession startWorkSession(final String principal,
                                        final Optional<byte[]> attachmentBytes,
                                        final Optional<String> memo) {
        final Optional<WorkSession> session = findOpenWorkSession(principal);
        if (!session.isPresent()) {
            return createNewWorkSession(principal, attachmentBytes, memo);
        }
        final WorkSession workSession = session.get();
        throw new IllegalWorkSessionStateException(workSession, workSession.getStatus());
    }

    protected WorkSession createNewWorkSession(final String principal,
                                               final Optional<byte[]> attachmentBytes,
                                               final Optional<String> memo) {
        final Tracking tracking = trackingsService.createTracking(principal, attachmentBytes, memo);

        final WorkSession session = new WorkSession();
        session.setStatus(WorkSessionStatus.OPEN);
        session.setOwner(principal);
        session.setTracking(tracking);
        session.setKeyboardActionsCount(0);
        session.setMouseActionsCount(0);

        entityManager.persist(session);
        return session;
    }

    public Optional<WorkSession> findOpenWorkSession(final String principal) {
        final TypedQuery<WorkSession> query = entityManager.createQuery(QL_FIND_WORKK_SESSION_BY_OWNER_AND_STATUS, WorkSession.class);
        query.setParameter("owner", principal).setParameter("status", WorkSessionStatus.OPEN);
        try{
            final WorkSession workSession = query.getSingleResult();
            return Optional.of(workSession);
        } catch (NoResultException ex){
            return Optional.empty();
        }

    }

    @Transactional(readOnly = false)
    public WorkSession logWorkToWorkSession(final String principal,
                                            final int mouseActions,
                                            final int keyboardActions,
                                            final Optional<byte[]> attachmentBytes,
                                            final Optional<String> memo) {
        final WorkSession workSession = pollOpenWorkSession(principal);
        final Optional<WorkPeriod> lastPeriod = Optional.ofNullable(workSession.getLastLoggedPeriod());

        final Tracking tracking = workSession.getTracking();
        final Moment lastMoment = tracking.getLastMoment();
        final MomentType lastMomentType = lastMoment.getType();

        if (!lastPeriod.isPresent()) {
            // Assert that the initial moment is a start moment
            if (!MomentType.START.equals(lastMomentType)) {
                LOGGER.error("Error logging work, the initial moment is not a START moment: {}", lastMoment);
                throw new IllegalMomentTypeExcetion(lastMoment, lastMomentType);
            }
        } else {
            if (!(MomentType.HEARTBEAT.equals(lastMomentType) || MomentType.RESUME.equals(lastMomentType))) {
                LOGGER.error("Error logging work, the last moment is not a HEARTBEAT or RESUME moment: {}", lastMoment);
                throw new IllegalMomentTypeExcetion(lastMoment, lastMomentType);
            }
        }

        final Moment logworkHeartBeatMoment = trackingsService.addMoment(principal, tracking, MomentType.HEARTBEAT, attachmentBytes, memo);
        addWorkPeriod(workSession, mouseActions, keyboardActions, lastMoment, logworkHeartBeatMoment, lastPeriod);
        return workSession;
    }


    @Transactional
    public WorkSession pauseWorkSession(final String principal,
                                        final int mouseActions,
                                        final int keyboardActions,
                                        final Optional<byte[]> attachmentBytes,
                                        final Optional<String> memo) throws UnpausableTrackingException {
        final WorkSession workSession = pollOpenWorkSession(principal);

        if (!WorkSessionStatus.OPEN.equals(workSession.getStatus())) {
            LOGGER.error("Error pausing work session work session is in a unpausable state");
            throw new IllegalWorkSessionStateException(workSession, workSession.getStatus());
        }

        final Optional<WorkPeriod> lastPeriod = Optional.ofNullable(workSession.getLastLoggedPeriod());

        final Tracking tracking = workSession.getTracking();
        final Moment lastMoment = tracking.getLastMoment();

        final Moment pauseMoment = trackingsService.pauseTracking(principal, tracking, attachmentBytes, memo).getLastMoment();
        workSession.setStatus(WorkSessionStatus.PAUSED);
        addWorkPeriod(workSession, mouseActions, keyboardActions, lastMoment, pauseMoment, lastPeriod);
        return workSession;
    }

    @Transactional
    public WorkSession resumeWorkSession(final String principal,
                                         final Optional<byte[]> attachmentBytes,
                                         final Optional<String> memo) throws UnresumableTrackingException {
        final WorkSession workSession = pollOpenWorkSession(principal);

        if (!WorkSessionStatus.PAUSED.equals(workSession.getStatus())) {
            LOGGER.error("Error pausing work session work session is in a unresumable state");
            throw new IllegalWorkSessionStateException(workSession, workSession.getStatus());
        }

        workSession.setStatus(WorkSessionStatus.OPEN);
        trackingsService.resumeTracking(principal, workSession.getTracking(), attachmentBytes, memo).getLastMoment();
        return workSession;
    }

    @Transactional
    public WorkSession stopWorkSession(final String principal,
                                       final int mouseActions,
                                       final int keyboardActions,
                                       final Optional<byte[]> attachmentBytes,
                                       final Optional<String> memo) throws UnstopableTrackingException {
        final WorkSession workSession = pollOpenWorkSession(principal);

        if (!WorkSessionStatus.OPEN.equals(workSession.getStatus())) {
            LOGGER.error("Error pausing work session work session is in a unstopable state");
            throw new IllegalWorkSessionStateException(workSession, workSession.getStatus());
        }

        final Optional<WorkPeriod> lastPeriod = Optional.ofNullable(workSession.getLastLoggedPeriod());

        final Tracking tracking = workSession.getTracking();
        final Moment lastMoment = tracking.getLastMoment();

        final Moment stopMoment = trackingsService.stopTracking(principal, tracking, attachmentBytes, memo).getLastMoment();
        workSession.setStatus(WorkSessionStatus.CLOSED);
        addWorkPeriod(workSession, mouseActions, keyboardActions, lastMoment, stopMoment, lastPeriod);
        return workSession;
    }

    public List<WorkPeriod> getPeriods(final String principal,
                                       final Long workSessionId) {
        Assert.hasText(principal);

        final WorkSession workSession = entityManager.find(WorkSession.class, workSessionId);
        if (principal.equals(workSession.getOwner())) return workSession.getLoggedPeriods();

        throw new ResourceNotOwnedException(WorkSession.class, workSessionId, principal);
    }

    protected WorkPeriod addWorkPeriod(final WorkSession workSession,
                                       final int mouseActions,
                                       final int keyboardActions,
                                       final Moment startMoment,
                                       final Moment endMoment) {
        return addWorkPeriod(workSession, mouseActions, keyboardActions, startMoment, endMoment, Optional.<WorkPeriod>empty());
    }

    protected WorkSession pollOpenWorkSession(final String principal) {
        final Optional<WorkSession> session = findOpenWorkSession(principal);
        if (!session.isPresent()) {
            LOGGER.error("Error pausing work session, there is no open work session for user: {}", principal);
            throw new ResourceNotFoundException(principal, WorkSession.class);
        }
        return session.get();
    }

    protected WorkPeriod addWorkPeriod(final WorkSession workSession,
                                       final int mouseActions,
                                       final int keyboardActions,
                                       final Moment startMoment,
                                       final Moment endMoment,
                                       final Optional<WorkPeriod> parent) {
        final WorkPeriod workPeriod = new WorkPeriod();
        workPeriod.setMouseActionsCount(mouseActions);
        workPeriod.setKeyboardActionsCount(keyboardActions);
        workPeriod.setWorkSession(workSession);
        workPeriod.setStart(startMoment);
        workPeriod.setEnd(endMoment);
        if (parent.isPresent()) workPeriod.setParent(parent.get());
        entityManager.persist(workPeriod);

        workSession.setLastLoggedPeriod(workPeriod);
        workSession.setMouseActionsCount(workSession.getMouseActionsCount() + mouseActions);
        workSession.setKeyboardActionsCount(workSession.getKeyboardActionsCount() + keyboardActions);
        return workPeriod;
    }

    public List<Moment> getMoments(String principal, Long workSessionId) {
        Assert.hasText(principal);

        final WorkSession workSession = entityManager.find(WorkSession.class, workSessionId);
        if (!principal.equals(workSession.getOwner())) throw new ResourceNotOwnedException(WorkSession.class, workSessionId, principal);

        return workSession.getTracking().getMoments();
    }
}
