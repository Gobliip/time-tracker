package com.gobliip.chronos.server.audit;

import java.io.Serializable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.gobliip.chronos.domain.exception.ResourceNotFoundException;
import com.gobliip.chronos.domain.exception.ResourceNotOwnedException;

@Aspect
@Component
public class ResourceAuditAspect implements ApplicationEventPublisherAware {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @AfterThrowing(pointcut = "@annotation(resource)", throwing = "ex")
    public void auditResoruceNotOwned(ResourceNotOwnedException ex, ResourceAudit resource) {
        String principal = ex.getPrincipal() != null ? ex.getPrincipal()
                : (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        // @formatter:off
        AuditEvent illegalAccessEvent = new AuditEvent(
                principal, "RESOURCE_ACCESS_ILLEGAL",
                "RESOURCE_ID=" + ex.getResourceId().toString(),
                "RESOURCE_TYPE=" + ex.getResourceType());
        //@formatter:on

        applicationEventPublisher.publishEvent(new AuditApplicationEvent(
                illegalAccessEvent));
    }

    @AfterThrowing(pointcut = "@annotation(resource)", throwing = "ex")
    public void auditResourceNotFound(ResourceNotFoundException ex, ResourceAudit resource) {
        String principal = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        // @formatter:off
        AuditEvent illegalAccessEvent = new AuditEvent(
                principal, "RESOURCE_ACCESS_NOT_FOUND",
                "RESOURCE_ID=" + ex.getResourceId(),
                "RESOURCE_TYPE=" + ex.getResourceType());
        //@formatter:on

        applicationEventPublisher.publishEvent(new AuditApplicationEvent(
                illegalAccessEvent));
    }

    @After("@annotation(resource) && args(resourceId,..)")
    public void auditResourceAccess(JoinPoint jp, ResourceAudit resource,
                                    Serializable resourceId) throws Throwable {
        String principal = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        // @formatter:off
        AuditEvent resourceAccessEvent = new AuditEvent(
                principal, "RESOURCE_ACCESS",
                "RESOURCE_TYPE=" + ((MethodSignature) jp.getSignature()).getReturnType().getName(),
                "RESOURCE_ID=" + resourceId
        );
        // @formatter:on
        applicationEventPublisher.publishEvent(new AuditApplicationEvent(
                resourceAccessEvent));
    }

}
