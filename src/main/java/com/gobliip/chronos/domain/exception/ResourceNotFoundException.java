package com.gobliip.chronos.domain.exception;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -2308126345262111020L;

    private Class<?> resourceType;
    private Serializable resourceId;

    public ResourceNotFoundException(Serializable resourceId,
                                     Class<?> resourceType) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    @Override
    public String getMessage() {
        return "Unable to find resource with id: " + resourceId + " of type: "
                + resourceType.getName();
    }

    public Class<?> getResourceType() {
        return resourceType;
    }

    public void setResourceType(Class<?> resourceType) {
        this.resourceType = resourceType;
    }

    public Serializable getResourceId() {
        return resourceId;
    }

    public void setResourceId(Serializable resourceId) {
        this.resourceId = resourceId;
    }

}
