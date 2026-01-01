package io.github.eddie999.minesweepergame.game;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import io.github.eddie999.minesweepergame.utils.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class ScoreTable {
	private Location location;
	private TextComponent title;
	private TextComponent mineExplosionPower;
	private int rows;
	private TextDisplay[] entity;
	
	public ScoreTable( Location location, TextComponent title, int rows) {
		this.title = title;
		this.mineExplosionPower = Component.text(ChatColor.RED + String.format(Lang.translate("scoretable.mine.topten-expolsion"), 4.0));
		this.rows = rows;
		entity = new TextDisplay[2];
		
		placeTable( location, 0);
		Location back = location.clone();
 		float yaw = back.getYaw() + 180F;
		if( yaw >= 180.0) yaw -= 360F;
		back.setYaw(yaw);
		placeTable( back, 1);
	}
	
    public Location getLocation() {
    	return this.location;
    }
    
    public void setMineExplosionPower(Float power) {
    	if( power == null) return;
    	mineExplosionPower = Component.text(ChatColor.RED + String.format(Lang.translate("scoretable.mine.topten-expolsion"), power));
    }
    
    public void remove() {
    	for(int i=0; i<2; i++) {
    		if( entity[i] != null) entity[i].remove();
        	entity[i] = null;
    	}
    }    
    
	private void placeTable( Location location, Integer index) {
        Location entityLocation = getLocationFromBlock(location);
        
        location.getWorld().spawn(entityLocation, TextDisplay.class, entity -> {
        	TextComponent table = Component.text("");
        	table = table.append(this.title);
        	String line;
        	for( int i=1; i<=this.rows; i++) {
        		table = table.append(Component.newline()); 
        		line = String.format("%02d: -------", i);
        		table = table.append(Component.text( line));
        	}
    		table = table.append(Component.newline()); 
    		table = table.append(Component.newline()); 
    		table = table.append( mineExplosionPower);
     		
        	entity.text(table);
        	entity.setAlignment(TextDisplay.TextAlignment.LEFT);
        	entity.setBillboard(Display.Billboard.FIXED);
        	entity.setSeeThrough(false);
        	entity.setRotation( location.getYaw(), 0);
       	
        	this.entity[index] = entity;
    		if(index == 0) this.location = location;
        });
		
	}
	
	public void updateTable( List<ScoreItem> entries) {
    	TextComponent table = Component.text("");
     	table = table.append(this.title);
    	String line;
    	for( int i=0; i<this.rows; i++) {
    		table = table.append(Component.newline());
    		if( entries.size() > i)
    			line = String.format("%02d: %03d %s", i+1, entries.get(i).getTime(),entries.get(i).getName());
    		else
    			line = String.format("%02d: -------", i+1);
    		table = table.append(Component.text( line));
    	}
		table = table.append(Component.newline()); 
		table = table.append(Component.newline()); 
		table = table.append(mineExplosionPower);
		
		for(int i=0; i<2; i++) {
    		entity[i].text(table);
     	}
	}
	
    private Location getLocationFromBlock (Location location) {
        Location entityLocation = location.clone();
        entityLocation.setX( location.getX() + 0.5 );
        entityLocation.setY( location.getY() + 0.5 );
        entityLocation.setZ( location.getZ() + 0.5 );

        return entityLocation;
    }	
}
