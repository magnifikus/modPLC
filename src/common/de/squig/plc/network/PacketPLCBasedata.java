package de.squig.plc.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
import de.squig.plc.tile.TilePLC;

public class PacketPLCBasedata extends PLCPacket {
	// Basedata
	public int dimid, x, y, z;

	// Replydata

	private UUID uuid;
	private short side;
	private Player player;

	public PacketPLCBasedata() {
		super(PacketTypeHandler.BASEDATA, true);
	}

	public void setCoords(int dimid, int x, int y, int z) {
		this.dimid = dimid;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(dimid);
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		if (uuid == null)
			data.writeBoolean(false);
		else {
			data.writeBoolean(true);
			data.writeUTF(uuid.toString());
			data.writeShort(side);
		}
		
	}

	public void readData(DataInputStream data) throws IOException {
		this.dimid = data.readInt();
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		boolean response = data.readBoolean();
		if (response) {
			uuid = UUID.fromString(data.readUTF());
			side = data.readShort();
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
			if (side == Side.CLIENT && uuid != null) {
				if (worldObj.getWorldInfo().getDimension() == dimid) {
					TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
					if (tile != null && tile instanceof TilePLC) {
						((TilePLC) tile).setBaseData(this);
					}
				}
			}
			else if (side == Side.SERVER && uuid == null) {
				if (worldObj.getWorldInfo().getDimension() == dimid) {
					TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
					if (tile instanceof TilePLC) {
						TilePLC tplc = ((TilePLC) tile);
						this.player = player;
						if (tplc.isInit()) {
							sendResponse(tplc);
						} else {
							tplc.getDelayedBaseData().add(this);
						}
					
					}
				}
			}

		}
	}
	
	public void sendResponse(TilePLC tplc) {
		UUID uuid = tplc.getUuid();
		PacketPLCBasedata pkg = new PacketPLCBasedata();
		pkg.setCoords(tplc.getWorldObj().getWorldInfo().getDimension(), 
				tplc.xCoord, tplc.yCoord, tplc.zCoord);
		pkg.setUuid(uuid); 
		pkg.setSide(tplc.getSide());
		PacketDispatcher.sendPacketToPlayer(PacketTypeHandler.populatePacket(pkg),player);
	}
	
	public static void requestDataFromServer(TilePLC tilePLC) {
		PacketPLCBasedata pkg = new PacketPLCBasedata();
		pkg.setCoords(tilePLC.getWorldObj().getWorldInfo().getDimension(), 
				tilePLC.xCoord, tilePLC.yCoord, tilePLC.zCoord);
		PacketDispatcher.sendPacketToServer(PacketTypeHandler.populatePacket(pkg));
	}
	

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public short getSide() {
		return side;
	}

	public void setSide(short side) {
		this.side = side;
	}


}