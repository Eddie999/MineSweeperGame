package io.github.eddie999.minesweepergame.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import io.github.eddie999.minesweepergame.MineSweeperGame;

public enum Configs {
	SETTINGS("settings");
	
	private final HashMap<String,Object> configList = new HashMap<>();
	private final String pluginName;
	
	private Configs(String label) {
		ConfigurationSection section = (ConfigurationSection) MineSweeperGame.getPlugin(MineSweeperGame.class).getConfig().get(label);
		pluginName = MineSweeperGame.getPlugin(MineSweeperGame.class).getName();
		
		Set<String> keys = section.getKeys(true);
		for( String key : keys) {
			Object obj = section.get(key);
			if( obj != null) {
				if( obj instanceof Boolean) {
					Boolean value = (Boolean) obj;
					configList.put(key, value);
				}else if( obj instanceof Double) {
					Double value = (Double) obj;
					configList.put(key, value);
				}else if( obj instanceof Integer) {
					Integer value = (Integer) obj;
					configList.put(key, value);
				}else if(obj instanceof String) {
					String value = (String) obj;
					configList.put(key, value);					
				}else if(obj instanceof List) {
					List<?> list = (List<?>) obj;
					if(!list.isEmpty() && (list.get(0) instanceof String)) {
						@SuppressWarnings("unchecked")
						List<String> value = (List<String>) obj;
						configList.put(key, value);
					}
				}
			}
		}		
	}
		
	public String get(String key) {
		return (String) getObject(key);
	}
	
	public Boolean getBoolean(String key) {
		return (Boolean) getObject(key);
	}

	public Double getDouble(String key) {
		return (Double) getObject(key);
	}
	
	public Integer getInteger(String key) {
		return (Integer) getObject(key);
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList(String key) {
		if(getObject(key) instanceof List) {
			return (List<String>) getObject(key);
		}
		return null;
	}	
	
	private Object getObject(String key) {
		Object obj = configList.get(key);
		if(obj == null) Bukkit.getLogger().log(Level.WARNING, "[" + pluginName+ "]: " + String.format("Undefined key: %s", key));
		return obj;
	}
	
}
