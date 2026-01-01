package io.github.eddie999.minesweepergame.game;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

public class RewardInventory implements InventoryHolder, Listener {
	private final Inventory inventory;
	private final Game game;
	
	public RewardInventory(JavaPlugin plugin, Game game) {
		this.inventory = plugin.getServer().createInventory(this, InventoryType.DISPENSER, Component.text("Game '" + game.getName() + "' rewards"));
		this.game = game;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public Game getGame() { return this.game; }
	
	@Override
	public @NotNull Inventory getInventory() {
		return this.inventory;
	}
	
    public void remove(){
    	HandlerList.unregisterAll(this);
    }
    
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
	    Inventory inventory = event.getInventory();
	    if (!(inventory.getHolder(false) instanceof RewardInventory rewardInventory)) {
	        return;
	    }
	    if(rewardInventory.getGame().isOpen()) {
	    	event.setCancelled(true);
	    	Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
	    	BukkitScheduler scheduler = plugin.getServer().getScheduler();
	    	scheduler.runTaskLater(plugin, () -> {
	    		rewardInventory.remove();
	    	}, 5);
	    	return;
	    }
	    rewardInventory.getGame().setOpen(true);
	    ItemStack[] items = this.getGame().getRewardItems();
	    if( items != null) {
	    	int index = 0;
	    	for(ItemStack item : items) {
	    		inventory.setItem(index, item);
	    		index++;
	    	}
	    }
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
	    Inventory inventory = event.getInventory();
	    if (!(inventory.getHolder(false) instanceof RewardInventory)) {
	        return;
	    }
		if(event.getClickedInventory() == null) {
	    	Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
	    	BukkitScheduler scheduler = plugin.getServer().getScheduler();
	    	scheduler.runTaskLater(plugin, () -> {
	    		inventory.close();
	    	}, 5);			
		}
	}
		
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
	    Inventory inventory = event.getInventory();
	    if (!(inventory.getHolder(false) instanceof RewardInventory rewardInventory)) {
	        return;
	    }
	    if(!inventory.isEmpty()) {
	    	getGame().setReward(inventory.getContents());
	    	getGame().setReward(0);
	    }
	    rewardInventory.remove();
	    rewardInventory.getGame().setOpen(false);
	}	
}
