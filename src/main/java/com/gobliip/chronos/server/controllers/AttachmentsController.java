package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.server.service.AttachmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by lsamayoa on 19/07/15.
 */
@RestController
@RequestMapping("/attachments")
public class AttachmentsController {

    @Autowired
    private AttachmentsService attachmentsService;

    @RequestMapping("/{attachmentId}/raw")
    public byte[] getRaw(
            @AuthenticationPrincipal Principal principal,
            @PathVariable("attachmentId") Long attachmentId
    ) {
        return attachmentsService.getRaw(principal.getName(), attachmentId);
    }

}
