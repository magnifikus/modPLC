package de.squig.plc.item;

import de.squig.plc.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemInterfaceRedstone extends Item {
	public ItemInterfaceRedstone (int id) {
		super(id);
		this.maxStackSize = 64;
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	@Override
	public String getTextureFile() {
		return CommonProxy.ITEMS_PNG;
	}

}
