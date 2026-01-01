package io.github.eddie999.minesweepergame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import io.github.eddie999.minesweepergame.support.VaultInterface;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.Lang;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class MineSweeperGamePermissions {
	protected List<String> checkPermissions( Player player, List<String> args){
		List<String> permitted = new ArrayList<>();
		
		for( String arg : args) {
			if( player.hasPermission("minesweepergame.admin") || player.hasPermission("minesweepergame." + arg)) permitted.add(arg);
		}
		
		return permitted;
	}	
	
	protected boolean hasPermission( Player player, String command) {
		String permission = String.format("minesweepergame.%s", command.toLowerCase());
		if( !(player.hasPermission(permission) || player.hasPermission("minesweepergame.admin"))) {
			player.sendMessage(Component.text(ChatColor.RED + Lang.translate(player, "messages.commands.no-permission")));
			return false;
		}
		return true;
	}
	
	protected boolean isAdmin(Player player) {
		return player.hasPermission("minesweepergame.admin");
	}
	
	protected boolean hasVault() {
		return !Configs.SETTINGS.getBoolean("disable-vault-support") && (VaultInterface.getEconomy() != null);
	}

}
