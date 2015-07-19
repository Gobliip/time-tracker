package com.gobliip.chronos.server.entities;

import com.fasterxml.jackson.annotation.*;
import com.gobliip.jpa.converters.InstantPersistenceConverter;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "moments")
public class Moment extends BaseEntity {

    public static enum MomentType {
        PAUSE, MEMO, RESUME, STOP, START
    }

    /**
     *
     */
    private static final long serialVersionUID = 5527675255255574923L;

    public Moment(MomentType type, Tracking tracking) {
        this(type);
        this.tracking = tracking;
    }

    public Moment(MomentType type) {
        this();
        this.type = type;
    }

    public Moment() {
        this.momentInstant = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Convert(converter = InstantPersistenceConverter.class)
    @Column(name = "moment_instant", nullable = false)
    @JsonProperty("instant")
    private Instant momentInstant;

    @Column(name = "memo", length = 1024)
    private String memo;

    @Column(name = "moment_type", length = 56, nullable = false)
    @Enumerated(EnumType.STRING)
    private MomentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("tracking_id")
    private Tracking tracking;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "attachment")
    @JsonIgnore
    private byte[] attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMomentInstant() {
        return momentInstant;
    }

    public void setMomentInstant(Instant momentInstant) {
        this.momentInstant = momentInstant;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public MomentType getType() {
        return type;
    }

    public void setType(MomentType type) {
        this.type = type;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
