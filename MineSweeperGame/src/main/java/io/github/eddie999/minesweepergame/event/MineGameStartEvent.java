package io.github.eddie999.minesweepergame.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import io.github.eddie999.minesweepergame.game.Game;

/*
 * Called when a new game starts.
 * If this event is cancelled then the start is cancelled.
 */
public class MineGameStartEvent extends MineGameSpawnEvent implements Cancellable{
	private boolean cancelled;
	private final Player player;

	public MineGameStartEvent(Game game, Player player) {
		super(game);
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
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
