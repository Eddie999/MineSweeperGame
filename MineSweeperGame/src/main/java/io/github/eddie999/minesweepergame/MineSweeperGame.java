package io.github.eddie999.minesweepergame;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import io.github.eddie999.minesweepergame.event.MineGameTickEvent;
import io.github.eddie999.minesweepergame.game.Game;
import io.github.eddie999.minesweepergame.game.GameCooldown;
import io.github.eddie999.minesweepergame.utils.CheckServer;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.PluginLanguages;
import io.github.eddie999.minesweepergame.utils.ResourcePack;

public class MineSweeperGame extends JavaPlugin{
	public Integer taskId = -1;
	public Map<String, String> languageMap = new HashMap<>();
	public static PluginLanguages languages;
	
	@Override
    public void onEnable() {
		saveDefaultConfig();
		try {
			Class.forName("io.papermc.paper.ServerBuildInfo");
			CheckServer.isCompatible(this);
		}catch(ClassNotFoundException e) {
			this.getLogger().log(Level.SEVERE, "This plugin is using Paper API!");
			return;
		}
		
		languages = new PluginLanguages(this, Configs.SETTINGS.get("language"), Configs.SETTINGS.getBoolean("use-player-locale-language"));
		
		if( !Configs.SETTINGS.getBoolean("disable-worldguard-support") && (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null)) {
			this.getLogger().log(Level.INFO, "WorldGuard support is activated.");
		} else this.getLogger().log(Level.INFO, "No WorldGuard support.");

		if( !Configs.SETTINGS.getBoolean("disable-vault-support") && (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)) {
			this.getLogger().log(Level.INFO, "Vault support is activated.");
		} else this.getLogger().log(Level.INFO, "No Vault support.");
		
    	if(!Configs.SETTINGS.getBoolean("disable-plugin-resourcepack-load")) 
    		this.getLogger().log(Level.INFO, "This plugin loads the following resourcepack:");
    	else {
    		this.getLogger().log(Level.WARNING, "Plugin resourcepack loading is disabled!");
    		this.getLogger().log(Level.INFO, "This plugin needs the following resourcepack:");
    	}
    	this.getLogger().log(Level.INFO, "URI : " + ResourcePack.resourceUri);			
    	this.getLogger().log(Level.INFO, "HASH: " + ResourcePack.resourceHash);			
		
		List<String> worldNames = Configs.SETTINGS.getStringList("build-world-name");
		if((worldNames != null) && (worldNames.size() > 0)) {
			for( String worldName : worldNames) {
				int start = 0;
				if(((start = worldName.indexOf(".")) > 0) && (start+1 < worldName.length())) {
					String keyNamespace = worldName.substring(0, start);
					String keyWorld = worldName.substring(start+1);
					World world = getServer().getWorld(new NamespacedKey(keyNamespace, keyWorld));
					if(world != null) {
						getLogger().log(Level.INFO, String.format("Restore games in %s.", worldName));
						Chunk[] chunks = world.getLoadedChunks();
						for( Chunk chunk : chunks) {
							Game.restore(chunk);
						}						
					}else {
						getLogger().log(Level.WARNING, String.format("Invalid world %s.", worldName));						
					}
				}
			}
		}
		
		getServer().getPluginManager().registerEvents( new MineSweeperGameListener(), this);
		getServer().getPluginManager().registerEvents( new GameCooldown(), this);
		
		CommandExecutor msgCommand = new MineSweeperGameCommands();
		this.getCommand("minesweepergame").setExecutor( msgCommand);
		this.getCommand("ms").setExecutor( msgCommand);
	    this.getCommand("minesweepergame").setTabCompleter(new MineSweeperGameCompleter());
	    
	    if((taskId = startTickTask()) < 0) {
	    	getLogger().log(Level.SEVERE, "Failed to start the MineGameTickTask!");
	    }
	}
	
	@Override
    public void onDisable() {
		stopTickTask(taskId);
    	Game.saveGames();
    	Game.removeGames();
    	
    	//Clean up undeleted TextDisplays
    	List<World> worlds = this.getServer().getWorlds();
    	for(World world : worlds) {
    		Collection<TextDisplay> collection = world.getEntitiesByClass(TextDisplay.class);
    		for( TextDisplay display : collection) {
    			if( display.text().toString().indexOf("MineSweeperGame") >= 0) display.remove();
    		}    		
    	}
	}
	
	private Integer startTickTask() {
		BukkitScheduler scheduler = getServer().getScheduler();
		Integer taskId = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Game[] games = Game.getGames();
				if( (games != null) && (games.length > 0)) {
					for( Game game : games) {
						if((game != null) && game.isRunning()) {
							MineGameTickEvent tickEvent = new MineGameTickEvent(game, game.getPlayer());
							Bukkit.getPluginManager().callEvent(tickEvent);
						}
					}
				}
			}			
		}, 0L, 20L);
		
		return taskId;
	}
	
	private void stopTickTask(Integer taskId) {
		getServer().getScheduler().cancelTask(taskId);
	}
	
}
