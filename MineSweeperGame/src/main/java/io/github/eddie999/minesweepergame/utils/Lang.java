package io.github.eddie999.minesweepergame.utils;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.eddie999.minesweepergame.MineSweeperGame;

public interface Lang {
    public static String translate( String key) {
    	return MineSweeperGame.languages.translate( key);
    }

    public static String translate( Player player, String key) {
    	String result = null;
    	if(Configs.SETTINGS.getBoolean("use-player-locale-language")) {
    		Locale locale = player.locale();
        	result = MineSweeperGame.languages.translate( locale.toLanguageTag().toLowerCase(), key);
        } else result = MineSweeperGame.languages.translate( key);
    	return result;
    }
    
    public static String translate( CommandSender sender, String key) {
    	String result = null;
    	if( sender instanceof Player) {
    		Player player = (Player)sender;
    		result = translate( player, key);
    	} else result = translate( key);
    	return result;
    }	
}
