package com.gobliip.chronos.server.entities;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
	@OneToMany(mappedBy = "tracking", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@NotEmpty
	private List<Moment> moments = new LinkedList<Moment>();

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
}
