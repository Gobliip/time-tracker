package com.gobliip.chronos.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by lsamayoa on 19/07/15.
 */
@Entity(name = "attachments")
public class Attachment extends BaseEntity {

    public enum AttachmentStatus {
        MANTAINANCE,
        AVAILABLE
    }

    public enum AttachmentLocation {
        AMAZON_S3,
        DATABASE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content")
    @JsonIgnore
    private byte[] content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttachmentStatus status;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
    private AttachmentLocation location;

    @ManyToOne
    @JsonIgnore
    private Moment moment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public AttachmentStatus getStatus() {
        return status;
    }

    public void setStatus(AttachmentStatus status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AttachmentLocation getLocation() {
        return location;
    }

    public void setLocation(AttachmentLocation location) {
        this.location = location;
    }

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }
}
