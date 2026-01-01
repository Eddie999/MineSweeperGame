package io.github.eddie999.minesweepergame.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.eddie999.minesweepergame.event.MineGameStartEvent;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.Lang;

public class GameCooldown implements Listener{
	private static Integer gameRetries = Configs.SETTINGS.getInteger("game-retries");
	private static Integer gameCooldown = Configs.SETTINGS.getInteger("game-cooldown-time");
	private static Map<UUID, GameCounter> gameCounters = new HashMap<UUID, GameCounter>();
	
	public GameCooldown() {
		if( gameRetries == null) gameRetries = 3;
		if( gameCooldown == null) gameCooldown = 1800;
	}
	
	public static void resetCounter(Player player) {
    	GameCounter counter = gameCounters.get(player.getUniqueId());
    	if(counter != null) {
    		counter.reset();
    	}
	}
	
	// Track retries
    @EventHandler
    public void onMineGameStartEvent (MineGameStartEvent event) {
    	if(event.getPlayer().hasPermission("minesweepergame.admin")) return;
    	GameCounter counter = gameCounters.get(event.getPlayer().getUniqueId());
    	if(counter == null) {
    		counter = new GameCounter(gameRetries, gameCooldown);
    		gameCounters.put(event.getPlayer().getUniqueId(), counter);
    	}
    	if(!counter.retry()) {
    		event.setCancelled(true);
    		event.getPlayer().sendMessage(String.format(Lang.translate(event.getPlayer(), "messages.game.cooldown"), counter.getCooldownLeft()));
    	} else if(counter.isLastRetry()) {
    		event.getPlayer().sendMessage(Lang.translate(event.getPlayer(), "messages.game.last-retry"));    		
    	}
    }

}
