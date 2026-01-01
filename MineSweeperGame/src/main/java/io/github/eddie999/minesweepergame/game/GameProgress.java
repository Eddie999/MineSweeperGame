package io.github.eddie999.minesweepergame.game;

public class GameProgress {
	private final Integer mines;
	private final Integer blocks;
	private Integer minesLeft;
	private Integer blocksLeft;
	private Integer flagsLeft;
	private Integer tick;
	
	public Integer getMinesLeft() { return minesLeft;}
	public Integer getBlocksLeft() { return blocksLeft;}
	public Integer getFlagsLeft() { return flagsLeft;}
	public Integer getTick() { return tick;}
	
	public GameProgress(Integer mines, Integer blocks) {
		this.mines = mines;
		this.blocks = blocks;
		reset();
	}
	
	public void reset() {
		minesLeft = mines;
		blocksLeft = blocks;
		flagsLeft = mines;
		tick = 0;		
	}
	
	public void placeFlag() {
		minesLeft--;
		if(minesLeft<0) minesLeft = 0;
		flagsLeft--;
		if(flagsLeft<0) flagsLeft = 0;
		blocksLeft--;
		if(blocksLeft<0) blocksLeft = 0;
	}

	public void removeFlag() {
		minesLeft++;
		if(minesLeft>mines) minesLeft = mines;
		flagsLeft++;
		if(flagsLeft>mines) flagsLeft = mines;
		blocksLeft++;
		if(blocksLeft>blocks) blocksLeft = blocks;
	}
	
	public void unhideBlock(boolean mine) {
		blocksLeft--;
		if(blocksLeft<0) blocksLeft = 0;
		if(mine) minesLeft--;
		if(minesLeft<0) minesLeft = 0;
	}
	
	public void setBlocksLeft(int blocksLeft) {
		if((blocksLeft >= 0) && (blocksLeft <= blocks)) this.blocksLeft = blocksLeft;
	}

	public void tick() {
		tick++;
	}
}
