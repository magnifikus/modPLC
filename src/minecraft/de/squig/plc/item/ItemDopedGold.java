package de.squig.plc.item;

import de.squig.plc.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemDopedGold extends Item {
	public ItemDopedGold (int id) {
		super(id);
		this.maxStackSize = 64;
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.ITEMS_PNG;
	}
}
