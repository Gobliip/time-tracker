package com.gobliip.chronos.domain.exception;

import java.io.Serializable;

public class ResourceNotOwnedException extends RuntimeException {

	private static final long serialVersionUID = 7924242450274369441L;

	private String resourceType;
	private Serializable resourceId;
	private String principal;

	public ResourceNotOwnedException(Class<?> resourceType, Serializable resourceId,
									 String principal) {
		this.resourceType = resourceType.getName();
		this.resourceId = resourceId;
		this.principal = principal;
	}

	public ResourceNotOwnedException(String resourceType, Serializable resourceId,
			String principal) {
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.principal = principal;
	}

	@Override
	public String getMessage() {
		return "User with username: " + getPrincipal()
				+ " does not own resource of type:" + getResourceType()
				+ " with id: " + getResourceId();
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public Serializable getResourceId() {
		return resourceId;
	}

	public void setResourceId(Serializable resourceId) {
		this.resourceId = resourceId;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

}
