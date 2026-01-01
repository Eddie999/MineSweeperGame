package io.github.eddie999.minesweepergame.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public abstract class StringObject extends Object{

	public String code() {
		String stringObject;
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			try(ObjectOutputStream oos = new ObjectOutputStream(bos)){
				byte[] bArray;
				oos.writeObject(serialize());
				oos.flush();
				oos.close();
				bArray = bos.toByteArray();
				stringObject = byteToHexString(bArray);
			}
		}catch(IOException e){
			Bukkit.getLogger().log(Level.WARNING, "[StringObject] Failed to write to OutputStream: " + e.getMessage());
			stringObject = new String();
		}
		return stringObject;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> decode(String stringObject) {
		Map<String, Object> data;
		byte[] objectData = HexStringToByteArray( stringObject);
		if(objectData == null) {
			Bukkit.getLogger().log(Level.WARNING, "[StringObject] Invalid HexString data!");
			return null;
		}
		try (ByteArrayInputStream bais = new ByteArrayInputStream(objectData)){
			try(ObjectInputStream ois = new ObjectInputStream(bais)){
				data = (Map<String, Object>) ois.readObject();
			} catch (ClassNotFoundException e) {
				Bukkit.getLogger().log(Level.WARNING, "[StringObject] Invalid HexString Map class: " + e.getMessage());
				return null;
			}
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "[StringObject] Failed to read from InputStream: " + e.getMessage());
			return null;
		}
		return data;
	}
	
	public abstract Map<String, Object> serialize();
		
	private static String byteToHexString(byte[] bArray) {
		String result = new String();
		if((bArray == null) || (bArray.length<=0)) return result;
		
		for( byte b : bArray) {
			String hex = String.format("%02X", b);
			result = result.concat(hex);
		}
		
		return result;
	}
	
	private static byte[] HexStringToByteArray(String hexString) {
		if((hexString == null) || (hexString.length()<=0)) return null;
		if( (hexString.length() % 2) != 0 ) {
			Bukkit.getLogger().log(Level.WARNING, "[StringObject] Hex String has odd length: " + hexString);
		}
		byte[] data = new byte[hexString.length()/2];

		for(int i=0; i<hexString.length(); i+=2) {
			try {
				Integer val = Integer.valueOf(hexString.substring(i, i+2), 16);
				data[i/2] = val.byteValue();
			}catch(NumberFormatException e) {
				Bukkit.getLogger().log(Level.WARNING, "[StringObject] Invalid Hex String value: " + e.getMessage());
			}
		}
		
		return data;
	}
}
