package io.github.eddie999.minesweepergame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import io.github.eddie999.minesweepergame.block.CustomMaterial;
import io.github.eddie999.minesweepergame.game.Game;
import io.github.eddie999.minesweepergame.game.RewardInventory;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.Lang;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class MineSweeperGameCommands extends MineSweeperGamePermissions implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String @NotNull [] args) {
		
		if( args[0].equalsIgnoreCase("create")) {
        	if( !(sender instanceof Player)) return false;
        	Player player = (Player)sender;
        	if( !hasPermission(player, args[0])) return false;
 			return create(player, args);
		}else if( args[0].equalsIgnoreCase("remove")) {
        	if( !(sender instanceof Player)) return false;
        	Player player = (Player)sender;
        	if( !hasPermission(player, args[0])) return false;
 			return remove(player, args);
		}else if( args[0].equalsIgnoreCase("list")) {
        	if( !(sender instanceof Player)) return false;
        	Player player = (Player)sender;
        	if( !hasPermission(player, args[0])) return false;
 			return list(player, args);
		}else if( args[0].equalsIgnoreCase("rename")) {
        	if( !(sender instanceof Player)) return false;
        	Player player = (Player)sender;
        	if( !hasPermission(player, args[0])) return false;
 			return rename(player, args);
		}else if( args[0].equalsIgnoreCase("power")) {
        	if( !(sender instanceof Player)) return false;
        	Player player = (Player)sender;
        	if( !hasPermission(player, args[0])) return false;
 			return power(player, args);
		}else if( args[0].equalsIgnoreCase("starters")) {
        	if( !(sender instanceof Player)) return false;
        	Player player = (Player)sender;
        	if( !hasPermission(player, args[0])) return false;
 			return starters(player, args);
		}else if( args[0].equalsIgnoreCase("reward")) {
			if( !(sender instanceof Player)) return false;
			Player player = (Player)sender;
			if( !hasPermission(player, args[0])) return false;
			return reward(player, args);
		}else if( args[0].equalsIgnoreCase("help")) {
			return help(sender, args);
		}	
		
		return false;
	}
	
	private boolean help(CommandSender sender, String[] args) {
		Lang.translate(sender, "messages.commands.no-object");
		if(args.length < 2) return false;
		boolean validTopic = false;
		for( String topic : MineSweeperGameCompleter.ARGS0) {
			if(topic.equals(args[1].toLowerCase())) {
				validTopic = true;
				break;
			}
		}
		if(!validTopic) return false;
		String help = "help." + args[1].toLowerCase() + ".";
		Integer index = 1;
		Boolean next = true;
		do {
			String topic = help + "line" + index;
			String text = Lang.translate(sender, topic);
			index++;
			if(!text.equals(topic)) sender.sendMessage(Component.text(text));
			else next = false;
		}while(next);
		
		return false;
	}
			
	private boolean create(Player player, String[] args) {
		String name="";
		Integer rows=2;
		Integer cols=2;
		Integer mines=1;
		
		if(args.length >= 6 && args[2].equalsIgnoreCase("custom")) {
			name = args[1].toLowerCase();
			try {
				rows = Integer.parseInt(args[3]);
				cols = Integer.parseInt(args[4]);
				mines = Integer.parseInt(args[5]);
			}catch(NumberFormatException e) {
				Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Creating minefield " + e.getMessage());
				return false;
			}
		} else if(args.length >= 3) {
			name = args[1].toLowerCase();
			if( args[2].equalsIgnoreCase("easy")) {
				rows = 9;
				cols = 9;
				mines = 10;
			}else if( args[2].equalsIgnoreCase("normal")) {
				rows = 16;
				cols = 16;
				mines = 40;    				
			}else if( args[2].equalsIgnoreCase("difficult")) {
				rows = 30;
				cols = 16;
				mines = 99;    				    				
			}else return false;
		}
		
		Integer maxSize = Configs.SETTINGS.getInteger("field-max-size");
		if( (rows>maxSize) || (cols>maxSize)) {
			player.sendMessage(String.format(Lang.translate(player, "messages.commands.field-too-big"), maxSize, maxSize));
			return false;
		}
			
		ItemStack game = createGame(name, rows, cols, mines); 
		giveGame(player, game, null);
		
		return true;
	}
	
	private boolean list(Player player, String[] args) {
    	Game[] games = Game.getGames(player.getUniqueId());
    	if( games == null) return true;
    	
    	if((args.length >= 2) && isAdmin(player) && args[1].equalsIgnoreCase("all")) {
     		for( Game game : games) {
    			player.sendMessage(game.getDisplayId());
    		}
    		return true;
    	}
    	
		for( Game game : games) {
			if(player.getUniqueId().compareTo(game.getOwner()) == 0) player.sendMessage(game.getName());
		}    	
    	        
        return true;
	}

	private boolean rename(Player player, String[] args) {
    	ItemStack item = player.getInventory().getItemInMainHand();
    	CustomMaterial material = CustomMaterial.getMaterial(item);	
    	if( (args.length >= 2) && (material==CustomMaterial.MINE_STARTER_0) && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			ItemMeta meta = item.getItemMeta();
			List<Component> lore = meta.lore();
			lore.set(0, Component.text(args[1]));
			meta.lore(lore);
			item.setItemMeta(meta);
			player.getInventory().setItemInMainHand(item);
			return true;
    	}
		player.sendMessage(String.format(Lang.translate(player, "messages.commands.no-object")));    	
    	return false;
	}

	private boolean power(Player player, String[] args) {
		if((args.length >= 3) && (Game.hasGame(player.getUniqueId(), args[1]))) {
			String id = player.getUniqueId().toString() + "/" + args[1];
			Game game = Game.getGame(id);
			Float power = Float.parseFloat(args[2]);
			if( !game.setExplosionPower(power)) {
				player.sendMessage(String.format(Lang.translate(player, "messages.commands.invalid-power")));    	
		    	return false;				
			}
			return true;
		}
		return false;
	}

	private boolean starters(Player player, String[] args) {
		if((args.length >= 3) && (Game.hasGame(player.getUniqueId(), args[1]))) {
			String id = player.getUniqueId().toString() + "/" + args[1];
			Game game = Game.getGame(id);
			Integer starters = Integer.parseInt(args[2]);
			game.setStarterCount(starters);
			return true;
		}
		return false;
	}	
	
	private boolean reward(Player player, String[] args) {
		String id = "";

		if(args.length >= 3) {
			id = player.getUniqueId().toString() + "/" + args[1];								
	        Game game = Game.getGame(id);
	        if( game != null) {
	        	if(args[2].equalsIgnoreCase("cash") && isAdmin(player) && (args.length >= 4)) {
	        		if(!game.setReward(args[3])) {
	        			player.sendMessage(Lang.translate(player, "messages.commands.invalid-amount"));
	        			return false;	        			
	        		}
	        	}else if(args[2].equalsIgnoreCase("item")) {
	        		RewardInventory rewardInv = new RewardInventory(MineSweeperGame.getPlugin( MineSweeperGame.class), game);
	        		player.openInventory(rewardInv.getInventory());
	        	}else return false;
	        }else return false;
		}

		return true;
	}
	
	
	private boolean remove(Player player, String[] args) {
		String id = "";
		int start;
		Game[] games = Game.getGames();
		if( games == null) return true;
		
		if(args.length >= 3) {
			if(isAdmin(player) && args[1].equalsIgnoreCase("player")) {
				Player gamePlayer = Bukkit.getPlayerExact(args[2]);
				if(gamePlayer == null) {
					player.sendMessage(Lang.translate(player, "messages.commands.invalid-player"));
					return false;
				}
		        for( Game game : games) {
		        	if(game.getOwner().compareTo(gamePlayer.getUniqueId()) == 0) {
		        		player.sendMessage("Remove " + game.getDisplayId());
		        		game.delete();
		        		game.remove();
		        	}
		        }
		        return true;
			}else if(args[1].equalsIgnoreCase("game")){
				if((start = args[2].indexOf("/")) > 0) {
					String owner = args[2].substring(0, start);
					Player gamePlayer = Bukkit.getPlayerExact(owner);
					if(gamePlayer == null) {
						player.sendMessage(Lang.translate(player, "messages.commands.invalid-player"));
						return false;
					}
					if(!isAdmin(player) && !gamePlayer.getUniqueId().equals(player.getUniqueId())) {
						player.sendMessage(Component.text(ChatColor.RED + Lang.translate(player, "messages.commands.no-permission")));
						return false;						
					}
					id = gamePlayer.getUniqueId().toString() + "/" + args[2].substring(start+1);
				} else {
					id = player.getUniqueId().toString() + "/" + args[2];					
				}
			}
		}else return false;
		        
        Game game = Game.getGame(id);
        if( game != null) {
        	player.sendMessage("Remove " + game.getDisplayId());
        	if(!game.ownerIsAdmin()) {
        		ItemStack item = game.getGameObject();
        		ItemStack[] drops = game.getDrops();
        		giveGame(player, item, drops);
        	}
        	game.delete();
        	game.remove();
        }
        
		return true;
	}

	private ItemStack createGame(String name, Integer rows, Integer cols, Integer mines) {
		ItemStack item = CustomMaterial.MINE_STARTER_0.getItem(1);
		ItemMeta meta = item.getItemMeta();
		List<Component> lore = new ArrayList<>();
		lore.add(Component.text(name));
		lore.add(Component.text(String.format("%d", rows)));
		lore.add(Component.text(String.format("%d", cols)));
		lore.add(Component.text(String.format("%d", mines)));
		meta.lore(lore);
		item.setItemMeta(meta);

		return item;
	}
	
	
	private void giveGame(Player player, ItemStack game, ItemStack[] drops) {
		if( game != null) {
			HashMap<Integer,ItemStack> overflow = player.getInventory().addItem(game);
			if( overflow.size() > 0) {
				overflow.clear();
				player.getLocation().getWorld().dropItem(player.getLocation(), game);
			}				
		}
		if((drops!=null) && (drops.length>0)) {
			for(ItemStack drop : drops) {
				HashMap<Integer,ItemStack> overflow = player.getInventory().addItem(drop);
				if( overflow.size() > 0) {
					overflow.clear();
					player.getLocation().getWorld().dropItem(player.getLocation(), drop);
				}								
			}
		}
	}
}
