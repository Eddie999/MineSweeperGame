package io.github.eddie999.minesweepergame.block;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import io.github.eddie999.minesweepergame.utils.GameConsts;
import io.github.eddie999.minesweepergame.utils.Lang;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;

public enum CustomMaterial implements GameConsts{
    MINE_0( CUSTOM_MODEL_DATA_BASE+"mine0", Material.STONE),
	MINE_1( CUSTOM_MODEL_DATA_BASE+"mine1", Material.STONE),
	MINE_2( CUSTOM_MODEL_DATA_BASE+"mine2", Material.STONE),
	MINE_3( CUSTOM_MODEL_DATA_BASE+"mine3", Material.STONE),
	MINE_4( CUSTOM_MODEL_DATA_BASE+"mine4", Material.STONE),
	MINE_5( CUSTOM_MODEL_DATA_BASE+"mine5", Material.STONE),
	MINE_6( CUSTOM_MODEL_DATA_BASE+"mine6", Material.STONE),
	MINE_7( CUSTOM_MODEL_DATA_BASE+"mine7", Material.STONE),
	MINE_8( CUSTOM_MODEL_DATA_BASE+"mine8", Material.STONE),
	MINE_9( CUSTOM_MODEL_DATA_BASE+"mine9", Material.STONE),
	MINE_X( CUSTOM_MODEL_DATA_BASE+"minex", Material.STONE),
	MINE_F( CUSTOM_MODEL_DATA_BASE+"minef", Material.STONE),
	MINE_FLAG( CUSTOM_MODEL_DATA_BASE+"mineflag", Material.STONE),
	MINE_MARKER_0( CUSTOM_MODEL_DATA_BASE+"minemarker0", Material.STONE),
	MINE_MARKER_1( CUSTOM_MODEL_DATA_BASE+"minemarker1", Material.STONE),
	MINE_STARTER_0(CUSTOM_MODEL_DATA_BASE+"minestarter0", Material.BARRIER),
	MINE_STARTER_1(CUSTOM_MODEL_DATA_BASE+"minestarter1", Material.BARRIER),
	MINE_STARTER_2(CUSTOM_MODEL_DATA_BASE+"minestarter2", Material.BARRIER),
	MINE_STARTER_3(CUSTOM_MODEL_DATA_BASE+"minestarter3", Material.BARRIER),
	MINE_STARTER_4(CUSTOM_MODEL_DATA_BASE+"minestarter4", Material.BARRIER);

    private final String id;
    private final Material baseMaterial;
	private static final Map<String, CustomMaterial> BY_ID = Maps.newHashMap();
	
	private CustomMaterial( final String id, final Material material) {
		this.id = id;
		this.baseMaterial = material;
	}

	public String getId() {
		return this.id;
	}

	public BlockData createBlockData() {
		return this.baseMaterial.createBlockData();
	}

	public String getDisplayName() {
		String displayName = Lang.translate("materials." + getId());
		
		if(displayName == null) {
			Bukkit.getLogger().log(Level.WARNING, "[MineSweeperGame] Missing material name materials." + getId());
			displayName = "#missing";
		}
		
		return displayName;
	}
		
	public ItemStack getItem( int amount) {
		ItemStack item = new ItemStack( this.baseMaterial, amount);
		
    	ItemMeta itemMeta = item.getItemMeta();
    	itemMeta.customName( Component.text( getDisplayName())); 
    	item.setItemMeta(itemMeta);
    	
        CustomModelData modelData = CustomModelData.customModelData().addString(id).build();
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, modelData);
        
        return item;
	}
	
    static {
        for (CustomMaterial material : values()) {
            BY_ID.put( material.id, material);
        }
    }
	
    @Nullable
    public static CustomMaterial getMaterial(@NotNull final String id) {
    	return BY_ID.get(id);
    }
    
    public static CustomMaterial getMaterial( ItemStack item) {
    	if( (item == null) || (!item.hasItemMeta())) return null;
    	
     	if( item.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
    		CustomModelData modelData = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
    		return BY_ID.get(modelData.strings().get(0));
    	}
    	
    	return null;
    }    
	
}
