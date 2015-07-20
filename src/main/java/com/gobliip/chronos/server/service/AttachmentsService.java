package com.gobliip.chronos.server.service;

import com.gobliip.chronos.domain.exception.ResourceNotFoundException;
import com.gobliip.chronos.domain.exception.ResourceNotOwnedException;
import com.gobliip.chronos.server.audit.ResourceAudit;
import com.gobliip.chronos.server.entities.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

/**
 * Created by lsamayoa on 19/07/15.
 */
@Service
public class AttachmentsService {

    @Autowired
    private EntityManager entityManager;

    @ResourceAudit
    public byte[] getRaw(String user, Long attachmentId){
        final Attachment attachment = findAttachment(user, attachmentId);
        return attachment.getContent();
    }

    @ResourceAudit
    public Attachment findAttachment(String user, Long attachmentId) {
        final Attachment attachment = entityManager.find(Attachment.class, attachmentId);

        if(attachment == null){
            throw new ResourceNotFoundException(attachmentId, Attachment.class);
        }

        if(!attachment.isPublic() && user.equals(attachment.getMoment().getTracking().getOwner())){
            throw new ResourceNotOwnedException("Attachment", attachmentId, user);
        }

        return attachment;
    }
}
