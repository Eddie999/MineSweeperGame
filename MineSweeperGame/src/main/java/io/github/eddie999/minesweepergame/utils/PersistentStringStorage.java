package io.github.eddie999.minesweepergame.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public interface PersistentStringStorage {
	
	public static boolean save(Plugin plugin, PersistentDataHolder storage, String key, String value) {
		if((plugin==null) || (storage==null) || (key==null) ||(value==null)) return false;
		
		NamespacedKey storageKey = new NamespacedKey(plugin, "PersistentStringStorage");
		PersistentDataContainer store=null;
		if( storage.getPersistentDataContainer().has(storageKey)) {
			store = storage.getPersistentDataContainer().get(storageKey, PersistentDataType.TAG_CONTAINER);
		}else {
			store = storage.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
		}
		if(store==null) return false;
		store.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
		storage.getPersistentDataContainer().set(storageKey, PersistentDataType.TAG_CONTAINER, store);
		
		return true;
	}

	public static String load(Plugin plugin, PersistentDataHolder storage, String key) {
		if((plugin==null) || (storage==null) || (key==null)) return null;

		NamespacedKey storageKey = new NamespacedKey(plugin, "PersistentStringStorage");
		if( storage.getPersistentDataContainer().has(storageKey)) {
			PersistentDataContainer store=null;
			store = storage.getPersistentDataContainer().get(storageKey, PersistentDataType.TAG_CONTAINER);
			if((store!=null) && !store.isEmpty()) {
				String value = store.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
				return value;
			}
		}
		
		return null;
	}	
	
	public static boolean delete(Plugin plugin, PersistentDataHolder storage, String key) {
		if((plugin==null) || (storage==null) || (key==null)) return false;
		
		NamespacedKey storageKey = new NamespacedKey(plugin, "PersistentStringStorage");
		if( storage.getPersistentDataContainer().has(storageKey)) {
			PersistentDataContainer store=null;
			store = storage.getPersistentDataContainer().get(storageKey, PersistentDataType.TAG_CONTAINER);
			if((store!=null) && !store.isEmpty()) {
				store.remove(new NamespacedKey(plugin, key));
				storage.getPersistentDataContainer().set(storageKey, PersistentDataType.TAG_CONTAINER, store);
				return true;
			}
		}
		
		return false;
	}
}
