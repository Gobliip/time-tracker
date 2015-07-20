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
        CREATE_MEMO("create", "moment", "memo");
        private final String action;
        private final String[] params;
        TrackingAction(String action, String... params){
            this.action = action;
            this.params = params;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(action);
            for (String param : params){
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
        Assert.hasText(ownerId);
        Assert.notNull(trackingId);
        Assert.state(trackingId > 0);
        Assert.notNull(type);
        Assert.state(verifyMomentTypeCreationSupport(type));

        final Tracking tracking = trackingsService.findTracking(ownerId, trackingId);

        final Moment moment = new Moment();
        moment.setTracking(tracking);
        moment.setMemo(memo);
        moment.setMomentInstant(Instant.now());
        moment.setType(type);

        final Attachment attachment = new Attachment();
        attachment.setLocation(Attachment.AttachmentLocation.DATABASE);
        attachment.setStatus(Attachment.AttachmentStatus.MANTAINANCE);
        attachment.setContent(attachmentBytes);
        attachment.setUrl(ATTACHMENT_URL_BEING_GENERATED_IMAGE_URL);
        attachment.setMoment(moment);
        moment.getAttachments().add(attachment);

        if (!validateTrackingAction(tracking, TrackingAction.CREATE_MEMO)) {
            LOGGER.error("Tracking currently not running, imposibble to create new moment: {}", moment);
            throw new InvalidTrackingStateException(tracking, TrackingAction.CREATE_MEMO);
        }

        entityManager.persist(moment);
        attachment.setUrl("/attachments/" + attachment.getId()+ "/raw");

        return moment;
    }

    public boolean validateTrackingAction(Tracking tracking, TrackingAction action){
        switch (action){
            case CREATE_MEMO:
                return Tracking.TrackingStatus.RUNNING.equals(tracking.getStatus());
        }
        return false;
    }

    public boolean verifyMomentTypeCreationSupport(Moment.MomentType type) {
        switch (type) {
            // Only allow memo creation through this service
            case MEMO:
                return true;
            default:
                return false;
        }
    }

}
