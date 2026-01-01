package io.github.eddie999.minesweepergame.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class PluginLanguages {
	private String defaultLanguage;
	private final Map<String, JsonObject> languages;
	
	@NotNull
	public PluginLanguages(@NotNull JavaPlugin plugin, @NotNull String defaultLanguage, @NotNull Boolean multiLanguage) {
		this.defaultLanguage = defaultLanguage.toLowerCase();
		this.languages = new HashMap<String, JsonObject>();
		String[] resourceNames = getResourceNames(plugin);
		updateLanguageConfig(plugin, resourceNames);
		String[] fileNames = getLanguageFileNames(plugin);
		readLanguageConfig(plugin, fileNames);
		String[] names = languages.keySet().toArray(new String[languages.size()]);
    	if(names.length > 0) {
    		if( languages.get(this.defaultLanguage) == null) {
        		plugin.getLogger().log(Level.WARNING, "Didn't find language " + this.defaultLanguage + " using " + names[0]);
        		this.defaultLanguage = names[0];
    		}
    		if( multiLanguage) {
    			String msg = "Supported languages: ";
    			for(String name : names) {
    				msg = msg.concat(name + ", ");
    			}
    			plugin.getLogger().info(msg.substring(0, msg.length()-2));
    		}else {
    			plugin.getLogger().info("Multi language support is turned off.");
    		}
     		plugin.getLogger().info("Default language: " + this.defaultLanguage);
    	}		
	}
	
    public String translate(String langName, String key) {
    	if((langName == null) || (key == null)) return null;
    	String result = null;
    	
    	JsonObject lang = languages.get(langName);
    	if(lang == null) lang = languages.get(defaultLanguage);
    	
    	if(lang != null) {
    		JsonElement item = lang.get(key);
    		if(item != null) {
    			String text = item.toString();
    			result = text.substring(1, text.length() - 1);
    		}
    	}
    	if( result == null) result = key;
    	
    	return result;
    }	

    public String translate(String key) {
    	return translate(defaultLanguage, key);
    }
        
    private String[] getResourceNames(JavaPlugin plugin) {
    	List<String> fileNames = new ArrayList<>();
    	
    	URL jar = plugin.getClass().getClassLoader().getResource("lang/");
    	try {
        	JarURLConnection connection = (JarURLConnection) jar.openConnection();
        	JarFile jarFile = connection.getJarFile();
        	Enumeration<JarEntry> entries = jarFile.entries();
        	while (entries.hasMoreElements()) {
        		JarEntry entry = entries.nextElement();
        		if(entry.getName().indexOf("lang/") >= 0) {
        			if(entry.getName().indexOf(".json") >= 0) fileNames.add(entry.getName().substring(5));
        		}
        	}        	
    	} catch (IOException e) {
			plugin.getLogger().log(Level.WARNING, "Missing language resources!");        			
    	}
    	
    	return fileNames.toArray(new String[fileNames.size()]);
    }

    private void updateLanguageConfig(JavaPlugin plugin, String[] fileNames) {
    	if(fileNames.length <= 0) return;
    	
        File path = new File(plugin.getDataFolder(), "\\lang");
		if( !path.exists()) path.mkdirs();   		
    	
		for(String fileName : fileNames) {
			File file = new File( path, fileName);
			if( !file.exists()) {
	        	plugin.getLogger().log(Level.INFO, fileName + " not found, creating!");
	        	plugin.saveResource("lang\\" + fileName, false);				
			}
		}		
    }

    private String[] getLanguageFileNames(JavaPlugin plugin) {
    	List<String> fileNames = new ArrayList<>();
    	
        File path = new File(plugin.getDataFolder(), "\\lang");    	
		File[] files = path.listFiles();
		if((files == null) || (files.length == 0)) return fileNames.toArray(new String[fileNames.size()]);
		
		for( File file : files) {
			if(file.isFile()) {
				if( file.getName().indexOf(".json") > 0) fileNames.add(file.getName());
			}
		}
		
		return fileNames.toArray(new String[fileNames.size()]);
    }
    
    private void readLanguageConfig(JavaPlugin plugin, String[] fileNames) {
    	if(fileNames.length <= 0) return;
    	
        File path = new File(plugin.getDataFolder(), "\\lang"); 		
        for(String fileName : fileNames) {
        	try {
         		FileReader reader = new FileReader(new File( path, fileName));
                JsonElement jsonElement = JsonParser.parseReader(reader);
                int index = fileName.indexOf(".json");
                languages.put(fileName.substring(0, index), jsonElement.getAsJsonObject());			
    		} catch (FileNotFoundException e) {
            	plugin.getLogger().log(Level.WARNING, fileName + " missing!");
    		} catch (JsonParseException e) {
            	plugin.getLogger().log(Level.WARNING, fileName + " has syntax error!");
    		}        	
        }
    }
 
}
