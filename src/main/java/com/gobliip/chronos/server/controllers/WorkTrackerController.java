package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.domain.exception.UnpausableTrackingException;
import com.gobliip.chronos.domain.exception.UnstopableTrackingException;
import com.gobliip.chronos.server.entities.WorkSession;
import com.gobliip.chronos.server.service.WorkTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

/**
 * Created by lsamayoa on 20/07/15.
 */
@RestController
@RequestMapping("/worktracker")
public class WorkTrackerController {

    @Autowired
    private WorkTrackerService workTrackerService;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public WorkSession startWorkSession(
            @AuthenticationPrincipal final Principal principal,
            @RequestParam(required = false) final MultipartFile attachment,
            @RequestParam(required = false) final String memo
    ) throws IOException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return workTrackerService
                .startWorkSession(principal.getName(),
                        Optional.ofNullable(attachmentBytes),
                        Optional.ofNullable(memo));
    }

    @RequestMapping(value = "/log", method = RequestMethod.POST)
    public WorkSession logWork(
            @AuthenticationPrincipal final Principal principal,
            @RequestParam final int mouseActions,
            @RequestParam final int keyboardActions,
            @RequestParam(required = false) final MultipartFile attachment,
            @RequestParam(required = false) final String memo
    ) throws IOException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return workTrackerService
                .logWorkToWorkSession(principal.getName(),
                        mouseActions,
                        keyboardActions,
                        Optional.ofNullable(attachmentBytes),
                        Optional.ofNullable(memo));
    }

    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public WorkSession resumeWorkSession(
            @AuthenticationPrincipal final Principal principal,
            @RequestParam(required = false) final MultipartFile attachment,
            @RequestParam(required = false) final String memo
    ) throws IOException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return workTrackerService
                .startWorkSession(principal.getName(),
                        Optional.ofNullable(attachmentBytes),
                        Optional.ofNullable(memo));
    }

    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public WorkSession pauseWorkSession(
            @AuthenticationPrincipal final Principal principal,
            @RequestParam(required = false, defaultValue = "0") final int mouseActions,
            @RequestParam(required = false, defaultValue = "0") final int keyboardActions,
            @RequestParam(required = false) final MultipartFile attachment,
            @RequestParam(required = false) final String memo
    ) throws IOException, UnpausableTrackingException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return workTrackerService
                .pauseWorkSession(principal.getName(),
                        mouseActions,
                        keyboardActions,
                        Optional.ofNullable(attachmentBytes),
                        Optional.ofNullable(memo));
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public WorkSession stopWorkSession(
            @AuthenticationPrincipal final Principal principal,
            @RequestParam(required = false, defaultValue = "0") final int mouseActions,
            @RequestParam(required = false, defaultValue = "0") final int keyboardActions,
            @RequestParam(required = false) final MultipartFile attachment,
            @RequestParam(required = false) final String memo
    ) throws IOException, UnpausableTrackingException, UnstopableTrackingException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        return workTrackerService
                .stopWorkSession(principal.getName(),
                        mouseActions,
                        keyboardActions,
                        Optional.ofNullable(attachmentBytes),
                        Optional.ofNullable(memo));
    }

}
