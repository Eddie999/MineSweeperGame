package io.github.eddie999.minesweepergame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreList extends ArrayList<ScoreItem>{
	private static final long serialVersionUID = 8046652458636945378L;
	private Integer lastPosition = 99;
	
	public Integer getPlace() {return lastPosition;}

	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		if(this.size() <= 0) return data;
		
		int i = 0;
		for(ScoreItem item : this) {
			data.put("item"+i, item.serialize());
			i++;
		}
		
		return data;
	}
	
	@Override
	public boolean add(ScoreItem score) {
		lastPosition = 99;
		Integer index = 0;
		if(this.size() > 0) {
			for(ScoreItem item : this) {
				if( score.getTime() <= item.getTime()) break;
				index++;
			}
			if(index < 10) {
				super.add(index, score);
				lastPosition = index + 1;
			}
		} else {
			super.add(score);
			lastPosition = 1;
		}
		
		return true;
	}

	public static ScoreList deserialize(Map<String, Object> data) {
		ScoreList list = new ScoreList();
		if( data.isEmpty()) return list;
		
		int i=0;
		Object dataItem;
		while( (dataItem = data.get("item"+i)) != null) {
			@SuppressWarnings("unchecked")
			Map<String,Object> itemMap = (Map<String, Object>) dataItem;
			ScoreItem item = ScoreItem.deserialize(itemMap);
			list.add(item);
			i++;
		}
		
		return list;
	}
}
