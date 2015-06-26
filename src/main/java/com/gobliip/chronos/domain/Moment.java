package com.gobliip.chronos.domain;

import java.time.Instant;

public class Moment {

	private Instant when;
	private String memo;

	public Instant getWhen() {
		return when;
	}

	public void setWhen(Instant when) {
		this.when = when;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
