package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.domain.exception.UnpausableTrackingException;
import com.gobliip.chronos.domain.exception.UnresumableTrackingException;
import com.gobliip.chronos.domain.exception.UnstopableTrackingException;
import com.gobliip.chronos.server.entities.Moment;
import com.gobliip.chronos.server.entities.WorkPeriod;
import com.gobliip.chronos.server.entities.WorkSession;
import com.gobliip.chronos.server.service.WorkTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Created by lsamayoa on 20/07/15.
 */
@RestController
@RequestMapping("/worktracker")
public class WorkTrackerController {
    
    public enum WorkTrackerAction {
        START,
        STOP,
        PAUSE,
        RESUME,
        LOG
    }

    @Autowired
    private WorkTrackerService workTrackerService;

    @RequestMapping(value = "/{workSessionId}/periods", method = RequestMethod.GET)
    public List<WorkPeriod> getWorkSessionPeriods(
            @AuthenticationPrincipal final Principal principal,
            @PathVariable final Long workSessionId
    ){
        return workTrackerService.getPeriods(principal.getName(), workSessionId);
    }

    @RequestMapping(value = "/{workSessionId}/moments", method = RequestMethod.GET)
    public List<Moment>  getWorkSessionMoments(@AuthenticationPrincipal final Principal principal,
                                               @PathVariable final Long workSessionId){
        return workTrackerService.getMoments(principal.getName(), workSessionId);
    }

    @RequestMapping(value = "/{action:start|log|pause|resume|stop}", method = RequestMethod.POST)
    public WorkSession sendAction(
            @AuthenticationPrincipal final Principal principal,
            @PathVariable final String action,
            @RequestParam(required = false) final Integer mouseActions,
            @RequestParam(required = false) final Integer keyboardActions,
            @RequestParam(required = false) final MultipartFile attachment,
            @RequestParam(required = false) final String memo
    ) throws IOException, UnresumableTrackingException, UnpausableTrackingException, UnstopableTrackingException {
        final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
        switch (WorkTrackerAction.valueOf(action.toUpperCase())) {
            case START:
                return workTrackerService
                        .startWorkSession(principal.getName(),
                                Optional.ofNullable(attachmentBytes),
                                Optional.ofNullable(memo));
            case LOG:
                return workTrackerService
                        .logWorkToWorkSession(principal.getName(),
                                mouseActions,
                                keyboardActions,
                                Optional.ofNullable(attachmentBytes),
                                Optional.ofNullable(memo));
            case RESUME:
                return workTrackerService
                        .resumeWorkSession(principal.getName(),
                                Optional.ofNullable(attachmentBytes),
                                Optional.ofNullable(memo));
            case PAUSE:
                return workTrackerService
                        .pauseWorkSession(principal.getName(),
                                mouseActions,
                                keyboardActions,
                                Optional.ofNullable(attachmentBytes),
                                Optional.ofNullable(memo));
            case STOP:
                return workTrackerService
                        .stopWorkSession(principal.getName(),
                                mouseActions,
                                keyboardActions,
                                Optional.ofNullable(attachmentBytes),
                                Optional.ofNullable(memo));
        }
        throw new IllegalArgumentException("Work session action is not recognized");
    }
}
