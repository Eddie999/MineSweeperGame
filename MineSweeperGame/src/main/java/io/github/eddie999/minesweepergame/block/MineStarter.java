package io.github.eddie999.minesweepergame.block;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;

import com.google.common.base.Preconditions;

public class MineStarter extends CustomBlock implements CustomBlockAnimation{
	private int state;
	private static CustomMaterial[] materials = { CustomMaterial.MINE_STARTER_0, CustomMaterial.MINE_STARTER_1, 
			CustomMaterial.MINE_STARTER_2, CustomMaterial.MINE_STARTER_3, CustomMaterial.MINE_STARTER_4};
	private static List<CustomMaterial> materialList = Arrays.asList( materials);
	
	public MineStarter(Location location, CustomMaterial material) {
		super(location, material);
		solidify();
		setBrightness( 10);
		setState( material);
	}
	
	public MineStarter(Location location, int state) {
		this(location, materials[state]);
		Preconditions.checkArgument( 0 <= state && state < materialList.size(),  "State index out of range: %s", state);
		this.state = state;
	}

	public MineStarter(Location location) {
		this(location, 0);
	}
		
	@Override
	public int getState() {
		return this.state;
	}		
	
	@Override
	public int getState(CustomMaterial material) {
		return materialList.indexOf(material);
	}

	@Override
	public void setState(CustomMaterial material) {
		Preconditions.checkArgument( materialList.contains(material), "Material is not in list: %s", material.getDisplayName());
		this.setType(material);
		this.state = materialList.indexOf(material);
	}
	
	@Override
	public void setState(int state) {
		Preconditions.checkArgument( 0 <= state && state < materialList.size(),  "State index out of range: %s", state);
		this.setType( materialList.get(state));
		this.state = state;
	}

	@Override
	public int stateNum() {
		return materials.length;
	}	
}
