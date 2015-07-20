package com.gobliip.chronos.server.entities;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gobliip.jpa.converters.InstantPersistenceConverter;

@Entity(name = "trackings")
public class Tracking extends BaseEntity {

    public static enum TrackingStatus {
        ISSUED, RUNNING, PAUSED, STOPPED
    }

    /**
     *
     */
    private static final long serialVersionUID = 7507481201274984639L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "owner", length = 256, nullable = false)
    private String owner;

    @Convert(converter = InstantPersistenceConverter.class)
    @Column(name = "start_date", nullable = false)
    private Instant start;

    @Convert(converter = InstantPersistenceConverter.class)
    @Column(name = "end_date")
    private Instant end;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrackingStatus status;

    // I use lazy fetching because we will usually not fetch them this way...
    // There should be moments > trackings
    @JsonIgnore
    @OneToMany(mappedBy = "tracking", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @NotEmpty
    private List<Moment> moments = new LinkedList<Moment>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "last_moment_id")
    private Moment lastMoment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public TrackingStatus getStatus() {
        return status;
    }

    public void setStatus(TrackingStatus status) {
        this.status = status;
    }

    public List<Moment> getMoments() {
        return moments;
    }

    public void setMoments(List<Moment> moments) {
        this.moments = moments;
    }

    public Moment getLastMoment() {

        return lastMoment;
    }

    public void setLastMoment(Moment lastMoment) {
        this.lastMoment = lastMoment;
    }

    public void addMoment(final Moment moment){
        moment.setTracking(this);
        this.lastMoment = moment;
        getMoments().add(moment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tracking tracking = (Tracking) o;

        if (!id.equals(tracking.id)) return false;
        if (!owner.equals(tracking.owner)) return false;
        if (!start.equals(tracking.start)) return false;
        if (end != null ? !end.equals(tracking.end) : tracking.end != null) return false;
        if (status != tracking.status) return false;
        return !(lastMoment != null ? !lastMoment.equals(tracking.lastMoment) : tracking.lastMoment != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + owner.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + status.hashCode();
        return result;
    }
}
