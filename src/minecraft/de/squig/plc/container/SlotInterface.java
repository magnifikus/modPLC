package de.squig.plc.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TileExtender;

public class SlotInterface extends Slot {
	private TileExtender extender;
	private int Slot;

    public SlotInterface(IInventory extender, int par3, int par4, int par5)
    {
        super(extender, par3, par4, par5);
        this.extender = (TileExtender) extender;
        this.Slot = par3;
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        	LogHelper.info(par1ItemStack.getItemName());
    	return (par1ItemStack == null || (par1ItemStack.getItemName() != null && par1ItemStack.getItemName().startsWith("item.plcItem.UsableInterface")));
    }

    @Override
    public int getSlotStackLimit(){
    	return 64;
    }
}