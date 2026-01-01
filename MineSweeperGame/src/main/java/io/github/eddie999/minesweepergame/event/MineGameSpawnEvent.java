package io.github.eddie999.minesweepergame.event;

import org.bukkit.Location;

import io.github.eddie999.minesweepergame.game.Game;

/*
 * Called when a new game is spawn.
 */
public class MineGameSpawnEvent extends MineGameEvent{

	public MineGameSpawnEvent(Game game) {
		super(game);
	}
	
	public Location getLocation() {
		return getGame().getBlock().getLocation().getBlock().getLocation();
	}

}
