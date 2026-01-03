package io.github.eddie999.minesweepergame.game;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.plugin.Plugin;

import io.github.eddie999.minesweepergame.utils.PersistentStringStorage;

import org.bukkit.entity.TextDisplay;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class RewardDisplay {
	private Location location;
	private Display[] entities;

	public RewardDisplay(Location location) {
		this.location = location;
	}
	
	public void showDisplay(GameReward reward) {
		if((entities != null) && (entities.length > 0)) removeDisplay();
		placeDisplay(this.location, reward);		
	}
	
    public void removeDisplay() {
    	if(this.entities == null) return;
    	for(int i=0; i<entities.length; i++) {
    		if( entities[i] != null) {
    			deleteEntityTag( entities[i]); 
    			entities[i].remove();
    		}
        	entities[i] = null;
    	}
    	this.entities = null;
    }
	
	private void placeDisplay( Location location, GameReward reward) {
		Location entityLocation = getLocationFromBlock(location);
		if(reward.hasAmount()) {
			this.entities = new Display[2];
			String amountText = ChatColor.YELLOW + reward.getAmount().toString();
			String currency = reward.getCurrency();
			if( currency != null) amountText = amountText + " " + currency; 
			TextComponent amount = Component.text(amountText);
			location.getWorld().spawn(entityLocation, TextDisplay.class, entity -> {
	        	entity.text(amount);
	        	entity.setAlignment(TextDisplay.TextAlignment.CENTER);
	        	entity.setBillboard(Display.Billboard.FIXED);
	        	entity.setSeeThrough(false);
	        	entity.setRotation( entityLocation.getYaw(), 0);
	        	if( !saveEntityTag( entity)) Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Failed to save 'RewardDisplay' entity tag");
				this.entities[0] = entity;
			});
	 		float yaw = entityLocation.getYaw() + 180F;
			if( yaw >= 180.0) yaw -= 360F;
			entityLocation.setYaw(yaw);
			location.getWorld().spawn(entityLocation, TextDisplay.class, entity -> {
	        	entity.text(amount);
	        	entity.setAlignment(TextDisplay.TextAlignment.CENTER);
	        	entity.setBillboard(Display.Billboard.FIXED);
	        	entity.setSeeThrough(false);
	        	entity.setRotation( entityLocation.getYaw(), 0);
	        	if( !saveEntityTag( entity)) Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Failed to save 'RewardDisplay' entity tag");
				this.entities[1] = entity;
			});
		}else if(reward.size()>0) {
			this.entities = new Display[3];
			location.getWorld().spawn(entityLocation, ItemDisplay.class, entity -> {
				entity.setItemStack(reward.get(0));
				entity.setItemDisplayTransform(ItemDisplayTransform.GUI);
	        	entity.setBillboard(Display.Billboard.FIXED);
	        	entity.setRotation( entityLocation.getYaw(), 0);
	        	if( !saveEntityTag( entity)) Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Failed to save 'RewardDisplay' entity tag");
				this.entities[0] = entity;
			});
			TextComponent amount = Component.text(ChatColor.BLUE + String.valueOf(reward.get(0).getAmount()));
			entityLocation.add(0, 0.5, 0);
			location.getWorld().spawn(entityLocation, TextDisplay.class, entity -> {
	        	entity.text(amount);
	        	entity.setAlignment(TextDisplay.TextAlignment.CENTER);
	        	entity.setBillboard(Display.Billboard.FIXED);
	        	entity.setSeeThrough(false);
	        	entity.setRotation( entityLocation.getYaw(), 0);
	        	if( !saveEntityTag( entity)) Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Failed to save 'RewardDisplay' entity tag");
				this.entities[1] = entity;
			});
	 		float yaw = entityLocation.getYaw() + 180F;
			if( yaw >= 180.0) yaw -= 360F;
			entityLocation.setYaw(yaw);
			location.getWorld().spawn(entityLocation, TextDisplay.class, entity -> {
	        	entity.text(amount);
	        	entity.setAlignment(TextDisplay.TextAlignment.CENTER);
	        	entity.setBillboard(Display.Billboard.FIXED);
	        	entity.setSeeThrough(false);
	        	entity.setRotation( entityLocation.getYaw(), 0);
	        	if( !saveEntityTag( entity)) Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Failed to save 'RewardDisplay' entity tag");
				this.entities[2] = entity;
			});			
		}
	}
	
	private boolean saveEntityTag( Display entity) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
		return PersistentStringStorage.save(plugin, entity, "GameDisplay", entity.getUniqueId().toString());
	}

	private void deleteEntityTag( Display entity) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
		PersistentStringStorage.delete(plugin, entity, "GameDisplay");
	}	
	
    private Location getLocationFromBlock (Location location) {
        Location entityLocation = location.clone();
        entityLocation.setX( location.getX() + 0.5 );
        entityLocation.setY( location.getY() + 0.5 );
        entityLocation.setZ( location.getZ() + 0.5 );

        return entityLocation;
    }		
}
