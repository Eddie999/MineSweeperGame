package io.github.eddie999.minesweepergame.event;

import org.bukkit.event.Cancellable;

import io.github.eddie999.minesweepergame.game.Game;

/*
 * Called when a new game is loaded.
 * If this event is cancelled then the game is not spawn.
 */
public class MineGameLoadEvent extends MineGameEvent implements Cancellable{
	private boolean cancelled;
	
	public MineGameLoadEvent(Game game) {
		super(game);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
