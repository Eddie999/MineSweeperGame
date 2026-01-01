package io.github.eddie999.minesweepergame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.kyori.adventure.text.Component;

public class GameboardBuilder {
	private final Scoreboard board;
	private final String id;
	private Objective obj;
	private List<Score> scores;
	private HashMap<String,String> numberFormats;
	
	public GameboardBuilder( Game game) {
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		//this.id = "MineSweeperGame-" + game.getOwner().toString() + "-" + game.getName();
		this.id = "MineSweeperGame-" + game.getId();
		this.obj = null;
		this.scores = new ArrayList<Score>();
		this.numberFormats = new HashMap<>();
	}
	
	public GameboardBuilder objective( Component displayName) {
		this.obj = this.board.registerNewObjective(this.id, Criteria.DUMMY, displayName);	
		return this;
	}

	public GameboardBuilder slot( DisplaySlot slot) {
		if( this.obj != null) {
			this.obj.setDisplaySlot(slot);
		}
		return this;
	}
	
	public GameboardBuilder score( String entry, Component displayText) {
		if( this.obj == null) return this;
		Score score = obj.getScore(entry);
		score.customName(displayText);
		scores.add(score);
		return this;
	}
	
	public GameboardBuilder score( String entry, Component displayText, String format) {
		if( this.obj == null) return this;
		Score score = obj.getScore(entry);
		score.customName(displayText);
		scores.add(score);
		numberFormats.put(entry, format);
		return this;
	}
	
	public Gameboard build() {
		if( this.obj == null) return null;
		Gameboard gb = null;
		
		int value = scores.size();
		for( Score score : scores) {
			score.setScore(value);
			value--;
		}
		scores.clear();
		gb = new Gameboard( this.board, this.id, this.numberFormats);
		return gb;
	}
}
