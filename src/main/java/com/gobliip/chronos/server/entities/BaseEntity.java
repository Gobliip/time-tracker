package com.gobliip.chronos.server.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@MappedSuperclass
public class BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 579655508125798607L;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@PreUpdate
	private void setUpdatedAt() {
		this.updatedAt = Date.from(Instant.now());
	}

	@PrePersist
	private void setcreatedAt() {
		this.createdAt = Date.from(Instant.now());
	}
}
