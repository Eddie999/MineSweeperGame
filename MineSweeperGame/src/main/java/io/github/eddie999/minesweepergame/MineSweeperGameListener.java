package io.github.eddie999.minesweepergame;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import io.github.eddie999.minesweepergame.block.CustomMaterial;
import io.github.eddie999.minesweepergame.block.MineStarter;
import io.github.eddie999.minesweepergame.event.MineGameEndedEvent;
import io.github.eddie999.minesweepergame.event.MineGameEndedEvent.GameResult;
import io.github.eddie999.minesweepergame.event.MineGameLoadEvent;
import io.github.eddie999.minesweepergame.event.MineGamePlaceFlagEvent;
import io.github.eddie999.minesweepergame.event.MineGameSpawnEvent;
import io.github.eddie999.minesweepergame.event.MineGameStartEvent;
import io.github.eddie999.minesweepergame.event.MineGameTickEvent;
import io.github.eddie999.minesweepergame.game.Game;
import io.github.eddie999.minesweepergame.mine.MineField;
import io.github.eddie999.minesweepergame.support.VaultInterface;
import io.github.eddie999.minesweepergame.support.WorldGuardInterface;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.GameArea;
import io.github.eddie999.minesweepergame.utils.Lang;
import io.github.eddie999.minesweepergame.utils.ResourcePack;
import io.github.eddie999.minesweepergame.utils.TrackPlayerEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class MineSweeperGameListener implements Listener {
	private TrackPlayerEvent eventQueue;
	
	public MineSweeperGameListener() {
		eventQueue = new TrackPlayerEvent();
	}
	
	@EventHandler
	public void onServerLoadEvent(ServerLoadEvent event) {
		if( Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			String currency = VaultInterface.getCurrencyName();
			if( currency != null) {
				Bukkit.getLogger().log(Level.INFO, "[MineSweeperGame] Currency: " + currency);
				Game.setCurrencyName(currency);
			}
		}
	}
	
	// Run game scheduler
	@EventHandler
	public void onMineGameTickEvent(MineGameTickEvent event) {
		event.getGame().onTick();
	}

	// Add resource pack when player joins
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if(!Configs.SETTINGS.getBoolean("disable-plugin-resourcepack-load")) {
			Player player = event.getPlayer();	
			ResourcePack.sendResourcePacks(player);
		}
		Player player = event.getPlayer();
		Locale locale = player.locale();
		Bukkit.getLogger().log(Level.INFO, String.format("[MineSweeperGame] Player %s has local language %s.", 
				player.getName(), locale.toLanguageTag().toLowerCase()));
	}
	
	// Stop game when player leaves
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Game[] games = Game.getGames();
		if( (games != null) && (games.length > 0)) {
			for( Game game : games) {
				if((game != null) && game.isRunning()) {
					if( game.getPlayer().equals(event.getPlayer())) {
						game.stop();
       	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(game, event.getPlayer(), GameResult.CANCELLED, null);
    					Bukkit.getPluginManager().callEvent(endedEvent);    	
					}
				}
			}
		}		
	}
	
    // Protect blocks when mine explode
    @EventHandler
    public void onEntityExplodeEvent (EntityExplodeEvent event) {
    	if( event.getEntity() instanceof ItemDisplay) {
        	ItemDisplay display = (ItemDisplay)event.getEntity();
        	CustomMaterial material = CustomMaterial.getMaterial(display.getItemStack());
        	if( material != null) {
            	if( material == CustomMaterial.MINE_9) {
					event.blockList().clear();
            	}
        	}
    	}
    }    
	
    // Place a new mine field
    @EventHandler
    public void onBlockPlaceEvent (BlockPlaceEvent event) {
    	ItemStack item = event.getItemInHand();
    	CustomMaterial material = CustomMaterial.getMaterial(item);
    	if( event.canBuild() && (material!=null) && (material==CustomMaterial.MINE_STARTER_0)) {
    		if( item.hasItemMeta() && item.getItemMeta().hasLore()) {
    			List<Component> lore = item.getItemMeta().lore();
    			if( lore.size() == 4) {
    	        	Player player = event.getPlayer();
    	    		String name = ((TextComponent)lore.get(0)).content();
    	    		String rows = ((TextComponent)lore.get(1)).content();
    	    		String cols = ((TextComponent)lore.get(2)).content();
    	    		String mines = ((TextComponent)lore.get(3)).content();
    	    		
    	    		Location loc = event.getBlock().getLocation().clone();
    	    		loc.setYaw(player.getYaw());
    	    		
    	    		List<String> worldNames = Configs.SETTINGS.getStringList("build-world-name");    	    		
    	    		if((worldNames != null) && (worldNames.size() > 0)) {
    	    			Boolean match = false;
    	    			int start = 0;
    	    			for( String worldName : worldNames) {
    	    				if(((start = worldName.indexOf(".")) > 0) && (start+1 < worldName.length())) {
    	    					String keyNamespace = worldName.substring(0, start);
    	    					String keyWorld = worldName.substring(start+1);
    	    					NamespacedKey key = new NamespacedKey(keyNamespace, keyWorld);
    	    					if( loc.getWorld().getKey().equals(key)) match = true;
    	    				}
    	    			}
    	    			if( !match){
    	    				player.sendMessage(String.format(Lang.translate(player, "messages.build.invalid-world")));
    	    				event.setCancelled(true);
    	    				return;    	    				
    	    			}
    	    		}
    	    		
	    			GameArea area = new GameArea(loc, Integer.valueOf(rows), Integer.valueOf(cols));
    	    		if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
    	    			if( !WorldGuardInterface.canBuild(player, area.getCorners())) {
    	    				player.sendMessage(String.format(Lang.translate(player, "messages.build.no-permission")));
    	    				event.setCancelled(true);
    	    				return;
    	    			}
    	    		}
    	    		if( Game.isOverlapping(area)) {
	    				player.sendMessage(String.format(Lang.translate(player, "messages.build.overlap")));
	    				event.setCancelled(true);
	    				return;    	    			
    	    		}
    	    		
    	    		if( Game.hasGame(player.getUniqueId(), name)) {
    	    			player.sendMessage(String.format(Lang.translate(player, "messages.build.name-in-use")));
    	    			event.setCancelled(true);
    	    			return;			
    	    		}
    	    			
    	    		Game game = new Game(player.getUniqueId(), name, player.hasPermission("minesweepergame.admin"));
    	    		MineGameLoadEvent loadEvent = new MineGameLoadEvent(game);
    	    		Bukkit.getPluginManager().callEvent(loadEvent);
    	    		if( loadEvent.isCancelled()) {
    	    			game.remove();
    	    			event.setCancelled(true);
    	    			return;
    	    		}
    	    		MineStarter starter = new MineStarter(loc);   	    		
    	    		MineField field = new MineField(Integer.valueOf(rows), Integer.valueOf(cols), Integer.valueOf(mines));
    	    		
    	    		game.place(starter, field);
    	    		game.show();
    	    		game.save();
    	    		player.getInventory().removeItem(item);
    	    		MineGameSpawnEvent spawnEvent = new MineGameSpawnEvent(game);
    	    		Bukkit.getPluginManager().callEvent(spawnEvent);
     			}
    		}
    	} else if((material!=null) && (material==CustomMaterial.MINE_FLAG)) {
    		if( item.hasItemMeta() && item.getItemMeta().hasLore()) {
    			List<Component> lore = item.getItemMeta().lore();
    			if( lore.size() >= 1) {
    				String id = ((TextComponent)lore.get(0)).content();
    				if( Game.hasGame(id)) {
    					Game game = Game.getGame(id);
    					MineGamePlaceFlagEvent flagEvent = new MineGamePlaceFlagEvent(game, event.getPlayer(), event.getBlock().getLocation(), true);
    					Bukkit.getPluginManager().callEvent(flagEvent);
    					if(flagEvent.isCancelled()) event.setCancelled(true);
    					else if(game.getBlocksLeft() == 0){
           	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(game, game.getPlayer(), GameResult.SUCCESS, null);
        					game.stop();
        					Bukkit.getPluginManager().callEvent(endedEvent);    	   						
    					}
    				}
    			}
    		}
    	} else {
    		if(eventQueue.match(event.getPlayer(), "PlayerInteractEvent", event.getBlockAgainst())) {
        		event.setCancelled(true);
    		}
    	}
    }
    
    // Handle restart of mine field
    @EventHandler
    public void onPlayerInteractEvent (PlayerInteractEvent event) {
    	if( (event.getClickedBlock() != null) && (event.getClickedBlock().getType().equals(Material.BARRIER))) {
    		if((event.getHand() != null) && (event.getHand().equals(EquipmentSlot.HAND))) {
    			Game game = Game.getGame(event.getClickedBlock()); 
    			if(game != null) {
    				if(!game.isRunning()) {
    					if( event.getPlayer().getInventory().getItemInMainHand().isEmpty()){    					
    						MineGameStartEvent startEvent = new MineGameStartEvent(game, event.getPlayer());
    						Bukkit.getPluginManager().callEvent(startEvent);    					
    						if(!startEvent.isCancelled()) {
    							game.start(event.getPlayer());
    							event.getPlayer().swingMainHand();
    						}
    					}else {
    						event.getPlayer().sendMessage(String.format(Lang.translate(event.getPlayer(), "messages.game.empty-hand")));
    					}
    				}
    				else {
       	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(game, game.getPlayer(), GameResult.CANCELLED, null);
    					game.stop();
    					Bukkit.getPluginManager().callEvent(endedEvent);    	
						event.getPlayer().swingMainHand();
			    		eventQueue.add(event.getPlayer(), event.getEventName(), event.getClickedBlock());
    				}
    			}
    		}
    	} else if((event.getHand() != null) && (event.getHand().equals(EquipmentSlot.HAND))) {
    		ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
    		if(item.getType().equals(Material.AIR)) {
    			Scoreboard board = event.getPlayer().getScoreboard();
    			if( board != null) {
    				Objective[] objs = board.getObjectives().toArray(new Objective[0]);
    				if( objs.length > 0) {
    					for(Objective obj : objs) {
    						if((obj != null) && (obj.getName().indexOf("MineSweeperGame") >= 0)){
    							Integer pos = obj.getName().indexOf("-");
    							String id = obj.getName().substring(pos+1);
            					MineGamePlaceFlagEvent flagEvent = new MineGamePlaceFlagEvent(Game.getGame(id), event.getPlayer(), 
            							event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), false);
            					Bukkit.getPluginManager().callEvent(flagEvent);
            					//Bukkit.getLogger().info("[MineSweeperGame] Interact event " + flagEvent.isCancelled());
            					if(!flagEvent.isCancelled()) event.getPlayer().swingMainHand();
    						}
    					}
    				}
    			}
    		}
    	}
    }

    // Show chunk games
    @EventHandler
    public void onChunkLoadEvent (ChunkLoadEvent event) {
    	Game.restore(event.getChunk());
    }
         
    // Store chunk games
    @EventHandler
    public void onChunkUnloadEvent (ChunkUnloadEvent event) {
     	Map<String, Game> games = Game.getChunkGames(event.getChunk());
    	if(games.isEmpty()) return;
		 for( Entry<String, Game> game : games.entrySet()) {
			 game.getValue().save();
		 }    	
    }
    
}
