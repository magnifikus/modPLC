package de.squig.plc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import de.squig.plc.item.ItemMulti;
import de.squig.plc.tile.TileExtender;

public class ContainerExtender extends Container {

    private TileExtender extender;
    
    public ContainerExtender(InventoryPlayer inventoryPlayer, TileExtender extender) {
    	this.extender = extender;
    	 
        this.addSlotToContainer(new SlotInterface(extender, 0,6, 74));
        //this.addSlotToContainer(new Slot(extender, 1, 56, 62));
        for (int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex) {
            for (int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex) {
                this.addSlotToContainer(new Slot(inventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, 8 + inventoryColumnIndex * 18, 94 + inventoryRowIndex * 18));
            }
        }
        // Add the player's action bar slots to the container
        for (int actionBarSlotIndex = 0; actionBarSlotIndex < 9; ++actionBarSlotIndex) {
            this.addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 152));
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
		
	}

	 @Override
     public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
             ItemStack stack = null;
             Slot slotObject = (Slot) inventorySlots.get(slot);
             if (slotObject != null && slotObject.getHasStack()) {
                     ItemStack stackInSlot = slotObject.getStack();
                     stack = stackInSlot.copy();
                     if (stack != null && !(stack.getItem() instanceof ItemMulti)) 
                    	 return null;
                     if ((stack.getItemName() != null || stack.getItemName().startsWith("item.plcItem.Interface")))
                    	 return null;
                     //merges the item into player inventory since its in the tileEntity
                     //this assumes only 1 slot, for inventories with > 1 slots, check out the Chest Container.
                     if (slot == 0) {
                             if (!mergeItemStack(stackInSlot, 1,
                                             inventorySlots.size(), true)) {
                                     return null;
                             }
                     //places it into the tileEntity is possible since its in the player inventory
                     } else if (!mergeItemStack(stackInSlot, 0, 1, false)) {
                             return null;
                     }

                     if (stackInSlot.stackSize == 0) {
                             slotObject.putStack(null);
                     } else {
                             slotObject.onSlotChanged();
                     }
             }
             extender.onSlotChange();
             return stack;
     }
}