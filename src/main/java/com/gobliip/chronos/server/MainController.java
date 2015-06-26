package com.gobliip.chronos.server;

import java.security.Principal;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gobliip.chronos.domain.Moment;

@RestController
public class MainController {

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	public String crateTracking(Principal user) throws JsonProcessingException {
		OAuth2Authentication principal = (OAuth2Authentication) user;
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) principal
				.getDetails();
		return new ObjectMapper().writeValueAsString(principal);
	}

	public void stopTracking(Long trackingId) {

	}

	public Moment addMoment(Long trackingId, Moment moment) {
		return null;
	}

}
