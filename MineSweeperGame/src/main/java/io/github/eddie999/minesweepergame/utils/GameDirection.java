package io.github.eddie999.minesweepergame.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class GameDirection {
	private static final BlockFace[] FACES = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};
	private static final float[] YAWS = { 0F, 90F, -180, -90F};
	private final Integer index;
	
	public GameDirection(float yaw) {
    	if( (yaw>-45F) && (yaw<=45)) {
    		index = 0;
    	}else if((yaw>45F) && (yaw<=135)) {
    		index = 1;
    	}else if((yaw>135F) || (yaw<=-135)) {
    		index = 2;
    	}else {
    		index = 3;
    	}		
	}

	public BlockFace getFace() {
		return FACES[index];
	}
	
	public float getYaw() {
		return YAWS[index];
	}
	
	public float getRotated(Integer rotate) {
		if( rotate < 0) rotate = 0;
		return YAWS[(index + rotate) % 4];
	}

	public Vector getVector() {
		Vector vec;
		switch(index) {
		case 0:
			vec = new Vector( -1, 0, 1);
			break;
		case 1:
			vec = new Vector( -1, 0, -1);
			break;
		case 2:
			vec = new Vector( 1, 0, -1);
			break;
		default:
			vec = new Vector( 1, 0, 1);
			break;
		}
		return vec;
	}	
	
	public Vector getRowVector() {
		Vector vec;
		switch(index) {
		case 0:
			vec = new Vector( 0, 0, 1);
			break;
		case 1:
			vec = new Vector( -1, 0, 0);
			break;
		case 2:
			vec = new Vector( 0, 0, -1);
			break;
		default:
			vec = new Vector( 1, 0, 0);
			break;
		}
		return vec;
	}

	public Vector getColVector() {
		Vector vec;
		switch(index) {
		case 0:
			vec = new Vector( -1, 0, 0);
			break;
		case 1:
			vec = new Vector( 0, 0, -1);
			break;
		case 2:
			vec = new Vector( 1, 0, 0);
			break;
		default:
			vec = new Vector( 0, 0, 1);
			break;
		}
		return vec;
	}
}
