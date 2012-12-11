package de.squig.plc.container;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntityFurnace;
import de.squig.plc.tile.TileExtender;

public class ContainerExtender extends Container {

    private TileExtender extender;
    
    public ContainerExtender(InventoryPlayer inventoryPlayer, TileExtender extender) {
    	this.extender = extender;
    	
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
    
}