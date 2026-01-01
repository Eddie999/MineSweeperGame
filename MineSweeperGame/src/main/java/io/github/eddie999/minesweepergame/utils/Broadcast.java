package io.github.eddie999.minesweepergame.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public class Broadcast {

	public static void broadcast(World world, Component message) {
		List<Player> players = world.getPlayers();
		for(Player player : players) {
			player.sendMessage(message);
		}
	}

	public static void broadcast(Component message) {
		List<World> worlds = Bukkit.getWorlds();
		for(World world : worlds) {
			broadcast(world, message);
		}
	}
}
