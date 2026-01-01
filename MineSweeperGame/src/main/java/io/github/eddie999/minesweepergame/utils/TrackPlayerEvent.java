package io.github.eddie999.minesweepergame.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TrackPlayerEvent {
	public class PlayerEvent{
		private final LocalDateTime time;
		private final String event;
		private final Block block;
		
		public PlayerEvent(String event, Block block) {
			time = LocalDateTime.now();
			this.event = event;
			this.block = block;
		}
		
		public Long getAge() {
			return ChronoUnit.MILLIS.between(time, LocalDateTime.now());
		}
		
		public String getEvent() {
			return event;
		}
		
		public Boolean equalsBlock(Block block) {
			return this.block.getLocation().equals(block.getLocation());
		}
	}
	
	private final Map<UUID, PlayerEvent> events;
	private final Integer timeout;
	
	public TrackPlayerEvent() {
		events = new HashMap<UUID, PlayerEvent>();
		timeout = 2000;
	}

	public void add(Player player, String event, Block block) {
		if(!events.isEmpty()) events.remove(player.getUniqueId()); 
		events.put(player.getUniqueId(), new PlayerEvent(event, block));
	}
	
	public Boolean match(Player player, String event, Block block) {
		refresh();
		if(events.isEmpty()) return false;
		PlayerEvent item = events.get(player.getUniqueId());
		if(item != null) {
			if(item.getEvent().equals(event) && item.equalsBlock(block)) {
				if(item.getAge() < 500) return true; 
			}
		}
		return false;
	}
	
	private void refresh() {
		if(events.isEmpty()) return;
		Iterator<Map.Entry<UUID, PlayerEvent>> iterator = events.entrySet().iterator();
		do {
			Map.Entry<UUID, PlayerEvent> event = iterator.next();
			if( event.getValue().getAge() > timeout) events.remove(event.getKey());
		}while(iterator.hasNext());
	}
}
