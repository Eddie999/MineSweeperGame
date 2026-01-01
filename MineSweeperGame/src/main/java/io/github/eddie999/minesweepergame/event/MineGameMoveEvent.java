package io.github.eddie999.minesweepergame.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import io.github.eddie999.minesweepergame.block.Mine;
import io.github.eddie999.minesweepergame.game.Game;
import io.github.eddie999.minesweepergame.mine.MineField;
import io.github.eddie999.minesweepergame.mine.MineMatrix;

public class MineGameMoveEvent extends MineGameEvent implements Cancellable{
	private boolean cancelled;
	private final Player player;
	private final MineField field;
	private final Mine mine;
	private final MineMatrix.Pos matrixPos;
	private final Integer matrixValue;

	public MineGameMoveEvent(Game game, Player player, MineField field, Mine mine, MineMatrix.Pos matrixPos, Integer matrixValue) {
		super(game);
		this.player = player;
		this.field = field;
		this.mine = mine;
		this.matrixPos = matrixPos;
		this.matrixValue = matrixValue;
	}
	
	public Player getPlayer() {return player;}
	public MineField getField() {return field;}
	public Mine getMine() {return mine;}
	public Integer getX() {return matrixPos.getX();}
	public Integer getY() {return matrixPos.getY();}
	public Integer getValue() {return matrixValue;}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
