package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.service.MomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

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
            @AuthenticationPrincipal Principal user,
            @RequestParam("trackingId") Long trackingId,
            @RequestParam("type") Moment.MomentType type,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @RequestParam(value = "memo", required = false) String memo
    ) throws IOException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return momentsService.createMoment(user.getName(), trackingId, type, attachmentBytes, memo);
    }

}
