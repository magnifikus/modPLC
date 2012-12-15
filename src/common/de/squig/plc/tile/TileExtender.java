
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.PLC;
import de.squig.plc.event.ControllerDataEvent;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.SearchEvent;
import de.squig.plc.event.SearchResponseEvent;
import de.squig.plc.event.SignalEvent;
import de.squig.plc.event.payloads.ControllerDataPayload;
import de.squig.plc.lib.StaticData;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.ExtenderChannel.TYPES;
import de.squig.plc.logic.extender.ExtenderChannelNetworkData;
import de.squig.plc.logic.extender.function.RedstoneFunction;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketExtenderLiteData;

public class TileExtender extends TilePLC implements IInventory {
	public enum TYPE {
		BASIC
	};

	private TYPE type = null;

	private int range = StaticData.ExtenderBaseRange;
	
	
	// SOFT link
	private UUID connectedController = null;
	private String connectedControllerName = null;

	private List<SearchResponseEvent> controllersInRange = null;

	private List<ExtenderChannel> channelsIn = null;
	private List<ExtenderChannel> channelsOut = null;
	
	private List<ExtenderChannel> sheduledChannelUpdates;
	
	
	private List<ExtenderChannel> tickListener = new ArrayList<ExtenderChannel>();
	
	private boolean isRedstonePowered;
	
	
	private boolean remoteUpdate = false;
	private boolean neighbourUpdate = false;

	
	
	// load shit
	private boolean needsLoad = false;
	private List<ExtenderChannelNetworkData> loadChannelDataIn = null;
	private List<ExtenderChannelNetworkData> loadChannelDataOut = null;
	
	
	
	// status
	private char[] instatus = new char[0];
	private char[] outstatus = new char[0];
	
	

	
	protected TileExtender() {
		super(PLCEvent.TARGETTYPE.EXTENDER);
		channelsIn = new ArrayList<ExtenderChannel>();
		channelsOut = new ArrayList<ExtenderChannel>();
		sheduledChannelUpdates = new LinkedList<ExtenderChannel>();
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.equals(Side.CLIENT))
			needsLoad = true;
	
	}
	
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
	

	/**
	 * bc3 support integration
	 * 
	 * @return
	 */
	public static TileExtender createInstance() {
		try {
			Class bc3Extender = PLC.class.getClassLoader().loadClass("de.squig.plc.bc3.compat.TileExtenderBC3");
			Constructor constructor = bc3Extender.getConstructor(null);
			TileExtender res = (TileExtender)constructor.newInstance(null);
			return res;
		} catch (Exception ex) {
			return new TileExtender();
		}
	}
	
	
	public boolean isSidePowered(ForgeDirection side) {
		return getSideSignal(side).equals(Signal.ON);
	}

	public Signal getSideSignal(ForgeDirection side) {
		Signal ret = Signal.OFF;
		for (int i = 0; i < channelsOut.size(); i++) {
			ExtenderChannel chn = channelsOut.get(i);
			if (chn.getFunction() instanceof RedstoneFunction 
					&& (chn.getSide() == 6 || chn.getSide() == side.ordinal())) 
				ret = ret.getHigherSignal(chn.getSignal());
			
		}
		return ret;
	}
	
	public void sheduleNeighbourUpdate() {
		neighbourUpdate = true;
	}
	
	public void sheduleRemoteUpdate() {
		remoteUpdate = true;
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


	public void addChannelToShedule(ExtenderChannel chn) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.equals(Side.CLIENT))
			return;
		if (!sheduledChannelUpdates.contains(chn))
			sheduledChannelUpdates.add(chn);
		
	}
	

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (needsLoad) {
			loadData();
		}
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.equals(Side.CLIENT))
			return;
		long now = worldObj.getTotalWorldTime();
		if (sheduledChannelUpdates.size() > 0)
			for (ExtenderChannel chn : sheduledChannelUpdates) {
				if (now >= chn.getSheduledOn() && chn.getFunction() != null) {
					chn.getFunction().onUpdate(chn,now);
					chn.setSheduledOn(-1);
					sheduledChannelUpdates.remove(chn);
				}			
			}
		if (remoteUpdate) {
			PacketExtenderLiteData.sendUpdateToClients(this);
			remoteUpdate = false;
		}
		if (neighbourUpdate) {
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, StaticData.BlockExtender);
			neighbourUpdate = false;
		}
	};

	private void loadData() {
		needsLoad = false;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.equals(Side.CLIENT)) {
			PacketExtenderLiteData.requestUpdateFromServer(this);
			return;
		}
			
		for (ExtenderChannelNetworkData dt : loadChannelDataIn) {
			injectChannel(ExtenderChannel.TYPES.INPUT,dt);
		}
		for (ExtenderChannelNetworkData dt : loadChannelDataOut) {
			injectChannel(ExtenderChannel.TYPES.OUTPUT,dt);
		}
		
		if (connectedController != null) {
			PLC.instance.getNetworkBroker().addMulticastListener(connectedController, this);
			requestUpdateFromController();
		}
		
		loadChannelDataIn = null;
		loadChannelDataOut = null;
		
	}

	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		needsLoad = true;

		if (nbtTagCompound.hasKey("cuuid")) {
			connectedController = UUID.fromString(nbtTagCompound.getString("cuuid"));
			connectedControllerName = nbtTagCompound.getString("cname");
		}
		loadChannelDataIn = new ArrayList<ExtenderChannelNetworkData>();
		for (int i = 0; i < 64; i++) {
			if (nbtTagCompound.hasKey("I-" + i)) {
				try {
					InputStream is = new ByteArrayInputStream(
							nbtTagCompound.getByteArray("I-" + i));
					DataInputStream dis = new DataInputStream(is);
					loadChannelDataIn.add(ExtenderChannel.readFrom(dis));
				} catch (IOException ex) {
					LogHelper.error("exception durring reading data!");
				}
			}
		}
		loadChannelDataOut = new ArrayList<ExtenderChannelNetworkData>();
		for (int i = 0; i < 32; i++) {
			if (nbtTagCompound.hasKey("O-" + i)) {
				try {
					InputStream is = new ByteArrayInputStream(
							nbtTagCompound.getByteArray("O-" + i));
					DataInputStream dis = new DataInputStream(is);
					loadChannelDataOut.add(ExtenderChannel.readFrom(dis));
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

		for (int i = 0; i < 64; i++) {
			if (i < channelsIn.size()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream w = new DataOutputStream(baos);

					ExtenderChannel chan = channelsIn.get(i);
					chan.saveTo(w);
					w.flush();

					byte[] result = baos.toByteArray();
					nbtTagCompound.setByteArray("I-" + i, result);

				} catch (IOException ex) {
					LogHelper.error("exception durring writing data!");
				}
			} else {
				if (nbtTagCompound.hasKey("I-" + i))
					nbtTagCompound.removeTag("I-" + i);
			}
		}
		for (int i = 0; i < 32; i++) {
			if (i < channelsOut.size()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream w = new DataOutputStream(baos);

					ExtenderChannel chan = channelsOut.get(i);
					chan.saveTo(w);
					w.flush();

					byte[] result = baos.toByteArray();
					nbtTagCompound.setByteArray("O-" + i, result);

				} catch (IOException ex) {
					LogHelper.error("exception durring writing data!");
				}
			} else {
				if (nbtTagCompound.hasKey("O-" + i))
					nbtTagCompound.removeTag("O-" + i);
			}
		}

	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return 0;
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int i) {
		return null;
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
		return 0;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public void sendBroadcastSearch() {
		controllersInRange = new ArrayList<SearchResponseEvent>();
		SearchEvent event = new SearchEvent(this,PLCEvent.TARGETTYPE.CONTROLLER, null);
		PLC.instance.getNetworkBroker().fireEvent(event);
	}

	public List<SearchResponseEvent> getControllerInRange() {
		return controllersInRange;
	}


	private void createChannels(SearchResponseEvent linkto) {
		channelsIn.clear();
		channelsOut.clear();
		tickListener.clear();
		char[] ins = new char[linkto.getInChannels()];
		char[] outs = new char[linkto.getOutChannels()];

	
		for (int i = 0; i < linkto.getInChannels(); i++) {
				ExtenderChannel channel = new ExtenderChannel(this,
							ExtenderChannel.TYPES.INPUT, i);
				
				channelsIn.add(channel);
				ins[i] = 0;	
				
		}
		for (int i = 0; i < linkto.getOutChannels(); i++) {
			ExtenderChannel channel = new ExtenderChannel(this,
						ExtenderChannel.TYPES.OUTPUT, i);
				
				channelsOut.add(channel);
				outs[i] = 0;
		}
		updateStatus(ins, outs);
	}




	public void checkRedstonePower() {
		boolean isRedstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		for (ExtenderChannel channel : channelsIn) {
			if (channel.getFunction() instanceof RedstoneFunction)
				channel.onRedstoneChanged(isRedstonePowered, false);
		}
	

	}

	public void injectChannel(TYPES input, ExtenderChannelNetworkData dt) {
		ExtenderChannel chnn = null;
		List<ExtenderChannel> chns = channelsIn;
		if (input.equals(ExtenderChannel.TYPES.OUTPUT))
			chns = channelsOut;
		
		for (ExtenderChannel chn : chns)
			if (chn.getType().ordinal() == dt.getType())
				if (chn.getNumber() == dt.getNumber()) {
					chnn = chn;
					break;
				}
		if (chnn == null) {
			chnn = new ExtenderChannel(this,
					ExtenderChannel.TYPES.values()[dt.getType()],
					dt.getNumber());
			chns.add(chnn);
		}
		chnn.inject(dt);
	}

	public void onEvent(PLCEvent event) {
		if (event instanceof ControllerDataEvent) {
			ControllerDataEvent eventd = (ControllerDataEvent) event;
			for (ControllerDataPayload load : eventd.getPayload()) {
				if (load.channel < channelsOut.size()) {
					ExtenderChannel chn = channelsOut.get(load.channel);
					chn.onSignal(load.signal);
				}
			}
			
		} else if (event instanceof SignalEvent) {
			SignalEvent events = (SignalEvent) event;
			int chn = events.getChannel();
			
			ExtenderChannel channel;
			if (chn < channelsOut.size() && (channel = channelsOut.get(chn)) != null)
				channel.onSignal(events.getSignal());
			else LogHelper.warn("cound not route event to channel "+events.getChannel());
		} else if (event instanceof SearchResponseEvent) {
			if (controllersInRange == null)
				controllersInRange = new ArrayList<SearchResponseEvent>();
			controllersInRange.add((SearchResponseEvent) event);
			return;
		}

		
	}

	public void link(SearchResponseEvent linkto) {
		connectedController = linkto.getUuid();
		connectedControllerName = linkto.getName();
		createChannels(linkto);
		
	}
	public void linkFromPackage(UUID controller, String controllerName) {
		if (connectedController != null)
			PLC.instance.getNetworkBroker().removeMulticastListener(connectedController, this);
		boolean requestUpdate = (this.connectedController == null || !this.connectedController.equals(controller));
		connectedController = controller;
		connectedControllerName = controllerName;
		if (connectedController != null)
			PLC.instance.getNetworkBroker().addMulticastListener(connectedController, this);
		
		if (requestUpdate) 
			requestUpdateFromController();
		
	
	}
	
	
	public void unlink() {
		if (connectedController != null)
			PLC.instance.getNetworkBroker().removeMulticastListener(connectedController, this);
		connectedController = null;
		connectedControllerName = null;
		channelsIn.clear();
		channelsOut.clear();
		tickListener.clear();
		sheduleNeighbourUpdate();
		sheduleRemoteUpdate();
	}
	
	public void requestUpdateFromController() {
		PLC.instance.getNetworkBroker().fireEvent(new ControllerDataEvent(this, connectedController, null));
	}
	
	public UUID getConnectedController() {
		return connectedController;
	}

	public String getConnectedControllerName() {
		return connectedControllerName;
	}

	public List<ExtenderChannel> getChannelsIn() {
		return channelsIn;
	}

	public List<ExtenderChannel> getChannelsOut() {
		return channelsOut;
	}

	
	

}
