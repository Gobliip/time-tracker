package com.gobliip.chronos.server.audit;

import com.gobliip.chronos.domain.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsamayoa on 19/07/15.
 */
@ControllerAdvice
public class ResourceNotFoundControllerAdvice {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Serializable> resourceNotFoundExceptionHandler(final ResourceNotFoundException exception){
        final Map<String, Serializable> result = new HashMap<>();
        result.put("resource_id", exception.getResourceId());
        result.put("resource_type", exception.getResourceType().getSimpleName());
        return result;
    }
}
