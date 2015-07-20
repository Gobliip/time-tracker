package com.gobliip.chronos.server.service;

import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.entities.Moment.MomentType;
import com.gobliip.chronos.server.entities.WorkPeriod;
import com.gobliip.chronos.server.entities.WorkSession;
import com.gobliip.chronos.server.entities.WorkSession.WorkSessionStatus;
import com.gobliip.chronos.server.service.exception.IllegalMomentTypeExcetion;
import com.gobliip.chronos.server.service.exception.IllegalWorkSessionStateException;
import com.gobliip.chronos.server.service.exception.WorkSessionAlreadyInProgressException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;

/**
 * Created by lsamayoa on 19/07/15.
 */
public class WorkTrackingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkTrackingService.class);

    public static final String QL_FIND_WORKK_SESSION_BY_OWNER_AND_STATUS = "SELECT ws FROM WorkSession ws WHERE ws.status = :status AND ws.owner = :owner";
    @Autowired
    private TrackingsService trackingsService;

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = false)
    public Optional<WorkSession> startWorkSession(String principal, Optional<byte[]> attachmentBytes, Optional<String> memo){
        Optional<WorkSession> session = findOpenWorkSession(principal);
        if(!session.isPresent()){
            return Optional.of(createNewWorkSession(principal, attachmentBytes, memo));
        }
        throw new WorkSessionAlreadyInProgressException(session.get());
    }

    protected WorkSession createNewWorkSession(String principal, Optional<byte[]> attachmentBytes, Optional<String> memo) {
        final Tracking tracking = trackingsService.createTracking(principal, attachmentBytes, memo);

        final WorkSession session =  new WorkSession();
        session.setStatus(WorkSessionStatus.OPEN);
        session.setOwner(principal);
        session.setTracking(tracking);
        session.setKeyboardActionsCount(0);
        session.setMouseActionsCount(0);

        entityManager.persist(session);
        return session;
    }

    public Optional<WorkSession> findOpenWorkSession(String principal) {
        final TypedQuery<WorkSession> query = entityManager.createQuery(QL_FIND_WORKK_SESSION_BY_OWNER_AND_STATUS, WorkSession.class);
        query.setParameter("owner", principal).setParameter("status", WorkSessionStatus.OPEN);
        final WorkSession workSession = query.getSingleResult();
        return Optional.ofNullable(workSession);
    }

    @Transactional(readOnly = false)
    public WorkPeriod logWorkToWorkSession(final String principal,
                                     final int mouseActions,
                                     final int keyboardActions,
                                     final Optional<byte[]> attachmentBytes,
                                     final Optional<String> memo){
        final Optional<WorkSession> session = findOpenWorkSession(principal);
        if(!session.isPresent()){
            LOGGER.error("Error logging work, there is no open work session for user: {}", principal);
            throw new IllegalWorkSessionStateException(session.get(), session.get().getStatus());
        }
        final WorkSession workSession = session.get();
        final WorkPeriod lastLoggedPeriod = workSession.getLastLoggedPeriod();

        final WorkPeriod workPeriod = new WorkPeriod();
        workPeriod.setMouseActionsCount(mouseActions);
        workPeriod.setKeyboardActionsCount(keyboardActions);
        workPeriod.setSession(workSession);

        final Tracking tracking = workSession.getTracking();
        final Moment lastMoment = tracking.getLastMoment();
        final MomentType lastMomentType = lastMoment.getType();

        if(lastLoggedPeriod == null){
            // Assert that the initial moment is a start moment
            if(!MomentType.START.equals(lastMomentType)) {
                LOGGER.error("Error logging work, the initial moment is not a START moment: {}", lastMoment);
                throw new IllegalMomentTypeExcetion(lastMoment, lastMomentType);
            }
        } else {
            if(!(MomentType.HEARTBEAT.equals(lastMomentType) || MomentType.RESUME.equals(lastMomentType))){
                LOGGER.error("Error logging work, the last moment is not a HEARTBEAT or RESUME moment: {}", lastMoment);
                throw new IllegalMomentTypeExcetion(lastMoment, lastMomentType);
            }
        }

        final Moment logworkHeartBeatMoment = trackingsService.addMoment(principal, tracking, MomentType.HEARTBEAT, attachmentBytes, memo);

        workPeriod.setStart(lastMoment);
        workPeriod.setEnd(logworkHeartBeatMoment);
        workSession.setLastLoggedPeriod(workPeriod);
        workSession.setMouseActionsCount(workSession.getMouseActionsCount()+mouseActions);
        workSession.setKeyboardActionsCount(workSession.getKeyboardActionsCount() + keyboardActions);
        entityManager.persist(workPeriod);
        return workPeriod;
    }

    public void pauseWorkSession(String principal){}

    public void resumeWorkSession(String principal){}

    public void stopWorkSession(String principal){}

}
