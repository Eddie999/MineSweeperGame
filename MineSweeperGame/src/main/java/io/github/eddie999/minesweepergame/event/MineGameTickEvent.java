package io.github.eddie999.minesweepergame.event;

import org.bukkit.entity.Player;

import io.github.eddie999.minesweepergame.game.Game;

/*
 * Called every second for a running game
 */
public class MineGameTickEvent extends MineGameEvent{
	private final Player player;

	public MineGameTickEvent(Game game, Player player) {
		super(game);
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
}
