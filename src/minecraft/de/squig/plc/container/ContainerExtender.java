package de.squig.plc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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