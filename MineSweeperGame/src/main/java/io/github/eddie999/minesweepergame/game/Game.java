package io.github.eddie999.minesweepergame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

import io.github.eddie999.minesweepergame.block.CustomMaterial;
import io.github.eddie999.minesweepergame.block.MineStarter;
import io.github.eddie999.minesweepergame.event.MineGameEndedEvent;
import io.github.eddie999.minesweepergame.event.MineGameLoadEvent;
import io.github.eddie999.minesweepergame.event.MineGameSpawnEvent;
import io.github.eddie999.minesweepergame.event.MineGameEndedEvent.GameResult;
import io.github.eddie999.minesweepergame.mine.MineField;
import io.github.eddie999.minesweepergame.support.VaultInterface;
import io.github.eddie999.minesweepergame.utils.Configs;
import io.github.eddie999.minesweepergame.utils.GameArea;
import io.github.eddie999.minesweepergame.utils.GameDirection;
import io.github.eddie999.minesweepergame.utils.Lang;
import io.github.eddie999.minesweepergame.utils.RealtimeTimer;
import io.github.eddie999.minesweepergame.utils.StringObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class Game extends StringObject {	
	private static final  ConcurrentMap<String, Game> games = new  ConcurrentHashMap<>();
	private Player gamePlayer = null;
	private GameProgress progress = null;
	private final String id;
	private UUID owner;
	private Boolean ownerIsAdmin;
	private String name;
	private Gameboard board;
	private ScoreTable table;
    private ScoreList topTen;
    private Float explosionPower;
    private Integer starterCount=0;
	private MineStarter block;
	private MineField field;
	private GameReward reward;
	private boolean rewardInventoryIsOpen;
	private RewardDisplay display;
	private boolean playerOutsideArea;
	private RealtimeTimer playerTimeout;
	
	public Game(UUID owner, String name, Boolean admin) {
		this.owner = owner;
		this.name = name;
		this.ownerIsAdmin = admin;
		this.rewardInventoryIsOpen = false;
		id = owner.toString() + "/" + name;
		games.put(id, this);
	}

	public Game place(MineStarter block, MineField field, ScoreList topTen, Float power, Integer starters, GameReward reward) {
		this.block = block;
		this.field = field;
		this.topTen = topTen;
		this.explosionPower = power;
		if(explosionPower == null) {
			Double defaultPower = Configs.SETTINGS.getDouble("mine-explosion-power");
			if(defaultPower == null) defaultPower = 4.0;
			explosionPower = defaultPower.floatValue();
		}
		this.starterCount = starters;
		if(starterCount == null) {
			starterCount = Configs.SETTINGS.getInteger("game-starter-mines");
			if(starterCount == null) starterCount = 1; 
		}
		this.reward = reward;
		if( VaultInterface.getEconomy().isEnabled()) this.reward.setCurrency(VaultInterface.getCurrencyName());
		this.progress = new GameProgress(field.getMines(), field.getRows()*field.getCols());
		Location displayLocation = block.getLocation().clone();
		displayLocation.add(0, 1, 0);
		this.display = new RewardDisplay(displayLocation);
		createBoard();
		createScoreTable();
		displayReward();
		this.playerTimeout = new RealtimeTimer();
		games.put(id, this);
		
		return this;
	}
	
	public Game place(MineStarter block, MineField field) {
		Double power = Configs.SETTINGS.getDouble("mine-explosion-power");
		if(power == null) power = 4.0;
		Integer starters = Configs.SETTINGS.getInteger("game-starter-mines");
		if(starters == null) starters = 1;
		return place(block, field, new ScoreList(), power.floatValue(), starters, new GameReward());
	}
	
	public boolean hasChunk(Chunk chunk) {
		if( block.getLocation().getChunk().equals(chunk)) return true;
		return false;
	}
		
	public String getId() { return id;}
	public String getDisplayId() {
		OfflinePlayer player;
		String id = "Anonymous";
		if( (player = Bukkit.getOfflinePlayer(owner)) != null) {
			id = player.getName();
		}
		id = id.concat("/" + name);
		return id;
	}
	public UUID getOwner() { return owner;}
	public String getName() { return name;}
	public boolean isRunning() { return (gamePlayer != null); }
	public Player getPlayer() { return gamePlayer;}
	public static Game[] getGames() { return games.values().toArray(new Game[games.size()]); }
	public boolean isEqual(MineField field) { return this.field.equals(field);}
	public boolean ownerIsAdmin() { return this.ownerIsAdmin; }
	public boolean isOpen() { return this.rewardInventoryIsOpen; }
	public void setOpen(boolean state) { this.rewardInventoryIsOpen = state; }
	public Integer getRows() { return field.getRows();}
	public Integer getCols() { return field.getCols();}
	public Integer getStarterCount() { return starterCount;}
	public boolean isPlayerOutsideArea() { return this.playerOutsideArea; }

	public void setPlayerOutsideArea(boolean state) {
		if( !playerOutsideArea && state) playerTimeout.reset();
		playerOutsideArea = state; 
	}	
	
	public static Boolean isOverlapping(GameArea area) {
		Game[] list = games.values().toArray(new Game[games.size()]);
		for( Game item : list) {
			if(area.isOverlapping(new GameArea(item))) return true;
		}
		
		return false;
	}
	
	public static void setCurrencyName(String name) {
		if( name != null) {
			 for( Entry<String, Game> game : games.entrySet()) {
				 game.getValue().setCurrency(name);
			 }			
		}
	}
	
	public void setCurrency(String currency) {
		if( (currency != null) && (display != null)) {
			reward.setCurrency(currency);
			if(reward.hasAmount()) {
				display.removeDisplay();
				display.showDisplay(reward);
			}
		}
	}
			
	public Block getBlock() {
		return block.getLocation().getBlock();
	}
	    
    public static Game getGame(Block block) {
    	Game result = null;
    	
    	for( Entry<String, Game> entry : games.entrySet()) {
    		if(entry.getValue().getBlock().equals(block)) {
    			result = entry.getValue();
    			break;
    		}
    	}
    	
    	return result;
    }
	
	public static boolean hasGame(UUID uuid, String name) {
		String id = uuid.toString() + "/" + name;
		return hasGame(id);
	}
	
	public static boolean hasGame(String id) {		
		return games.containsKey(id);
	}
		
	public static Game getGame(String id) {
		return games.get(id);
	}
	
	public static Game[] getGames(UUID owner) {
		ArrayList<Game> myGames = new ArrayList<>();
		for( Entry<String, Game> game : games.entrySet()) {
			if( game.getValue().getOwner().equals(owner)) {
				myGames.add(game.getValue());
			}
		}
		if(myGames.size() <= 0) return null;
		
		return myGames.toArray(new Game[myGames.size()]);
	}
	
	public void show() {
		Location loc = block.getLocation().clone();
		GameDirection dir = new GameDirection(loc.getYaw());
		loc.add(dir.getVector());
		field.place(loc, dir.getFace());
	}
	
	public void start(Player player) {
		if((player != null) && (player.isOnline())) {
			gamePlayer = player;
			progress.reset();
			progress.setBlocksLeft(field.getBlocksLeft());
			board.saveCurrentBoard(gamePlayer);
			gamePlayer.setScoreboard(board.getScoreboard());
			block.setState( 1);
			playerOutsideArea = false;
			
			ItemStack item = CustomMaterial.MINE_FLAG.getItem(progress.getFlagsLeft());
			ItemMeta meta = item.getItemMeta();
			List<Component> lore = new ArrayList<>();
			lore.add(Component.text(id));
			meta.lore(lore);
			item.setItemMeta(meta);

			HashMap<Integer,ItemStack> overflow = player.getInventory().addItem(item);
			if( overflow.size() > 0) {
				overflow.clear();
				player.getLocation().getWorld().dropItem(player.getLocation(), item);
			}		
			gamePlayer.sendMessage(Component.text(Lang.translate(gamePlayer, "messages.game.started")));
		}
	}
	
	public void stop() {
		if((gamePlayer != null) && (gamePlayer.isOnline())) {
			//gamePlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			board.restorePreviousBoard(gamePlayer);
			Integer flagsLeft = progress.getFlagsLeft();
			if(flagsLeft <= 0) flagsLeft = 1;
			ItemStack item = CustomMaterial.MINE_FLAG.getItem(flagsLeft);
			ItemMeta meta = item.getItemMeta();
			List<Component> lore = new ArrayList<>();
			lore.add(Component.text(id));
			meta.lore(lore);
			item.setItemMeta(meta);
			gamePlayer.getInventory().removeItemAnySlot(item);
			gamePlayer = null;
			block.setState( 0);
		}
	}
	
	public ItemStack[] getDrops() {
		return reward.toArray(new ItemStack[reward.size()]);
	}
	
	public ItemStack getGameObject() {
		ItemStack item = CustomMaterial.MINE_STARTER_0.getItem(1);
		ItemMeta meta = item.getItemMeta();
		List<Component> lore = new ArrayList<>();
		lore.add(Component.text(name));
		lore.add(Component.text(String.format("%d", field.getRows())));
		lore.add(Component.text(String.format("%d", field.getCols())));
		lore.add(Component.text(String.format("%d", field.getMines())));
		meta.lore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public void remove() {
		stop();
		games.remove(this.getId());
		if(table != null) table.remove();
		if(block != null) block.remove();
		if(field != null) field.remove();
		if(display != null) display.removeDisplay();
	}

	public boolean setReward(String strAmount) {
		try { 
			Integer amount = Integer.parseInt(strAmount);
			return setReward(amount);
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
	public boolean setReward(Integer amount) {
		if(amount < 0) return false;
		reward.setAmount(amount);
		displayReward();
		return true;
	}
	
	public void setReward(ItemStack[] items) {
		reward.clear();
		if((items==null) || items.length <= 0) {
			displayReward();
			return;
		}
		
	    for( ItemStack item : items) {
	    	if( (item != null) && !item.isEmpty()) reward.add(item);    		
	    }
		displayReward();
	}
	
	public void displayReward() {
		this.display.showDisplay(reward);		
	}

	public ItemStack[] getRewardItems() {
		if( (reward==null) || reward.size()<=0) return null;
		return reward.toArray(new ItemStack[reward.size()]);
	}
	
	public Object getReward() {
		if(reward == null) return null;
		Object obj = reward.getReward();
		if(ownerIsAdmin()) {
			reward.rotateReward();
		}else {
			reward.popReward();
		}
		displayReward();

		return obj;
	}

	
	public boolean setExplosionPower(Float power) {
		if((power==null) || (power < 1.0F) || (power > 15.0F)) return false;
		this.explosionPower = power;
		if( this.table != null) {
			this.table.setMineExplosionPower(power);
			this.table.updateTable(topTen);
		}
		return true;
	}
	
	public boolean setStarterCount(Integer starters) {
		if((starters==null) || (starters<0)) this.starterCount = 0;
		else this.starterCount = starters;
		return true;
	}
	
	public void onTick() {
		if(isRunning()) {
			progress.tick();
			updateBoard();
			toggleStarter();
			if(isPlayerOutsideArea()) {
				if((gamePlayer!=null) && (gamePlayer.isConnected())) {
					Integer timeLeft = 10 - playerTimeout.get().intValue()/1000;
					if(timeLeft <= 0) {
						Player player = gamePlayer;
						stop();
	   	    			MineGameEndedEvent endedEvent = new MineGameEndedEvent(this, player, GameResult.CANCELLED, null);
						Bukkit.getPluginManager().callEvent(endedEvent);						
					}else {
						gamePlayer.sendMessage(Component.text(String.format( Lang.translate(gamePlayer, "messages.game.outside-area"), timeLeft)));						
					}
				}
			}
		}
	}
	
	public void placeFlag(boolean place) {
		if(progress == null) return;
		if(place) progress.placeFlag();
		else progress.removeFlag();
	}
	
	public void unhideBlock(boolean mine) {
		if(progress == null) return;
		progress.unhideBlock(mine);
	}
	
	public Integer getTick() {
		if(progress == null) return 0;
		return progress.getTick();		
	}

	public Integer getBlocksLeft() {
		if(progress == null) return 0;
		return progress.getBlocksLeft();		
	}	
	
	public Integer addTopTen(ScoreItem item) {
		topTen.add(item);
		Integer result = topTen.getPlace();
		this.table.updateTable(this.topTen);
		
		return result;
	}
	
	private void createBoard() {
		Component title = Component.text(ChatColor.GREEN + "<<" + ChatColor.BLUE + "MineSweeperGame" + ChatColor.GREEN + ">>");
		Component line = Component.text(ChatColor.BLUE + "===================");
		Component mines = Component.text(ChatColor.AQUA + "Mines: " + ChatColor.DARK_AQUA);
		Component flags = Component.text(ChatColor.AQUA + "Flags: " + ChatColor.DARK_AQUA);
		Component blocks = Component.text(ChatColor.AQUA + "Blocks left: " + ChatColor.DARK_AQUA);
		Component timer = Component.text(ChatColor.AQUA + "Timer: " + ChatColor.DARK_AQUA);
		Gameboard board = new GameboardBuilder(this).objective(title).slot(DisplaySlot.SIDEBAR).
				score("line", line).score("mines", mines, "%03d").score("flags", flags, "%03d")
				.score("blocks", blocks, "%03d").score("timer", timer, "%03d").build();
		
		this.board = board;		
	}
	
	private void updateBoard() {
		if( board != null) {
			board.setEntry("mines", progress.getMinesLeft());
			board.setEntry("flags", progress.getFlagsLeft());
			board.setEntry("blocks", progress.getBlocksLeft());
			board.setEntry("timer", progress.getTick());
		}
	}
	
	private void toggleStarter() {
		if( block == null) return;
		int state = block.getState();
		if( state >= 1) {
			state++;
			if( state >= block.stateNum()) state = 1;
			block.setState( state);
		}
	}
	
	public void createScoreTable() {
    	Location loc = block.getLocation().clone();
    	loc.add( 0, 2, 0);
    	
		TextComponent title = Component.text(ChatColor.GREEN + Lang.translate("scoretable.mine.topten-title"));
		title = title.append(Component.newline());
		title = title.append(Component.text(ChatColor.GREEN + String.format( Lang.translate("scoretable.mine.topten-name"), name)));
		this.table = new ScoreTable( loc, title, 10);
		this.table.setMineExplosionPower(explosionPower);
		this.table.updateTable(this.topTen);
	}	
	
	public void save() {
		String configString = code();		
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
		Chunk chunk = block.getLocation().getChunk();	
		NamespacedKey key = new NamespacedKey(plugin, "starter_blocks");
		
		PersistentDataContainer store;
		if( chunk.getPersistentDataContainer().has(key)) {
			store = chunk.getPersistentDataContainer().get(key, PersistentDataType.TAG_CONTAINER);
		}else {
			store = chunk.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
		}
		store.set(new NamespacedKey(plugin, id), PersistentDataType.STRING, configString);
		chunk.getPersistentDataContainer().set(key, PersistentDataType.TAG_CONTAINER, store);
	}
	
	public void delete() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
		Chunk chunk = block.getLocation().getChunk();	
		NamespacedKey key = new NamespacedKey(plugin, "starter_blocks");

		if( chunk.getPersistentDataContainer().has(key)) {
			PersistentDataContainer store = chunk.getPersistentDataContainer().get(key, PersistentDataType.TAG_CONTAINER);
			store.remove(new NamespacedKey(plugin, id));
			chunk.getPersistentDataContainer().set(key, PersistentDataType.TAG_CONTAINER, store);
		}
	}

	public static void restore(Chunk chunk) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MineSweeperGame");
		NamespacedKey key = new NamespacedKey(plugin, "starter_blocks");

		if( chunk.getPersistentDataContainer().has(key)) {
			PersistentDataContainer store = chunk.getPersistentDataContainer().get(key, PersistentDataType.TAG_CONTAINER);
			if( !store.isEmpty()) {
				for( NamespacedKey gameKey : store.getKeys()) {
					String configString = store.get(gameKey, PersistentDataType.STRING);
					Game game = deserialize(configString);
					if( game != null) game.show();
				}
			}
		}
	}
	
	public static Map<String, Game> getChunkGames(Chunk chunk){
		 Map<String, Game> chunkGames = new HashMap<>();
		 
		 for( Entry<String, Game> game : games.entrySet()) {
			 if( game.getValue().hasChunk(chunk)) {
				 chunkGames.put(game.getKey(), game.getValue());
			 }
		 }
		 
		 return chunkGames;
	}

	public static void saveGames(){
		 for( Entry<String, Game> game : games.entrySet()) {
			 game.getValue().save();
		 }
	}

	public static void stopGames(){
		 for( Entry<String, Game> game : games.entrySet()) {
			 game.getValue().stop();
		 }
	}	

	public static void removeGames(){
		 for( Entry<String, Game> game : games.entrySet()) {
			 game.getValue().remove();
		 }		
	}
	
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		
		data.put("location", block.getLocation().serialize());
		data.put("id", id.toString());
		data.put("name", name);
		data.put("rows", field.getRows());
		data.put("cols", field.getCols());
		data.put("mines", field.getMines());
		data.put("top_ten", topTen.serialize());
		data.put("power", explosionPower);
		data.put("permission", (this.ownerIsAdmin) ? "admin" : "player");
		data.put("reward", reward.serialize());
		data.put("starters", starterCount);
		
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public static Game deserialize(String hexString) {
		Map<String, Object> data = decode(hexString);
		if(data == null) return null;
		
		Map<String,Object> locationMap = (Map<String, Object>) data.get("location");
		Location location = Location.deserialize(locationMap);
		String id = (String) data.get("id");
		String name = (String) data.get("name");
		Integer rows = (Integer) data.get("rows");
		Integer cols = (Integer) data.get("cols");
		Integer mines = (Integer) data.get("mines");
		ScoreList topTen;
		Map<String,Object> topTenMap = (Map<String, Object>) data.get("top_ten");
		if(topTenMap==null) topTen = new ScoreList();
		else topTen = ScoreList.deserialize(topTenMap);
		Float power = (Float) data.get("power");
		String permission = (String) data.get("permission");
		if(permission == null) permission = "player";
		GameReward reward;
		Map<String,Object> rewardMap = (Map<String, Object>) data.get("reward");
		if(rewardMap==null) reward = new GameReward();
		else reward = GameReward.deserialize(rewardMap);
		Integer starters = (Integer) data.get("starters");
		
		Game game = null;
		if( !hasGame(id)) {
			String uuid = id.substring(0, id.indexOf('/'));
			game = new Game(UUID.fromString(uuid), name, permission.equalsIgnoreCase("admin"));			
			MineGameLoadEvent loadEvent = new MineGameLoadEvent(game);
			Bukkit.getPluginManager().callEvent(loadEvent);
			if( loadEvent.isCancelled()) {
				game.remove();
				return null;
			}				
			MineStarter starter = new MineStarter(location);
			MineField field = new MineField(rows, cols, mines);
			game.place(starter, field, topTen, power, starters, reward);
			MineGameSpawnEvent spawnEvent = new MineGameSpawnEvent(game);
			Bukkit.getPluginManager().callEvent(spawnEvent);
			Bukkit.getLogger().info(String.format( "[MineSweeperGame] Restored game %s", game.getDisplayId()));
		}
		
		return game;
	}
}
