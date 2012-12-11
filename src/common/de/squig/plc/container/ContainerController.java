package de.squig.plc.container;

import de.squig.plc.tile.TileController;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntityFurnace;

public class ContainerController extends Container {

    private TileController controller;
    
    public ContainerController(InventoryPlayer inventoryPlayer, TileController controller) {
    	this.controller = controller;
    	
    }
    
    public boolean canInteractWith(EntityPlayer player) {
    	return true;
    }

    
}