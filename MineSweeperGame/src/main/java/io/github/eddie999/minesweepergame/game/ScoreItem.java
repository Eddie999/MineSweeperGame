package io.github.eddie999.minesweepergame.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class ScoreItem {
	private final OfflinePlayer player;
	private final Integer time;
	
	public ScoreItem( OfflinePlayer player, Integer time) {
		this.player = player;
		this.time = time;
	}

	public ScoreItem( String uuid, Integer time) {
		this(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), time);		
	}

	public String getName() { 
		if( player == null) return "Anonymous";
		return player.getName();
	}
	
	/*
	public String getUuid() {
		if( player == null) return null;
		return player.getUniqueId().toString();		
	}
	*/
	
	public int getTime() {
		return time;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();

		if( player == null) data.put("player", "Anonymous");
		else data.put("player", player.getUniqueId().toString());
		data.put("time", time);		
		
		return data;
	}

	public static ScoreItem deserialize(Map<String, Object> data) {
		String uuid = (String) data.get("player");
		Integer time = (Integer) data.get("time");
		ScoreItem item = new ScoreItem(uuid, time);
		return item;
	}

}
