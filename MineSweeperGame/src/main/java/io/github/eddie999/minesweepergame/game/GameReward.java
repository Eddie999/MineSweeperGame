package io.github.eddie999.minesweepergame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class GameReward extends ArrayList<ItemStack>{
	private static final long serialVersionUID = -7182044525070460369L;
	private Integer amount=0;
	private String currency=null;
	
	public void setAmount(Integer amount) { this.amount = amount; }
	public Integer getAmount() { return amount;}
	public boolean hasAmount() { return amount>0;}
	public void setCurrency(String currency) { this.currency = currency;}
	public String getCurrency() { return this.currency;}
	
	public Object getReward() {
		if(hasAmount()) return getAmount();
		if(this.size() > 0) {
			return this.get(0);
		}
		return null;
	}
	
	public void popReward() {
		this.remove(0);
	}
	
	public void rotateReward() {
		if(this.size() > 1) {
			ItemStack item = this.remove(0);
			this.add(item);
		}		
	}
	
	@Override
	public boolean add(ItemStack item) {
		return super.add(item);
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("amount", amount);
		if(currency != null) data.put("currency", currency);
		if(this.size() <= 0) return data;
		
		int i = 0;
		for(ItemStack item : this) {
			data.put("item"+i, item.serialize());
			i++;
		}
		
		return data;
	}

	public static GameReward deserialize(Map<String, Object> data) {
		GameReward list = new GameReward();
		list.setAmount((Integer) data.get("amount"));
		list.setCurrency((String) data.get("currency"));
		if(data.size() <= 1) return list;
		
		int i=0;
		Object dataItem;
		while( (dataItem = data.get("item"+i)) != null) {
			@SuppressWarnings("unchecked")
			Map<String,Object> itemMap = (Map<String, Object>) dataItem;
			ItemStack item = ItemStack.deserialize(itemMap);
			list.add(item);
			i++;
		}
		
		return list;
	}

}
