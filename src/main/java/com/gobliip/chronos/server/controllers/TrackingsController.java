package com.gobliip.chronos.server.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import com.gobliip.chronos.server.entities.Moment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gobliip.chronos.domain.exception.UnpausableTrackingException;
import com.gobliip.chronos.domain.exception.UnresumableTrackingException;
import com.gobliip.chronos.domain.exception.UnstopableTrackingException;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.service.TrackingsService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/trackings")
public class TrackingsController {

	@Autowired
	private TrackingsService service;

	@Secured("ROLE_USER")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
	public Tracking crateTracking(@AuthenticationPrincipal final Principal user,
								  @RequestParam(required = false) final MultipartFile attachment,
								  @RequestParam(required = false) final String memo) throws IOException {
		final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
		return service.createTracking(user.getName(), attachmentBytes, memo);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/moments", method = RequestMethod.GET, produces = "application/json")
	public List<Moment> getTrackingMoments(@AuthenticationPrincipal Principal user,
										   @PathVariable Long trackingId) throws IOException {
		return service.findMoments(user.getName(), trackingId);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/stop", method = RequestMethod.POST, produces = "application/json")
	public Tracking stopTracking(@AuthenticationPrincipal Principal user,
								 @PathVariable Long trackingId,
								 @RequestParam(required = false) final MultipartFile attachment,
								 @RequestParam(required = false) final String memo)
			throws UnstopableTrackingException, IOException {
		final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
		return service.stopTracking(user.getName(), trackingId, attachmentBytes, memo);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/pause", method = RequestMethod.POST, produces = "application/json")
	public Tracking pauseTracking(@AuthenticationPrincipal Principal user,
								  @PathVariable Long trackingId,
								  @RequestParam(required = false) final MultipartFile attachment,
								  @RequestParam(required = false) final String memo)
			throws UnpausableTrackingException, IOException {
		final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
		return service.pauseTracking(user.getName(), trackingId, attachmentBytes, memo);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/resume", method = RequestMethod.POST, produces = "application/json")
	public Tracking resumeTracking(@AuthenticationPrincipal Principal user,
								   @PathVariable Long trackingId,
								   @RequestParam(required = false) final MultipartFile attachment,
								   @RequestParam(required = false) final String memo)
			throws UnresumableTrackingException, IOException {
		final byte[] attachmentBytes = attachment != null ? attachment.getBytes() : null;
		return service.resumeTracking(user.getName(), trackingId, attachmentBytes, memo);
	}

}
