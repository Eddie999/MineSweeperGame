package io.github.eddie999.minesweepergame.support;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public interface WorldGuardInterface {
	
	public static boolean isInstalled() {
		return (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null);
	}
	
	public static boolean canBypass( Player player) {
		if(!isInstalled()) return true;
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		World localWorld = localPlayer.getWorld();
		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass( localPlayer, localWorld);
	}
	
	public static boolean canBuild( Player player, org.bukkit.Location location) {
		if( canBypass( player)) return true;
		
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		Location loc = new Location( localPlayer.getWorld(), location.getX(), location.getY(), location.getZ());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		return query.testState( loc, localPlayer, Flags.BUILD);
	}
	
	public static boolean canBuild( Player player, List<org.bukkit.Location> locations) {
		boolean result = true;
		
		for( org.bukkit.Location loc : locations) {
			if( !canBuild( player, loc)) result = false;
		}
		
		return result;
	}
}
