package io.github.eddie999.minesweepergame.game;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class Gameboard {
	private Scoreboard board;
	private Scoreboard oldBoard;
	private final String objective;
	private final HashMap<String,String> scores;
	private HashMap<String,Component> labels;
	
	public Gameboard( Scoreboard board, String objective, HashMap<String,String> scores) {
		this.board = board;
		this.objective = objective;
		this.scores = scores;
		labels = new HashMap<>();
		scores.forEach(
			(entry, score) 
				-> labels.put(entry, board.getObjective(objective).getScore(entry).customName())
		);
	}
	
	public Scoreboard getScoreboard() {
		return this.board;
	}
	
	public void saveCurrentBoard(Player player) {
		oldBoard = player.getScoreboard();
		if(oldBoard == null) oldBoard = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	public void restorePreviousBoard(Player player) {
		player.setScoreboard(oldBoard);
	}
	
	public void clear() {
		this.scores.clear();
		this.labels.clear();
		this.board = null;
	}
	
	public void setEntry(String entry, int value) {
		String strValue = String.format( scores.get(entry), value);
		updateScore( entry, strValue);
	}
	
	public void setEntry(String entry, float value) {
		String strValue = String.format( scores.get(entry), value);
		updateScore( entry, strValue);		
	}

	public void setEntry(String entry, String value) {
		String strValue = String.format( scores.get(entry), value);
		updateScore( entry, strValue);		
	}

	private void updateScore( String entry, String newScore) {
		TextComponent text = Component.text("").append( labels.get(entry)).append(Component.text(newScore));
		Score score = this.board.getObjective(objective).getScore(entry);
		score.customName(text);		
	}
}
