package io.github.eddie999.minesweepergame.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.inventory.ItemStack;

import io.github.eddie999.minesweepergame.utils.GameDirection;

public abstract class CustomBlock {
	private CustomMaterial material;
    private ItemDisplay entity;
    private Location location;

    public CustomBlock( Location location, CustomMaterial material) {
    	this.material = material;
    	Location blockLocation = location.getBlock().getLocation();
    	GameDirection dir = new GameDirection(location.getYaw());
    	blockLocation.setYaw(dir.getYaw());
    	place( blockLocation);
    }

    public void place( Location location) {
        Location entityLocation = getLocationFromBlock(location);

        location.getWorld().spawn(entityLocation, ItemDisplay.class, entity -> {
            ItemStack item = getItem();
            entity.setItemStack(item);
            entity.setPersistent(false);
            entity.setInvulnerable(true);
            entity.setRotation( location.getYaw(), 0);

            this.entity = entity;
            this.location = location;
        });    	
    }    
    
    public void remove() {
    	this.entity.setItemStack( ItemStack.empty());
        this.entity.remove();
		Block block = this.location.getBlock();
		block.setType(Material.AIR);
        this.location = null;
    }
    
    public void solidify() {
    	this.location.getWorld().setBlockData( this.location, getType().createBlockData());
    }
        
    public void setBrightness( int light) {
    	Brightness brightness = new Brightness( light, light);
    	this.entity.setBrightness( brightness);
    }
    
    public Entity getEntity() {
    	return this.entity;
    }

    public Location getLocation() {
    	return this.location;
    }    
  
    public ItemStack getItem() {
    	return this.material.getItem( 1);
    }
    
    public CustomMaterial getType() {
    	return this.material;
    }
        
    public void setType(CustomMaterial material) {
    	this.material = material;
    	this.entity.setItemStack( getItem());
    }
    
    protected Location getLocationFromBlock (Location location) {
        Location entityLocation = location.clone();
        entityLocation.setX( location.getX() + 0.5 );
        entityLocation.setY( location.getY() + 0.5 );
        entityLocation.setZ( location.getZ() + 0.5 );

        return entityLocation;
    }    

}
