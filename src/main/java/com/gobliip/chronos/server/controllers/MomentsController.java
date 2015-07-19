package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.domain.exception.ResourceNotFoundException;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.service.MomentsService;
import com.gobliip.chronos.server.service.exception.InvalidTrackingStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsamayoa on 19/07/15.
 */
@RestController
@RequestMapping("/moments")
public class MomentsController {

    @Autowired
    private MomentsService momentsService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Moment createMoment(
            @AuthenticationPrincipal final Principal user,
            @RequestParam("trackingId") final Long trackingId,
            @RequestParam("type") final Moment.MomentType type,
            @RequestParam(value = "attachment", required = false) final MultipartFile attachment,
            @RequestParam(value = "memo", required = false) final String memo
    ) throws IOException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return momentsService.createMoment(user.getName(), trackingId, type, attachmentBytes, memo);
    }

    @ExceptionHandler(InvalidTrackingStateException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public Map<String, Serializable> invalidTrackingException(final InvalidTrackingStateException exception){
        final Tracking tracking = exception.getTracking();
        final Map<String, Serializable> result = new HashMap<>();
        result.put("tracking_id", tracking.getId());
        result.put("tracking_status", tracking.getStatus());
        result.put("action", exception.getAction());
        return result;
    }

}
