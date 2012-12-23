package de.squig.plc.item;

import de.squig.plc.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemInterfaceBuildcraft extends Item {
	public ItemInterfaceBuildcraft (int id) {
		super(id);
		this.maxStackSize = 64;
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	@Override
	public String getTextureFile() {
		return CommonProxy.ITEMS_PNG;
	}

}
