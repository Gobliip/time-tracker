package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.domain.exception.UnpausableTrackingException;
import com.gobliip.chronos.domain.exception.UnresumableTrackingException;
import com.gobliip.chronos.domain.exception.UnstopableTrackingException;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.service.TrackingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trackings")
public class TrackingsController {

    public enum TrackingAction {
        STOP,
        RESUME,
        PAUSE,
        HEARTBEAT
    }

    @Autowired
    private TrackingsService service;

    @Secured("ROLE_USER")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Tracking crateTracking(@AuthenticationPrincipal final Principal user,
                                  @RequestParam(required = false) final MultipartFile attachment,
                                  @RequestParam(required = false) final String memo) throws IOException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return service.createTracking(user.getName(), Optional.ofNullable(attachmentBytes), Optional.ofNullable(memo));
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/{trackingId}/moments", method = RequestMethod.GET, produces = "application/json")
    public List<Moment> getTrackingMoments(@AuthenticationPrincipal final Principal user,
                                           @PathVariable final Long trackingId) throws IOException {
        return service.findMoments(user.getName(), trackingId);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/{trackingId}/{action:stop|pause|resume|heartbeat}", method = RequestMethod.POST, produces = "application/json")
    public Tracking sendAction(@AuthenticationPrincipal final Principal user,
                               @PathVariable final Long trackingId,
                               @PathVariable final String action,
                               @RequestParam(required = false) final MultipartFile attachment,
                               @RequestParam(required = false) final String memo)
            throws IOException, UnstopableTrackingException, UnpausableTrackingException, UnresumableTrackingException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        final TrackingAction trackingAction = TrackingAction.valueOf(action.toUpperCase());
        switch (trackingAction) {
            case STOP:
                return service.stopTracking(user.getName(), trackingId, Optional.ofNullable(attachmentBytes), Optional.ofNullable(memo));
            case PAUSE:
                return service.pauseTracking(user.getName(), trackingId, Optional.ofNullable(attachmentBytes), Optional.ofNullable(memo));
            case RESUME:
                return service.resumeTracking(user.getName(), trackingId, Optional.ofNullable(attachmentBytes), Optional.ofNullable(memo));
            case HEARTBEAT:
                return service.doHeartbeat(user.getName(), trackingId, Optional.ofNullable(attachmentBytes), Optional.ofNullable(memo));
        }
        throw new IllegalArgumentException("Tracking action is not recognized");
    }

}
