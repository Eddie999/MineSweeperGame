package io.github.eddie999.minesweepergame.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import io.github.eddie999.minesweepergame.block.Mine;
import io.github.eddie999.minesweepergame.game.Game;

/*
 * Called when a game is ended.
 */
public class MineGameEndedEvent extends MineGameEvent{
	public enum GameResult {
		CANCELLED,
		FAILED,
		SUCCESS;
	}
	
	private final Player player;
	private final GameResult result;
	private Mine mine;

	public MineGameEndedEvent(Game game, Player player, GameResult result, @Nullable Mine mine) {
		super(game);
		this.player = player;
		this.result = result;
		this.mine = mine;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public GameResult getResult() {
		return result;
	}
	
	public Mine getMine() {
		return mine;
	}
}
