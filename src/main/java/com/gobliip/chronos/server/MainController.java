package com.gobliip.chronos.server;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gobliip.chronos.domain.exception.UnpausableTrackingException;
import com.gobliip.chronos.domain.exception.UnresumableTrackingException;
import com.gobliip.chronos.domain.exception.UnstopableTrackingException;
import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.service.TrackingsService;

@RestController
public class MainController {

	@Autowired
	private TrackingsService service;

	@Secured("ROLE_USER")
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	public Tracking crateTracking(Principal user) {
		String userName = user.getName();
		return service.createTracking(userName);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/stop", method = RequestMethod.POST, produces = "application/json")
	public Tracking stopTracking(Principal user, @PathVariable Long trackingId)
			throws UnstopableTrackingException {
		String userName = user.getName();
		return service.stopTracking(userName, trackingId);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/pause", method = RequestMethod.POST, produces = "application/json")
	public Tracking pauseTracking(Principal user, @PathVariable Long trackingId)
			throws UnpausableTrackingException {
		String userName = user.getName();
		return service.pauseTracking(userName, trackingId);
	}

	@Secured("ROLE_USER")
	@RequestMapping(value = "/{trackingId}/resume", method = RequestMethod.POST, produces = "application/json")
	public Tracking resumeTracking(Principal user, @PathVariable Long trackingId)
			throws UnresumableTrackingException {
		String userName = user.getName();
		return service.resumeTracking(userName, trackingId);
	}

}
