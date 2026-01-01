package io.github.eddie999.minesweepergame.game;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class GameCounter {
	private Boolean cooldownRunning;
	private Integer retryCounter;
	private LocalDateTime lastRetry;
	private final Integer gameRetries;
	private final Integer gameCooldown;
	
	public GameCounter(Integer gameRetries, Integer gameCooldown) {
		this.gameRetries = gameRetries;
		this.gameCooldown = gameCooldown;
		cooldownRunning = false;
		retryCounter = 0;
		lastRetry = LocalDateTime.now();
	}
	
	public Boolean retry() {
		LocalDateTime time = LocalDateTime.now();
		Long diff = ChronoUnit.SECONDS.between(lastRetry, time);			
		if( diff > gameCooldown) {
			cooldownRunning = false;
			retryCounter = 0;
		}
		
		if(cooldownRunning) return false;

		retryCounter++;
		if(retryCounter > gameRetries) {
			cooldownRunning = true;
			return false;
		}
		lastRetry = LocalDateTime.now();
		
		return true;
	}
	
	public void reset() {
		cooldownRunning = false;
		retryCounter = 0;		
	}
	
	public Boolean isLastRetry() {
		return (retryCounter == gameRetries);
	}
	
	public Long getCooldownLeft() {
		LocalDateTime time = LocalDateTime.now();
		return (gameCooldown - ChronoUnit.SECONDS.between(lastRetry, time));	
	}
}
