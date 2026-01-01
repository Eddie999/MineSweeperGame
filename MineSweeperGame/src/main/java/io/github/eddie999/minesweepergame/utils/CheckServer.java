package io.github.eddie999.minesweepergame.utils;

import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.key.Key;

public class CheckServer {
	public static Boolean isCompatible(Plugin plugin) {
		try {
			ServerBuildInfo server = ServerBuildInfo.buildInfo();
			if(server != null) {
				if(server.isBrandCompatible(Key.key("papermc", "paper"))) return true;
				plugin.getLogger().log(Level.SEVERE, "This plugin is using Paper API!");
			}
		}catch(NoSuchElementException e) {
			plugin.getLogger().log(Level.SEVERE, "This plugin is using Paper API!");
		}
		
		return false;
	}
}
