
/**
 * PacketTypeHandler
 * 
 * Handler that routes packets to the appropriate destinations depending on what kind of packet they are
 * 
 * @author pahimar
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */

package de.squig.plc.tile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.PLC;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.SearchEvent;
import de.squig.plc.event.SearchResponseEvent;
import de.squig.plc.event.SignalEvent;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.ExtenderChannelNetworkData;
import de.squig.plc.logic.extender.function.BC3Function;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketExtenderLiteData;

public class TileExtender extends TilePLC implements IInventory {
	public enum TYPE {
		BASIC
	};

	private TYPE type = null;

	private int extenderID = -1;

	private int range = 16;
	
	private int inChannels = 0;
	private int outChannels = 0;

	// SOFT!
	private UUID connectedController = null;
	private String connectedControllerName = null;

	private List<SearchResponseEvent> controllersInRange = null;

	private List<ExtenderChannel> channels = null;

	private List<ExtenderChannel> redstoneListener = new ArrayList<ExtenderChannel>();
	private List<ExtenderChannel> tickListener = new ArrayList<ExtenderChannel>();
	private List<ExtenderChannel> changeListener = new ArrayList<ExtenderChannel>();

	private Signal sidePowered[] = new Signal[6];

	private List<ExtenderChannel> sideChannels[] = new ArrayList[6];

	private boolean isRedstonePowered;

	private List<Long> sheduledUpdates = new ArrayList<Long>();
	
	
	private boolean sheduleRemoteUpdate = false;

	// load/save shit

	private boolean needsLoad = false;
	
	private List<ExtenderChannelNetworkData> loadChannelData = null;
	
	private char[] instatus = new char[0];
	private char[] outstatus = new char[0];
	
	public void updateStatus(char[] ins, char[] outs) {
		instatus = ins;
		outstatus = outs;
	}
	
	public char[] getInputs() {
		return instatus;
	}
	
	public char[] getOutputs() {
		return outstatus;
	}
	
	
	protected TileExtender() {
		super(PLCEvent.TARGETTYPE.EXTENDER);
		for (int i = 0; i < sidePowered.length; i++)
			sidePowered[i] = Signal.OFF;
		for (int i = 0; i < 6; i++)
			sideChannels[i] = new ArrayList<ExtenderChannel>();
		channels = new ArrayList<ExtenderChannel>();
	}

	public static TileExtender createInstance() {
		try {
			Class bc3Extender = PLC.class.getClassLoader().loadClass("de.squig.plc.bc3.compat.TileExtenderBC3");
			Constructor constructor = bc3Extender.getConstructor(null);
			TileExtender res = (TileExtender)
			        constructor.newInstance(null);
			return res;
	     
		} catch (Exception ex) {
			return new TileExtender();
		}
	}
	
	
	public boolean isSidePowered(ForgeDirection side) {
		Signal signal = sidePowered[side.ordinal()];
		if (signal.equals(Signal.ON) || signal.equals(Signal.PULSE))
			return true;
		else
			return false;
	}

	public Signal getSideSignal(ForgeDirection side) {
		return sidePowered[side.ordinal()];
	}

	public List<ExtenderChannel> getSideChannels(int side) {
		return sideChannels[side];
	}

	public void setSidePowered(ForgeDirection side, Signal signal) {
		setSidePowered(side, signal, true);
	}

	public void setSidePowered(ForgeDirection side, Signal signal,
			boolean dNotify) {
		if (!sidePowered[side.ordinal()].equals(signal)) {
			sidePowered[side.ordinal()] = signal;
		}
		Side sside = FMLCommonHandler.instance().getEffectiveSide();
		if (sside.equals(Side.SERVER) && dNotify) {
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, 501);
			//PacketExtenderLiteData.sendUpdateToClients(this);
		}

	}

	public void setSheduleRemoteUpdate(boolean sheduleRemoteUpdate) {
		this.sheduleRemoteUpdate = sheduleRemoteUpdate;
	}

	public void setSidePowered(ExtenderChannel channel, Signal signal) {
		
		boolean update = false;
		if (sideChannels[0].contains(channel)) {
			if (!sidePowered[0].equals(signal)) {
				update = true;
				sidePowered[0] = signal;
			}
		}
		if (sideChannels[1].contains(channel)) {
			if (!sidePowered[1].equals(signal)) {
				update = true;
				sidePowered[1] = signal;
			}
		}
		if (sideChannels[2].contains(channel)) {
			if (!sidePowered[2].equals(signal)) {
				update = true;
				sidePowered[2] = signal;
			}
		}
		if (sideChannels[3].contains(channel)) {
			if (!sidePowered[3].equals(signal)) {
				update = true;
				sidePowered[3] = signal;
			}
		}
		if (sideChannels[4].contains(channel)) {
			if (!sidePowered[4].equals(signal)) {
				update = true;
				sidePowered[4] = signal;
			}
		}
		if (sideChannels[5].contains(channel)) {
			if (!sidePowered[5].equals(signal)) {
				update = true;
				sidePowered[5] = signal;
			}
		}
		if (update) {
			Side sside = FMLCommonHandler.instance().getEffectiveSide();
			if (sside.equals(Side.SERVER)) {
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
						501);
				sheduleRemoteUpdate  = true;
			}
			if (signal.equals(Signal.PULSE)
					|| signal.equals(Signal.NEGATIVEPULSE))
				sheduleUpdate(2);

		}

	}

	@Override
	protected void initialize() {
		super.initialize();

	}

	private ItemStack[] calcinatorItemStacks = new ItemStack[3];

	@Override
	public void validate() {
		super.validate();
	};

	@Override
	public boolean canUpdate() {
		return true;
	};

	private void sheduledUpdate(Long updatetime) {
		for (int i = 0; i < 6; i++) {
			Signal spower = sidePowered[i];
			if (spower.equals(Signal.PULSE)) {
				setSidePowered(ForgeDirection.getOrientation(i), Signal.OFF);
			} else if (spower.equals(Signal.NEGATIVEPULSE)) {
				setSidePowered(ForgeDirection.getOrientation(i), Signal.ON);
			}
		}
	}

	private void sheduleUpdate(long timeto) {
		sheduledUpdates.add(new Long(worldObj.getTotalWorldTime() + timeto));
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (needsLoad)
			loadData();
		if (sheduledUpdates.size() > 0) {
			List<Long> remove = new ArrayList<Long>();
			for (Long updt : sheduledUpdates)
				if (worldObj.getTotalWorldTime() >= updt) {
					remove.add(updt);
					sheduledUpdate(updt);
				}
			if (remove.size() > 0)
				for (Long rem : remove)
					sheduledUpdates.remove(rem);
		}
		for (ExtenderChannel chn : channels) {
			if (chn.getFunction() instanceof BC3Function)
				((BC3Function) chn.getFunction()).onUpdate(chn);
		}
		if (sheduleRemoteUpdate) {
			PacketExtenderLiteData.sendUpdateToClients(this);
			sheduleRemoteUpdate = false;
		}
	};

	private void loadData() {
		needsLoad = false;
		inChannels = 0;
		outChannels = 0;
		for (int i = 0; i < 6; i++)
			getSideChannels(i).clear();
		for (int i = 0; i < 6; i++)
			setSidePowered(ForgeDirection.values()[i], Signal.OFF, false);
		for (ExtenderChannelNetworkData dt : loadChannelData) {
			injectChannel(dt);
		}
		for (ExtenderChannel chn : channels) {
			if (chn.getType().equals(ExtenderChannel.TYPES.INPUT))
				inChannels++;
			else outChannels++;
		}
		
		loadChannelData = null;
	}

	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		needsLoad = true;

		if (nbtTagCompound.hasKey("cuuid")) {
			connectedController = UUID.fromString(nbtTagCompound.getString("cuuid"));
			connectedControllerName = nbtTagCompound.getString("cname");
		}
		loadChannelData = new ArrayList<ExtenderChannelNetworkData>();
		for (int i = 0; i < 128; i++) {
			if (nbtTagCompound.hasKey("channel-" + i)) {
				try {
					InputStream is = new ByteArrayInputStream(
							nbtTagCompound.getByteArray("channel-" + i));
					DataInputStream dis = new DataInputStream(is);
					loadChannelData.add(ExtenderChannel.readFrom(dis));
				} catch (IOException ex) {
					LogHelper.error("exception durring reading data!");
				}
			}
		}

	}

	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		if (connectedController != null) {
			nbtTagCompound.setString("cuuid", connectedController.toString());
			nbtTagCompound.setString("cname", connectedControllerName);
		} else {
			nbtTagCompound.removeTag("cuuid");
			nbtTagCompound.removeTag("cname");
		}

		for (int i = 0; i < 128; i++) {
			if (i < channels.size()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream w = new DataOutputStream(baos);

					ExtenderChannel chan = channels.get(i);
					chan.saveTo(w);
					w.flush();

					byte[] result = baos.toByteArray();
					nbtTagCompound.setByteArray("channel-" + i, result);

				} catch (IOException ex) {
					LogHelper.error("exception durring writing data!");
				}
			} else {
				if (nbtTagCompound.hasKey("channel-" + i))
					nbtTagCompound.removeTag("channel-" + i);
			}
		}

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
		return "container.plcExtender"; // + ModBlocks.CALCINATOR_NAME;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public void sendBroadcastSearch() {
		controllersInRange = new ArrayList<SearchResponseEvent>();
		SearchEvent event = new SearchEvent(this,
				PLCEvent.TARGETTYPE.CONTROLLER, null);
		PLC.instance.fireEvent(event);
	}

	public List<SearchResponseEvent> getControllerInRange() {
		return controllersInRange;
	}


	private void createChannels(SearchResponseEvent linkto) {
		channels.clear();

		redstoneListener.clear();
		changeListener.clear();
		tickListener.clear();
		inChannels = 0;
		outChannels = 0;

		for (int i = 0; i < 6; i++) {
			sideChannels[i].clear();
			sidePowered[i] = Signal.OFF;
		}
		int nmbr = 0;
		for (int i = 0; i < linkto.getInChannels(); i++) {
				ExtenderChannel channel = new ExtenderChannel(this,
							ExtenderChannel.TYPES.INPUT, nmbr++);
					channel.setLinkedChannel(i);
					channels.add(channel);
					inChannels++;
				
		}
		for (int i = 0; i < linkto.getOutChannels(); i++) {
			ExtenderChannel channel = new ExtenderChannel(this,
						ExtenderChannel.TYPES.OUTPUT, nmbr++);
				channel.setLinkedChannel(i);
				channels.add(channel);
				outChannels++;
		}
		
	}




	public List<ExtenderChannel> getChannels() {
		return channels;
	}

	public void checkRedstonePower() {
		boolean isRedstonePowered = worldObj.isBlockIndirectlyGettingPowered(
				xCoord, yCoord, zCoord);

		if (this.isRedstonePowered != isRedstonePowered) {
			this.isRedstonePowered = isRedstonePowered;
			for (ExtenderChannel channel : redstoneListener) {
				channel.onRedstoneChanged(isRedstonePowered, false);
			}
		}
		PacketExtenderLiteData.sendUpdateToClients(this);

	}

	public void addRedstoneListener(ExtenderChannel chn) {
		redstoneListener.add(chn);

	}

	public void removeRedstoneListener(ExtenderChannel chn) {
		redstoneListener.remove(chn);
	}

	public void injectChannel(ExtenderChannelNetworkData dt) {
		ExtenderChannel chnn = null;
		for (ExtenderChannel chn : channels)
			if (chn.getType().ordinal() == dt.getType())
				if (chn.getNumber() == dt.getNumber()) {
					chnn = chn;
					break;
				}
		if (chnn == null) {
			chnn = new ExtenderChannel(this,
					ExtenderChannel.TYPES.values()[dt.getType()],
					dt.getNumber());
			channels.add(chnn);
		}
		chnn.inject(dt);
	}

	public void onEvent(PLCEvent event) {
		if (event instanceof SearchResponseEvent) {
			if (controllersInRange == null)
				controllersInRange = new ArrayList<SearchResponseEvent>();
			controllersInRange.add((SearchResponseEvent) event);
			return;
		}

		if (event instanceof SignalEvent) {
			SignalEvent events = (SignalEvent) event;
			
			for (ExtenderChannel channel : channels) {
				if (channel.getType().equals(ExtenderChannel.TYPES.OUTPUT))
					if (channel.getLinkedChannel() == events.getChannel()) {	
						//LogHelper.info(channel+" executes");
						channel.onSignal(events.getSignal());
					}
			}
		}
	}

	public void link(SearchResponseEvent linkto) {
		connectedController = linkto.getUuid();
		connectedControllerName = linkto.getName();
		LogHelper.info("controller is now " + connectedController);
		createChannels(linkto);
	}
	public void linkFromPackage(UUID controller, String controllerName) {
		connectedController = controller;
		connectedControllerName = controllerName;
		LogHelper.info("controller is now " + connectedController);
	}
	public void unlink() {
		connectedController = null;
		connectedControllerName = null;
		channels.clear();
		redstoneListener.clear();
		changeListener.clear();
		tickListener.clear();
	}
	
	
	
	public UUID getConnectedController() {
		return connectedController;
	}

	public String getConnectedControllerName() {
		return connectedControllerName;
	}

	public int getInChannels() {
		return inChannels;
	}

	public int getOutChannels() {
		return outChannels;
	}


}
