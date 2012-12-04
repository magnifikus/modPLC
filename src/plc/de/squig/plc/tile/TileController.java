package de.squig.plc.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.PLC;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.SearchEvent;
import de.squig.plc.event.SearchResponseEvent;
import de.squig.plc.event.SignalEvent;
import de.squig.plc.logic.BasicCircuit;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.LogicInput;

public class TileController extends TilePLC implements IInventory {
	

	
	public final static int STATE_EDIT = 0;
	public final static int STATE_STOP = 1;
	public final static int STATE_RUN = 2;
	public final static int STATE_ERROR = 3;
	
	private int controllerID = -1;
	
	private Circuit circuit = null;
	public int machineState = -1;
	private String controllerName = "unknown";
	
	private int range = 32;
	
	
	public TileController() {
		super(PLCEvent.TARGETTYPE.CONTROLLER);
		LogHelper.info("Controler Constructor");
		machineState = TileController.STATE_EDIT;
		circuit = new BasicCircuit(this);
		
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		LogHelper.info("Controler Init "+xCoord+" "+yCoord+" "+zCoord+" with uuid: "+uuid.toString());
		
		
		
		controllerID = LinkGrid.getWorldMap(worldObj).newID(this);
		controllerName = "no name";
	}

	public void onEvent(PLCEvent event) {
		if (event instanceof SignalEvent) {
			SignalEvent events = (SignalEvent) event;
			CircuitObject obj = circuit.getByType(CircuitObject.TYPES.INPUT, events.getChannel()+"");
			if (obj instanceof LogicInput) {
				LogicInput inp = (LogicInput) obj;
				inp.onSignal(events.getSignal());
			}
		}
		
		else if (event instanceof SearchEvent) {
			if (event.getSource().getWorldObj() == getWorldObj()) {
				int dx = xCoord - event.getSource().xCoord;
				int dy = yCoord - event.getSource().yCoord;
				int dz = zCoord - event.getSource().zCoord;
				int dist = (int)Math.floor(Math.sqrt(dx * dx + dy * dy + dz * dz));
				if (range >= dist) {
					SearchResponseEvent resp = new SearchResponseEvent(this, 
							event.getSource().getUuid(),
							dist,uuid,controllerName,
							circuit.getByType(CircuitObject.TYPES.INPUT).size(),
							circuit.getByType(CircuitObject.TYPES.OUTPUT).size());
					PLC.instance.fireEvent(resp);
				}
			}
		}
		
	}
	
	
	
	@Override
	public void onChunkUnload() {
		circuit.onDestroy();
		super.onChunkUnload();
	}
	
	public void onDestroy() {
		circuit.onDestroy();
		super.onDestroy();
	}
	
	/**
     * The ItemStacks that hold the items currently being used in the Calcinator
     */
	
	
	private ItemStack[] calcinatorItemStacks = new ItemStack[3];

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		LogHelper.info("readFromNBT called");

		
		
		
		/*
		// Read in the ItemStacks in the inventory from NBT
		NBTTagList tagList = nbtTagCompound.getTagList("Items");
        this.calcinatorItemStacks = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound tagCompound = (NBTTagCompound)tagList.tagAt(i);
            byte slot = tagCompound.getByte("Slot");
            if (slot >= 0 && slot < this.calcinatorItemStacks.length) {
                this.calcinatorItemStacks[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
*/
	}

	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		LogHelper.info("writeToNBT called");
		// Write the ItemStacks in the inventory to NBT
		/*NBTTagList tagList = new NBTTagList();
        for (int currentIndex = 0; currentIndex < this.calcinatorItemStacks.length; ++currentIndex) {
            if (this.calcinatorItemStacks[currentIndex] != null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte)currentIndex);
                this.calcinatorItemStacks[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        nbtTagCompound.setTag("Items", tagList);
        */
	}

	/**
     * Returns the number of slots in the inventory.
     */
	public int getSizeInventory() {
		return this.calcinatorItemStacks.length;
	}

	/**
     * Returns the stack in slot i
     */
	public ItemStack getStackInSlot(int i) {
		return this.calcinatorItemStacks[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		// TODO Auto-generated method stub

	}

	public String getInvName() {
		return "container.plcController"; //+ ModBlocks.CALCINATOR_NAME;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void openChest() { }
	public void closeChest() { }

	
	
	
	public Circuit getCircuit() {
		return circuit;
	}

	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
	}

	public int getMachineState() {
		return machineState;
	}

	public void setMachineState(int machineState) {
		this.machineState = machineState;
	}
	public String getControllerName() {
		return controllerName;
	}
	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	
	
	
	

}
