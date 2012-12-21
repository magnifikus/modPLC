package de.squig.plc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import de.squig.plc.tile.TileController;

public class ContainerController extends Container {

    private TileController controller;
    
    public ContainerController(InventoryPlayer inventoryPlayer, TileController controller) {
    	this.controller = controller;
    	
    }
    
    public boolean canInteractWith(EntityPlayer player) {
    	return true;
    }

    
}