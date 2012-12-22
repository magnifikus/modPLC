package de.squig.plc.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.ExtenderChannelNetworkData;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TileExtender;

public class PacketExtenderData extends PLCPacket {

	public int x, y, z;
	public int dimId;
	
	private UUID cUUID = null;
	private String cName = null;
	private Boolean cConnected = null;
	private List<ExtenderChannelNetworkData> channelsIn = null;
	private List<ExtenderChannelNetworkData> channelsOut = null;
	
	private TileExtender extender;
	
	
	
	public PacketExtenderData() {
		super(PacketTypeHandler.EXTENDERDATA, true);
	}

	public void setCoords(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setExtender (TileExtender extender) {
		this.extender = extender;
	}


	// int x,y,z
	// String id
	//
	// bool state
	// state...
	// bool elements 
	// bool elementsAll
	// elements....
	// bool objects
	// objects...
	// bool poweredState
	// poweredMap...
	// bool more
	//
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(extender.getWorldObj().getWorldInfo().getDimension());
		data.writeInt(extender.xCoord);
		data.writeInt(extender.yCoord);
		data.writeInt(extender.zCoord);
		if (extender.getConnectedController()!= null) {
			data.writeBoolean(true);
			data.writeUTF(extender.getConnectedController().toString());
			data.writeUTF(extender.getConnectedControllerName());
		} else  {
			data.writeBoolean(false);
		}
		
		data.writeChar(extender.getChannelsIn().size());
		for (ExtenderChannel channel : extender.getChannelsIn()) {
			channel.saveTo(data);
		}
		data.writeChar(extender.getChannelsOut().size());
		for (ExtenderChannel channel : extender.getChannelsOut()) {
			channel.saveTo(data);
		}
		data.writeBoolean(false);
	}

	public void readData(DataInputStream data) throws IOException {
		this.dimId = data.readInt();
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.cConnected = data.readBoolean();
		if (cConnected) {
			cUUID = UUID.fromString(data.readUTF());
			cName = data.readUTF();
		} 
		int dsize = data.readChar();
		channelsIn = new ArrayList<ExtenderChannelNetworkData>();
		for (int i = 0; i < dsize; i++) {
			channelsIn.add(ExtenderChannel.readFrom(data));
		}
		
		dsize = data.readChar();
		channelsOut = new ArrayList<ExtenderChannelNetworkData>();
		for (int i = 0; i < dsize; i++) {
			channelsOut.add(ExtenderChannel.readFrom(data));
		}
		
		
	}

	public void execute(INetworkManager manager, Player player) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		World worldObj = null;
		if (side == Side.CLIENT) {
			if (player instanceof EntityClientPlayerMP)
				worldObj = ((EntityClientPlayerMP) player).worldObj;
		} else if (side == Side.SERVER) {
			
			if (player instanceof EntityPlayerMP)
				worldObj = ((EntityPlayerMP) player).worldObj;
		}

		if (worldObj != null) {
			TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
			if (tile != null && tile instanceof TileExtender) {
				TileExtender tileE = (TileExtender) tile;
				
				if (cConnected) {
					if (tileE.getConnectedController() != null 
							&& !tileE.getConnectedController().equals(cUUID))
						tileE.unlink();
					tileE.linkFromPackage(cUUID, cName);
				} else {
					tileE.unlink();
				}
				for (ExtenderChannelNetworkData dt : channelsIn) {
					tileE.injectChannel(ExtenderChannel.TYPES.INPUT,dt);
				}
				for (ExtenderChannelNetworkData dt : channelsOut) {
					tileE.injectChannel(ExtenderChannel.TYPES.OUTPUT,dt);
				}
				tileE.sheduleNeighbourUpdate();
				tileE.sheduleRemoteUpdate();
				
			}
			
		}
		LogHelper.info("Extender Data executed");
	}

	
	public static void sendElements(TileExtender tile,
			 EntityPlayer player) {
		PacketExtenderData pkg = new PacketExtenderData();
		pkg.setExtender(tile);
		pkg.setCoords(tile.xCoord, tile.yCoord, tile.zCoord);
		Packet packet = PacketTypeHandler.populatePacket(pkg);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// Server
			EntityPlayerMP playermp = (EntityPlayerMP) player;
			PacketDispatcher.sendPacketToPlayer(packet, (Player) playermp);
		
		} else if (side == Side.CLIENT) {
			// Client
			//PacketDispatcher.sendPacketToServer(packet);
			EntityClientPlayerMP playerclient = (EntityClientPlayerMP)player;
			playerclient.sendQueue.addToSendQueue(packet);
		}

	}
	private static void updateArround(TileExtender tile, int i) {
		PacketExtenderData pkg = new PacketExtenderData();
		pkg.setExtender(tile);
		pkg.setCoords(tile.xCoord, tile.yCoord, tile.zCoord);
		Packet packet = PacketTypeHandler.populatePacket(pkg);
		PacketDispatcher.sendPacketToAllAround(tile.xCoord, tile.yCoord, tile.zCoord, i,tile.getWorldObj().getWorldInfo().getDimension() , packet);	
	}
	
	
}