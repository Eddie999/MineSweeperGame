package io.github.eddie999.minesweepergame.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.Nullable;

import io.github.eddie999.minesweepergame.game.Game;

public class MineGamePlaceFlagEvent extends MineGameEvent implements Cancellable{
	private boolean cancelled;
	private final Player player;
	private final Location location;
	private final boolean flagInHand;

	public MineGamePlaceFlagEvent(@Nullable Game game, Player player, Location location, boolean flagInHand) {
		super(game);
		this.player = player;
		this.location = location;
		this.flagInHand = flagInHand;
	}
	
	public Player getPlayer() {return player;}
	public Location getLocation() {return location;}
	public boolean isFlagInHand() {return flagInHand;}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
