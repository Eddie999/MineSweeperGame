package io.github.eddie999.minesweepergame.mine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.eddie999.minesweepergame.MineSweeperGame;
import io.github.eddie999.minesweepergame.block.CustomMaterial;
import io.github.eddie999.minesweepergame.block.Mine;
import io.github.eddie999.minesweepergame.event.MineGameStartEvent;
import io.github.eddie999.minesweepergame.event.MineGameEndedEvent.GameResult;
import io.github.eddie999.minesweepergame.event.MineGameEndedEvent;
import io.github.eddie999.minesweepergame.event.MineGameMoveEvent;
import io.github.eddie999.minesweepergame.event.MineGamePlaceFlagEvent;
import io.github.eddie999.minesweepergame.game.Game;
import io.github.eddie999.minesweepergame.game.GameCooldown;
import io.github.eddie999.minesweepergame.game.ScoreItem;
import io.github.eddie999.minesweepergame.support.VaultInterface;
import io.github.eddie999.minesweepergame.utils.Broadcast;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.GameConsts;
import io.github.eddie999.minesweepergame.utils.GameDirection;
import io.github.eddie999.minesweepergame.utils.Lang;
import net.kyori.adventure.text.Component;

public class MineField implements Listener{
	private Game game = null;
	private GameDirection direction;
	private MineFrame frame = null;
	private MineMatrix matrix = null;
	private Map<Location, MineMatrix.Pos> matrixLocations = new HashMap<>();
	private Map<Location, Mine> mineLocations = new HashMap<>();
	private Location[][] matrixLookup;
	
	public MineField(Integer rows, Integer cols, Integer mines) {
		this.matrix = new MineMatrix(rows, cols, mines);
	}
	
	public Integer getRows() { return matrix.getRows();}
	public Integer getCols() { return matrix.getCols();}
	public Integer getMines() { return matrix.getMines();}
	
	public boolean isInside(Location location) {
		if( frame == null) return false;
		return frame.isInside(location);
	}
			
	public boolean place( Location loc, BlockFace face) {
		if( (loc==null) || (face==null)) {
			Bukkit.getLogger().log(Level.WARNING, "Invalid start/stop references.");
			return false;
		}
		
		if(matrixLocations.isEmpty()) {
			matrixLookup = new Location[matrix.getRows()][matrix.getCols()];
			direction = new GameDirection(loc.getYaw());
			Location rowLoc = loc.clone();
			for(Integer i=matrix.getRows()-1;i>=0;i--) {
				Location colLoc = rowLoc.clone();
				colLoc.setYaw(direction.getYaw());
				for(Integer j=0;j<matrix.getCols(); j++) {			
					MineMatrix.Pos matrixPos = matrix.new Pos(i,j);
					Location matrixLoc = colLoc.clone();
					matrixLocations.put(matrixLoc, matrixPos);
					matrixLookup[i][j] = matrixLoc;
					colLoc.add(direction.getColVector());
				}
				rowLoc.add(direction.getRowVector());
			}	
		}
		
		for( Location pos : matrixLocations.keySet()) {
			if( mineLocations.containsKey(pos)) {
				Mine mine = mineLocations.get(pos);
				mine.remove();
				mineLocations.remove(pos);
			}
			pos.getBlock().setType(Material.AIR);
			Mine mine = new Mine( pos, CustomMaterial.MINE_F);
			mineLocations.put(pos, mine);
		}
		
		frame = new MineFrame(mineLocations.keySet().toArray(new Location[mineLocations.size()]));
		
		Bukkit.getPluginManager().registerEvents(this, MineSweeperGame.getPlugin( MineSweeperGame.class));
		
		return true;
	}
	
	private void resetMines(CustomMaterial material) {
		for(Entry<Location, Mine> entry : mineLocations.entrySet()) {
			Mine mine = entry.getValue();
			mine.remove();
			mine = new Mine( entry.getKey(), material);
			entry.setValue(mine);
		}
	}
	private MineMatrix.Pos[] findStartMines(int count) {
		List<MineMatrix.Pos> mineList = new ArrayList<>();		
		for(int i=0;i<matrix.getCols();i++) {
			mineList.add(matrix.new Pos(0, i));
			mineList.add(matrix.new Pos(matrix.getRows()-1, i));
		}
		for(int i=1;i<matrix.getRows()-1;i++) {
			mineList.add(matrix.new Pos(i, 0));
			mineList.add(matrix.new Pos(i, matrix.getCols()-1));			
		}
		Collections.shuffle(mineList);

		int numMines = count;
		if(numMines < 0) numMines = 0;
		if(numMines >  mineList.size()) numMines =  mineList.size();
		if(numMines == 0) return null;
				
		MineMatrix.Pos[] result = new MineMatrix.Pos[numMines];
		int index = 0;
		if( !mineList.isEmpty()) {
			for(MineMatrix.Pos pos : mineList) {
				if(matrix.get(pos) <= 0) {
					result[index] = pos;
					index++;
					if(index >= numMines) break;
				}
			}
			if(index < numMines) {
				for(MineMatrix.Pos pos : mineList) {
					if((matrix.get(pos) <= 8) && (matrix.get(pos) > 0)) {
						result[index] = pos;
						index++;
						if(index >= numMines) break;
					}
				}				
			}
			if(index < numMines) {
				for(MineMatrix.Pos pos : mineList) {
					if(matrix.get(pos) >= 9) {
						result[index] = pos;
						index++;
						if(index >= numMines) break;
					}
				}				
			}
		}
		
		return result;
	}
		
	private Location getLocation(MineMatrix.Pos pos) {
		if(pos == null) return null;
		Location location = null;
		for(Entry<Location, MineMatrix.Pos> entry : matrixLocations.entrySet()) {
			if(entry.getValue().equals(pos)) {
				location = entry.getKey();
				break;
			}
		}
		
		return location;
	}
	
	private void uncoverMines() {
		for(Entry<Location, MineMatrix.Pos> entry : matrixLocations.entrySet()) {
			if(matrix.get(entry.getValue()) == 9) {
				Mine mine = mineLocations.get(entry.getKey());	
    			if(mine != null) {
    				mine.remove();
    				mine = new Mine(entry.getKey(),CustomMaterial.MINE_9);
    				mineLocations.put(entry.getKey(), mine);
    			}
			}
		}		
	}
		
	public Integer getBlocksLeft() {
		Integer blocksLeft = 0;
		for(Mine mine : mineLocations.values()) {
			if( mine.getType().equals(CustomMaterial.MINE_X)) blocksLeft++;
		}
		return blocksLeft;
	}
	
	public void remove() {
		if( !mineLocations.isEmpty()) {
			for( Mine mine : mineLocations.values()) {
				mine.remove();
			}
			mineLocations.clear();
		}
		if( !matrixLocations.isEmpty()) {
			matrixLocations.clear();
		}
		
		HandlerList.unregisterAll(this);
	}
	
    // Wait for game to start
    @EventHandler
    public void onMineGameStartEvent (MineGameStartEvent event) {
    	if( !event.isCancelled() && event.getGame().isEqual(this)) {
    		game = event.getGame();
    		matrix.reset();
    		matrix.populate();
    		Integer startMineCount = event.getGame().getStarterCount();
    		resetMines(CustomMaterial.MINE_X);
    		MineMatrix.Pos[] positions = findStartMines( startMineCount);
    		if( (positions != null) && (positions.length > 0)) {
    			for(int i=0;i<startMineCount;i++) {
    				Location location = null;
    				if(positions.length > i) location = getLocation(positions[i]); 
    				if(location != null) {
    					CustomMaterial material = CustomMaterial.getMaterial(GameConsts.CUSTOM_MODEL_DATA_BASE+"mine"+matrix.get(positions[i]));
    					Mine mine = mineLocations.get(location);
    					if(mine != null) {
    						mine.remove();
    						mine = new Mine(location, material);
    						mineLocations.put(location, mine);
    					}
    				}
    			}
    		}
    	}
    }

    // Wait for game to end
    @EventHandler
    public void onMineGameEndedEvent (MineGameEndedEvent event) {
    	if((event.getGame() != null) && event.getGame().isEqual(this)) {
    		if(event.getResult().equals(GameResult.SUCCESS)) {
    			ScoreItem item = new ScoreItem(event.getPlayer(), event.getGame().getTick());
    			Integer place = event.getGame().addTopTen(item);
    			event.getPlayer().sendMessage(String.format(Lang.translate(event.getPlayer(), "messages.game.success"),item.getTime()));
    			if( place <= 10) event.getPlayer().sendMessage(String.format(Lang.translate(event.getPlayer(), "messages.game.place"),place));
    			if(Configs.SETTINGS.getBoolean("enable-broadcast")) {
    				if(!Configs.SETTINGS.getBoolean("broadcast-topten-only")) {
    					Component message = Component.text(String.format(Lang.translate(event.getPlayer(), "messages.game.broadcast-success"),
    						event.getPlayer().getName(), event.getGame().getName(), item.getTime()));
    					if(Configs.SETTINGS.getBoolean("broadcast-all-worlds")) Broadcast.broadcast(message);   					
    					else Broadcast.broadcast(event.getPlayer().getWorld(), message);
    				}
    				if( place <= 10) {
    					Component message = Component.text(String.format(Lang.translate(event.getPlayer(), "messages.game.broadcast-topten"),
        						event.getPlayer().getName(), place));
    					if(Configs.SETTINGS.getBoolean("broadcast-all-worlds")) Broadcast.broadcast(message);   					
    					else Broadcast.broadcast(event.getPlayer().getWorld(), message);
    				}
    			}
    			GameCooldown.resetCounter(event.getPlayer());
    			Object gift = event.getGame().getReward();
    			if(gift != null) {
    				if( gift instanceof Integer) {
    					Integer giftAmount = (Integer)gift;
    					if(!Configs.SETTINGS.getBoolean("disable-vault-support")) {
    						VaultInterface.deposit(event.getPlayer(), giftAmount);
    					}
    				}else if( gift instanceof ItemStack) {
    					ItemStack giftItem = (ItemStack)gift;
    					HashMap<Integer,ItemStack> overflow = event.getPlayer().getInventory().addItem(giftItem);
    					if( overflow.size() > 0) {
    						overflow.clear();
    						event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), giftItem);
    					}		
    				}
    			}
    		}else if(event.getResult().equals(GameResult.FAILED)) {
				float power = Configs.SETTINGS.getDouble("mine-explosion-power").floatValue();
				event.getMine().getLocation().getWorld().createExplosion( event.getMine().getEntity(), power);
				event.getPlayer().sendMessage(String.format(Lang.translate(event.getPlayer(), "messages.game.failed")));
				GameCooldown.resetCounter(event.getPlayer());
				uncoverMines();
    		}else if(event.getResult().equals(GameResult.CANCELLED)) {
    			if((event.getPlayer()!=null) && event.getPlayer().isConnected()) {
    				event.getPlayer().sendMessage(String.format(Lang.translate(event.getPlayer(), "messages.game.cancelled")));    				
    			}
    		}
    		game = null;
    	}
    }
    
    // A flag is being placed
    @EventHandler
    public void onMineGamePlaceFlagEvent (MineGamePlaceFlagEvent event) {
    	if((game != null) && game.equals(event.getGame())) {
    		if(game.isRunning() && game.getPlayer().equals(event.getPlayer()) && isInside(event.getLocation())){
    			Location location = event.getLocation().clone();
 				location.setYaw(direction.getYaw());
 				MineMatrix.Pos pos = matrixLocations.get(location);
 				Mine mine = mineLocations.get(location);
 				if((pos != null) && (mine != null)) {
 					if( mine.getType().equals(CustomMaterial.MINE_FLAG)) {
 						mine.remove();
 						mine = new Mine( location, CustomMaterial.MINE_X);
 						mineLocations.put(location, mine);
 						game.placeFlag(false);
 						ItemStack item = CustomMaterial.MINE_FLAG.getItem(1);
 						ItemMeta meta = item.getItemMeta();
 						List<Component> lore = new ArrayList<>();
 						lore.add(Component.text(game.getId()));
 						meta.lore(lore);
 						item.setItemMeta(meta);
 						event.getPlayer().getInventory().addItem(item);
 					}else if(mine.getType().equals(CustomMaterial.MINE_X) && event.isFlagInHand()){
 						mine.remove();
 						mine = new Mine( location, CustomMaterial.MINE_FLAG);
 						mineLocations.put(location, mine);
 						game.placeFlag(true);
 						ItemStack item = CustomMaterial.MINE_FLAG.getItem(1);
 						ItemMeta meta = item.getItemMeta();
 						List<Component> lore = new ArrayList<>();
 						lore.add(Component.text(game.getId()));
 						meta.lore(lore);
 						item.setItemMeta(meta);
 						event.getPlayer().getInventory().removeItem(item);
 					}else event.setCancelled(true);
 				}else event.setCancelled(true);
    		}else event.setCancelled(true);
    	}
    }
    
    // Monitor player movement in the mine field
    @EventHandler
    public void onPlayerMoveEvent (PlayerMoveEvent event) {
    	if( !event.isCancelled() && (game != null) && (game.isRunning()) && (event.getPlayer().equals(game.getPlayer()))) {
    		if( frame.isInside(event.getTo().getBlock().getLocation())) {
    	    	double fromFractionX = event.getFrom().getX() - event.getFrom().getBlockX();
    	    	double fromFractionZ = event.getFrom().getZ() - event.getFrom().getBlockZ();
    	    	double toFractionX = event.getTo().getX() - event.getTo().getBlockX();
    	    	double toFractionZ = event.getTo().getZ() - event.getTo().getBlockZ();
    	    	//Going from outside to inside
    	 		if(((toFractionX>=0.2) || (toFractionX<=0.8) ||
    	 			(toFractionZ>=0.2) || (toFractionZ<=0.8)) ) {
    	 			if((((fromFractionX<0.2) || (fromFractionX>0.8)) &&
    	 				((fromFractionZ<0.2) || (fromFractionZ>0.8))) ||
    	 				(event.getFrom().getBlockX() != event.getTo().getBlockX()) ||
    	 				(event.getFrom().getBlockZ() != event.getTo().getBlockZ()) )
    	 			{
    	 				Location loc = event.getTo().getBlock().getLocation().clone();
    	 				loc.setYaw(direction.getYaw());
    	 				MineMatrix.Pos pos = matrixLocations.get(loc);
    	 				Mine mine = mineLocations.get(loc);
    	 				if((pos != null) && (mine != null)) {
           	    			MineGameMoveEvent moveEvent = new MineGameMoveEvent(game, event.getPlayer(), this, mine, pos, matrix.get(pos));
        					Bukkit.getPluginManager().callEvent(moveEvent);    	
        					if(!moveEvent.isCancelled()) {
        	 					if(mine.getType().equals(CustomMaterial.MINE_X)) {
        	 						CustomMaterial material = CustomMaterial.getMaterial(GameConsts.CUSTOM_MODEL_DATA_BASE+"mine"+matrix.get(pos));
        	 						mine.remove();
        	 						mine = new Mine( loc, material);
        	 						mineLocations.put(loc, mine);
        	 						if( matrix.get(pos) == 9) {
            	 						game.unhideBlock(true);
            	       	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(game, game.getPlayer(), GameResult.FAILED, mine);
            	    					game.stop();
            	    					Bukkit.getPluginManager().callEvent(endedEvent);    	
        	 						}else {
            	 						game.unhideBlock(false); 
            	 						if(game.getBlocksLeft() == 0) {
                	       	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(game, game.getPlayer(), GameResult.SUCCESS, mine);
                	    					game.stop();
                	    					Bukkit.getPluginManager().callEvent(endedEvent);    	            	 							
            	 						}else if(matrix.get(pos) == 0) {
            	 							Integer[][] flags = new Integer[matrix.getRows()][matrix.getCols()];
               	 							for(int i=0;i<matrix.getRows();i++) {
            	 								for(int j=0;j<matrix.getCols();j++) {
            	 									flags[i][j] = 0;	
            	 								}
               	 							}
            	 							matrix.uncoverNullBlocks(pos, flags);
            	 							for(int i=0;i<matrix.getRows();i++) {
            	 								for(int j=0;j<matrix.getCols();j++) {
            	 									if(flags[i][j] > 0) {
            	 										Location show = matrixLookup[i][j];
            	 										Integer showVal = matrix.get(i, j);
            	 										Mine showMine = mineLocations.get(show);
            	 										if(showMine.getType().equals(CustomMaterial.MINE_X)) {
            	 											showMine.remove();
            	 											showMine = new Mine(show, CustomMaterial.getMaterial( GameConsts.CUSTOM_MODEL_DATA_BASE+"mine"+showVal));
            	 											mineLocations.put(show, showMine);
            	 											game.unhideBlock(false);
            	 										}
            	 									}
            	 								}
            	 							}
            	 						}
        	 						}
        	 					}
        					}else {
        						event.setCancelled(true);
        					}
    	 				}
    	 			}
    	 		}
    		} else {
    			Integer distance = frame.distance(event.getPlayer().getLocation());
				if( distance == Integer.MAX_VALUE) {
					game.stop();
   	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(game, event.getPlayer(), GameResult.CANCELLED, null);
					Bukkit.getPluginManager().callEvent(endedEvent);
					return;
				}
				if( distance > 10) {
					game.setPlayerOutsideArea(true);
				}else {
					game.setPlayerOutsideArea(false);					
				}
    		}
    	}
    }
}
