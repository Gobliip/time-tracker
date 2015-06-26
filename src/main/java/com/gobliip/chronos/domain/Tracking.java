package com.gobliip.chronos.domain;

import java.time.Instant;
import java.util.List;

public class Tracking {

	Instant start;
	Instant end;

	List<Moment> moments;

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

	public List<Moment> getMoments() {
		return moments;
	}

	public void setMoments(List<Moment> moments) {
		this.moments = moments;
	}

}
