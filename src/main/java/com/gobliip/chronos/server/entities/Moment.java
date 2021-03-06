package com.gobliip.chronos.server.entities;

import com.fasterxml.jackson.annotation.*;
import com.gobliip.jpa.converters.InstantPersistenceConverter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "moments")
public class Moment extends BaseEntity {

    public static enum MomentType {
        PAUSE, RESUME, STOP, START, HEARTBEAT
    }

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
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("trackingId")
    private Tracking tracking;

    @OneToMany(mappedBy = "moment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"location", "moment"})
    private List<Attachment> attachments = new ArrayList<Attachment>();

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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Moment moment = (Moment) o;

        if (!id.equals(moment.id)) return false;
        if (!momentInstant.equals(moment.momentInstant)) return false;
        if (memo != null ? !memo.equals(moment.memo) : moment.memo != null) return false;
        if (type != moment.type) return false;
        if (!tracking.equals(moment.tracking)) return false;
        return !(attachments != null ? !attachments.equals(moment.attachments) : moment.attachments != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + momentInstant.hashCode();
        result = 31 * result + (memo != null ? memo.hashCode() : 0);
        result = 31 * result + type.hashCode();
        result = 31 * result + tracking.hashCode();
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        return result;
    }
}
