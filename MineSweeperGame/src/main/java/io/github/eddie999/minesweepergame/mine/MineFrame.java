package io.github.eddie999.minesweepergame.mine;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class MineFrame {
	private World world;
	private Integer minX;
	private Integer maxX;
	private Integer Y;
	private Integer minZ;
	private Integer maxZ;
	
	public MineFrame(@NotNull Location[] locations) {
		world = locations[0].getWorld();
		minX = locations[0].getBlockX();
		maxX = locations[0].getBlockX();
		Y = locations[0].getBlockY();
		minZ = locations[0].getBlockZ();
		maxZ = locations[0].getBlockZ();
		for( Location loc : locations) {
			if( loc.getBlockX() < minX) minX = loc.getBlockX();
			if( loc.getBlockX() > maxX) maxX = loc.getBlockX();
			if( loc.getBlockZ() < minZ) minZ = loc.getBlockZ();
			if( loc.getBlockZ() > maxZ) maxZ = loc.getBlockZ();
		}
	}
	
	public boolean isInside(Location location) {
		if((location==null) || !location.getWorld().equals(world)) return false;
		return ((location.getBlockX()>=minX) && (location.getBlockX()<=maxX) && (location.getBlockY()==Y) &&
				(location.getBlockZ()>=minZ) && (location.getBlockZ()<=maxZ));
	}

	public Integer distance(Location location) {
		if((location==null) || isInside(location)) return 0;
		if(!location.getWorld().equals(world)) return Integer.MAX_VALUE;
		
		Integer centerX = minX + (maxX-minX)/2;
		Integer centerZ = minZ + (maxZ-minZ)/2;
		Location center = world.getBlockAt(centerX, Y, centerZ).getLocation();
		Location corner = world.getBlockAt(minX, Y, minZ).getLocation();
		Double distance = location.distance(center);
		if(distance.isNaN()) return Integer.MAX_VALUE;
		Double radius = corner.distance(center);
		
		return (distance.intValue() - radius.intValue());
	}
}
