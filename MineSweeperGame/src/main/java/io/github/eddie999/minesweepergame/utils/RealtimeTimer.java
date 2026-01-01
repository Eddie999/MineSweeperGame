package io.github.eddie999.minesweepergame.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RealtimeTimer {
	private LocalDateTime time;
	
	public RealtimeTimer() {
		time = LocalDateTime.now();
	}
	
	public Long get() {
		return ChronoUnit.MILLIS.between(time, LocalDateTime.now());
	}
	
	public void reset() {
		time = LocalDateTime.now();
	}	
}
