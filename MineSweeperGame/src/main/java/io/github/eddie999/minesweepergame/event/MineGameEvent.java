package io.github.eddie999.minesweepergame.event;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.eddie999.minesweepergame.game.Game;

public class MineGameEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final Game game;
    
    public MineGameEvent(@Nullable Game game) {
    	this.game = game;
    }
 
    public Game getGame() { return game;}

    public OfflinePlayer getOwner() {
    	if(game == null) return null;
		return Bukkit.getOfflinePlayer(getGame().getOwner());
	}
    
	@Override
	public @NotNull HandlerList getHandlers() {
		return MineGameEvent.handlers;
	}
	
    public static HandlerList getHandlerList() {
        return MineGameEvent.handlers;
    }

}
