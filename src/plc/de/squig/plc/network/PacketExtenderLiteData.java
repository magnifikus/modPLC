package de.squig.plc.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.CircuitStateNetworkData;
import de.squig.plc.logic.PoweredMapNetworkData;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.CircuitElementNetworkData;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TileExtender;

public class PacketExtenderLiteData extends PLCPacket {

	public int x, y, z;
	

	private Signal[] inSignals = new Signal[6];

	private TileExtender extender  = null;
	
	
	public PacketExtenderLiteData() {
		super(PacketTypeHandler.EXTENDERLITEDATA, true);
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
	// 
	// 6x Signal [char]
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		data.writeChar(extender.getSideSignal(ForgeDirection.UP).ordinal());
		data.writeChar(extender.getSideSignal(ForgeDirection.DOWN).ordinal());
		data.writeChar(extender.getSideSignal(ForgeDirection.NORTH).ordinal());
		data.writeChar(extender.getSideSignal(ForgeDirection.SOUTH).ordinal());
		data.writeChar(extender.getSideSignal(ForgeDirection.EAST).ordinal());
		data.writeChar(extender.getSideSignal(ForgeDirection.WEST).ordinal());
		data.writeBoolean(false);
	}

	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		inSignals[0] = Signal.fromOrdinal(data.readChar());
		inSignals[1] = Signal.fromOrdinal(data.readChar());
		inSignals[2] = Signal.fromOrdinal(data.readChar());
		inSignals[3] = Signal.fromOrdinal(data.readChar());
		inSignals[4] = Signal.fromOrdinal(data.readChar());
		inSignals[5] = Signal.fromOrdinal(data.readChar());
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
			if (side == Side.CLIENT) {
				TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
				if (tile != null && tile instanceof TileExtender) {
					TileExtender extender = (TileExtender) tile;
					extender.setSidePowered(ForgeDirection.UP, inSignals[0]);
					extender.setSidePowered(ForgeDirection.DOWN, inSignals[1]);
					extender.setSidePowered(ForgeDirection.NORTH, inSignals[2]);
					extender.setSidePowered(ForgeDirection.SOUTH, inSignals[3]);
					extender.setSidePowered(ForgeDirection.EAST, inSignals[4]);
					extender.setSidePowered(ForgeDirection.WEST, inSignals[5]);
					LogHelper.info("ExtenderLiteUpdate executed");
				}
			}
		}
	}

	
	
	public static void sendUpdateToClients (TileExtender extender) {
		
		
		PacketExtenderLiteData pkg = new PacketExtenderLiteData();
		pkg.setExtender(extender);
		pkg.setCoords(extender.xCoord, extender.yCoord, extender.zCoord);
		Packet packet = PacketTypeHandler.populatePacket(pkg);
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		// only server can do!
		if (side == Side.SERVER) {
			// Server
			PacketDispatcher.sendPacketToAllAround(extender.xCoord, extender.yCoord, extender.zCoord, 64, extender.getWorldObj().getWorldInfo().getDimension() , packet);	
		}
	}
	
}