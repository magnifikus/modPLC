package de.squig.plc.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import de.squig.plc.item.ItemInterfaceRedstone;
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
        return (par1ItemStack == null || par1ItemStack.getItem() instanceof ItemInterfaceRedstone);
    }

    @Override
    public int getSlotStackLimit(){
    	return 64;
    }
}