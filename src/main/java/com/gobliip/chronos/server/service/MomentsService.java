package com.gobliip.chronos.server.service;

import com.gobliip.chronos.server.audit.ResourceAudit;
import com.gobliip.chronos.server.entities.Attachment;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.service.exception.InvalidTrackingStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.time.Instant;

/**
 * Created by lsamayoa on 19/07/15.
 */
@Service
public class MomentsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MomentsService.class);
    private static final String ATTACHMENT_URL_BEING_GENERATED_IMAGE_URL = "http://www.codemonkeyintraining.com/wp-content/uploads/2015/06/monkey-on-computer.jpg";

    public enum TrackingAction {
        CREATE_MEMO("create", "moment", "memo"),
        START_TRACKING("create", "moment", "start"),
        STOP_TRACKING("create", "moment", "stop"),
        PAUSE_TRACKING("create", "moment", "pause"),
        RESUME_TRACKING("create", "moment", "resume"),
        HEARTBEAT("create", "moment", "heartbeat");

        private final String action;
        private final String[] params;

        TrackingAction(String action, String... params) {
            this.action = action;
            this.params = params;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(action);
            for (String param : params) {
                builder.append(' ');
                builder.append(param);
            }
            return builder.toString();
        }
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TrackingsService trackingsService;

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Moment createMoment(String ownerId, Long trackingId, Moment.MomentType type, byte[] attachmentBytes, String memo)
            throws InvalidTrackingStateException {
        Assert.notNull(trackingId);
        Assert.state(trackingId > 0);

        final Tracking tracking = trackingsService.findTracking(ownerId, trackingId);

        return createMoment(ownerId, tracking, type, attachmentBytes, memo);
    }

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Moment createMoment(String ownerId, Tracking tracking, Moment.MomentType type, byte[] attachmentBytes, String memo)
            throws InvalidTrackingStateException {
        Assert.hasText(ownerId);
        Assert.notNull(type);
        Assert.state(ownerId.equals(tracking.getOwner()));

        final Moment moment = new Moment();
        moment.setTracking(tracking);
        moment.setMemo(memo);
        moment.setMomentInstant(Instant.now());
        moment.setType(type);

        final Tracking.TrackingStatus trackingStatus = tracking.getStatus();
        switch (type) {
            case MEMO:
                if (!Tracking.TrackingStatus.RUNNING.equals(trackingStatus)) {
                    LOGGER.error("Tracking currently not running, impossible to create new memo: {}", moment);
                    throw new InvalidTrackingStateException(tracking, TrackingAction.CREATE_MEMO);
                }
                break;
            case START:
                if (!Tracking.TrackingStatus.RUNNING.equals(trackingStatus)) {
                    LOGGER.error("Tracking currently not running, impossible to create new start moment: {}", moment);
                    throw new InvalidTrackingStateException(tracking, TrackingAction.START_TRACKING);
                }
                break;
            case STOP:
                if (!Tracking.TrackingStatus.RUNNING.equals(trackingStatus) || !Tracking.TrackingStatus.PAUSED.equals(trackingStatus)) {
                    LOGGER.error("Tracking currently not running or paused, impossible to create new stop moment: {}", moment);
                    throw new InvalidTrackingStateException(tracking, TrackingAction.STOP_TRACKING);
                }
                break;
            case PAUSE:
                if (!Tracking.TrackingStatus.RUNNING.equals(trackingStatus)) {
                    LOGGER.error("Tracking currently not running, impossible to create new pause moment: {}", moment);
                    throw new InvalidTrackingStateException(tracking, TrackingAction.PAUSE_TRACKING);
                }
                break;
            case RESUME:
                if (!Tracking.TrackingStatus.PAUSED.equals(trackingStatus)) {
                    LOGGER.error("Tracking currently not paused, impossible to create new pause moment: {}", moment);
                    throw new InvalidTrackingStateException(tracking, TrackingAction.RESUME_TRACKING);
                }
                break;
            case HEARTBEAT:
                if (!Tracking.TrackingStatus.RUNNING.equals(trackingStatus)) {
                    LOGGER.error("Tracking currently not running, impossible to create new heartbeat moment: {}", moment);
                    throw new InvalidTrackingStateException(tracking, TrackingAction.HEARTBEAT);
                }
                break;
        }

        entityManager.persist(moment);

        if (attachmentBytes != null && attachmentBytes.length > 0) {
            final Attachment attachment = new Attachment();
            attachment.setLocation(Attachment.AttachmentLocation.DATABASE);
            attachment.setStatus(Attachment.AttachmentStatus.MANTAINANCE);
            attachment.setContent(attachmentBytes);
            attachment.setUrl(ATTACHMENT_URL_BEING_GENERATED_IMAGE_URL);
            attachment.setMoment(moment);
            moment.getAttachments().add(attachment);
            entityManager.persist(attachment);
            attachment.setUrl("/attachments/" + attachment.getId() + "/raw");
            attachment.setStatus(Attachment.AttachmentStatus.AVAILABLE);
        }

        return moment;
    }

}
