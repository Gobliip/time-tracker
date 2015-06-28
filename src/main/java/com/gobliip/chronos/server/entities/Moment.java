package com.gobliip.chronos.server.entities;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.gobliip.jpa.converters.InstantPersistenceConverter;

@Entity(name = "moments")
public class Moment extends BaseEntity {

	public static enum MomentType {
		PAUSE, MEMO, RESUME, STOP, START
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5527675255255574923L;
	
	public Moment(MomentType type, Tracking tracking){
		this(type);
		this.tracking = tracking;
	}
	
	public Moment(MomentType type){
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
	private Instant momentInstant;

	@Column(name = "memo", length = 1024)
	private String memo;

	@Column(name = "moment_type", length = 56, nullable = false)
	@Enumerated(EnumType.STRING)
	private MomentType type;

	@ManyToOne
	@JoinColumn(name = "tracking_id", nullable = false)
	private Tracking tracking;

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

}
