package com.gobliip.chronos.server.controllers;

import com.gobliip.chronos.server.service.AttachmentsService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.Media;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Collections;

/**
 * Created by lsamayoa on 19/07/15.
 */
@RestController
@RequestMapping("/attachments")
public class AttachmentsController {

    @Autowired
    private AttachmentsService attachmentsService;

    @RequestMapping(value = "/{attachmentId}/raw")
    public ResponseEntity<byte[]> getRaw(
            @AuthenticationPrincipal Principal principal,
            @PathVariable("attachmentId") Long attachmentId
    ) {
        byte[] attachment = attachmentsService.getRaw(principal.getName(), attachmentId);
        Tika tika = new Tika();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.parseMediaType(tika.detect(attachment)));
        return new ResponseEntity<>(attachment, responseHeaders, HttpStatus.OK);
    }
    @RequestMapping(value = "/{attachmentId}/base64")
    public ResponseEntity<byte[]> getBase64Encoded(
            @AuthenticationPrincipal Principal principal,
            @PathVariable("attachmentId") Long attachmentId
    ) throws IOException {
        byte[] attachment = attachmentsService.getRaw(principal.getName(), attachmentId);
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            Thumbnails.of(new ByteArrayInputStream(attachment))
                    .width(350)
                    .toOutputStream(baos);
            baos.flush();
            Tika tika = new Tika();
            HttpHeaders responseHeaders = new HttpHeaders();
            byte[] encodedAttachment = Base64.getMimeEncoder().encode(baos.toByteArray());
            responseHeaders.setContentType(MediaType.parseMediaType(tika.detect(encodedAttachment)));
            return new ResponseEntity<>(encodedAttachment, responseHeaders, HttpStatus.OK);
        }
    }

}
