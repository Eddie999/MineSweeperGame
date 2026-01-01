package io.github.eddie999.minesweepergame.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import io.github.eddie999.minesweepergame.game.Game;

public class GameArea {
	private final Location location;
	private final Integer rows;
	private final Integer cols;
	private List<Location> corners = new ArrayList<Location>();
	
	public GameArea(Location location, Integer rows, Integer cols) {
		if(location == null || rows == null || cols == null || rows < 2 || cols < 2) {
			this.location = null;
			this.rows = 0;
			this.cols = 0;
			return;
		}
		this.location = location;
		this.rows = rows;
		this.cols = cols;
		setCorners();
	}
	
	public GameArea(Game game) {
		 this(game.getBlock().getLocation(), game.getRows(), game.getCols());
	}
	
	public Boolean isOverlapping(GameArea area) {
		if(!area.isValid()) return false;
		List<Location> areaCorners = area.getCorners();
		for( Location corner: areaCorners) {
			if(isInside(corner)) return true;
		}
		
		return false;
	}
	
	public Boolean isInside(Location loc) {
		if((loc.getY() >= corners.get(0).getY()) && (loc.getY() <= corners.get(0).getY()+3)) {
			Double minX = 10000000.0;
			Double maxX = -10000000.0;
			Double minZ = 10000000.0;
			Double maxZ = -10000000.0;
			for( Location corner: corners) {
				if( corner.getX() < minX) minX = corner.getX();  
				if( corner.getX() > maxX) maxX = corner.getX();  
				if( corner.getZ() < minZ) minZ = corner.getZ();  
				if( corner.getZ() > maxZ) maxZ = corner.getZ();  
			}
			if((loc.getX()>=minX) && (loc.getX()<=maxX) && (loc.getZ()>=minZ) && (loc.getZ()<=maxZ)) return true;
		}
		
		return false;
	}
	
	private void setCorners() {
		corners.add(getCorner(0, 0));
		corners.add(getCorner(0, cols));		
		corners.add(getCorner(rows, cols));		
		corners.add(getCorner(rows, 0));		
	}
	
	private Location getCorner( Integer row, Integer col) {
		Location corner = location.clone();
		GameDirection direction = new GameDirection(corner.getYaw());
		if(row != 0) corner.add(direction.getRowVector().multiply(row));
		if(col != 0) corner.add(direction.getColVector().multiply(col));		
		return corner;
	}
	
	public boolean isValid() {
		return location != null;
	}
	
	public List<Location> getCorners() { return corners;}
}
