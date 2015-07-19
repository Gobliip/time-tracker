package com.gobliip.chronos.server.service;

import com.gobliip.chronos.server.audit.ResourceAudit;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.MomentsRepository;
import com.gobliip.chronos.server.entities.Tracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;

/**
 * Created by lsamayoa on 19/07/15.
 */
@Service
public class MomentsService {

    @Autowired
    private MomentsRepository momentsRepository;

    @Autowired
    private TrackingsService trackingsService;

    @ResourceAudit
    @Secured("ROLE_USER")
    @Transactional(readOnly = false)
    public Moment createMoment(String ownerId, Long trackingId, Moment.MomentType type, byte[] attachment, String memo) {
        Assert.hasText(ownerId);
        Assert.notNull(trackingId);
        Assert.state(trackingId > 0);
        Assert.notNull(type);
        Assert.state(verifyTypeCreationSupport(type));

        Tracking tracking = trackingsService.findTracking(ownerId, trackingId);

        Moment moment = new Moment();
        moment.setAttachment(attachment);
        moment.setTracking(tracking);
        moment.setMemo(memo);
        moment.setMomentInstant(Instant.now());
        moment.setType(type);

        return momentsRepository.save(moment);
    }

    public boolean verifyTypeCreationSupport(Moment.MomentType type) {
        switch (type) {
            // Only allow memo creation through this service
            case MEMO:
                return true;
            default:
                return false;
        }
    }

}
