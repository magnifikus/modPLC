package de.squig.plc.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.function.DisabledFunction;
import de.squig.plc.tile.TileExtender;

public class PacketExtenderLiteData extends PLCPacket {

	public int x, y, z;

	private List<Character> resC = null;
	private List<Boolean> res = null;
	private List<Boolean> resT = null;
	private int ins = 0;
	private int outs = 0;

	private TileExtender extender = null;

	public PacketExtenderLiteData() {
		super(PacketTypeHandler.EXTENDERLITEDATA, true);
	}

	public void setCoords(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setExtender(TileExtender extender) {
		this.extender = extender;
	}

	// int x,y,z
	//
	// int in, int out
	// boolean en, boolean on

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.equals(Side.SERVER)) {
			data.writeChar(extender.getChannelsIn().size());
			data.writeChar(extender.getChannelsOut().size());

			List<Character> resC = new LinkedList<Character>();
			List<Boolean> res = new ArrayList<Boolean>();
			List<Boolean> resT = new ArrayList<Boolean>();

			for (ExtenderChannel chn : extender.getChannelsIn()) {
				if (chn.getFunction() != null
						&& !(chn.getFunction() instanceof DisabledFunction)) {
					resC.add((char) chn.getNumber());
					res.add(!chn.getSignal().equals(Signal.OFF));
					resT.add(chn.getType().equals(ExtenderChannel.TYPES.INPUT));
				}
			}
			for (ExtenderChannel chn : extender.getChannelsOut()) {
				if (chn.getFunction() != null
						&& !(chn.getFunction() instanceof DisabledFunction)) {
					resC.add((char) chn.getNumber());
					res.add(!chn.getSignal().equals(Signal.OFF));
					resT.add(chn.getType().equals(ExtenderChannel.TYPES.INPUT));
				}
			}

			data.writeChar((char) resC.size());
			int i = 0;
			for (Character c : resC) {
				data.writeChar(c);
				data.writeBoolean(res.get(i));
				data.writeBoolean(resT.get(i++));
			}

		} else {
			data.writeChar(0);
			data.writeChar(0);
		}
		data.writeBoolean(false);
	}

	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.equals(Side.CLIENT)) {
			ins = data.readChar();
			outs = data.readChar();

			int i = data.readChar();
			resC = new ArrayList<Character>();
			res = new ArrayList<Boolean>();
			resT = new ArrayList<Boolean>();

			for (int j = 0; j < i; j++) {
				resC.add(data.readChar());
				res.add(data.readBoolean());
				boolean t = data.readBoolean();
				resT.add(t);

			}
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
			if (side == Side.CLIENT) {
				TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
				if (tile != null && tile instanceof TileExtender) {
					TileExtender extender = (TileExtender) tile;
					char[] in = new char[ins];
					char[] out = new char[outs];

					for (int i = 0; i < in.length; i++)
						in[i] = 0;
					for (int i = 0; i < out.length; i++)
						out[i] = 0;
					int i = 0;
					for (Character c : resC) {

						if (resT.get(i) && in.length > c) {
							if (res.get(i))
								in[c] = 2;
							else
								in[c] = 1;
						} else if (!resT.get(i) && out.length > c)
							if (res.get(i))
								out[c] = 2;
							else
								out[c] = 1;
						i++;
					}
					extender.updateStatus(in, out);
					//LogHelper.info("ExtenderLiteUpdate executed");
				}

			} else if (side == Side.SERVER) {
				TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
				if (tile != null && tile instanceof TileExtender) {
					TileExtender extender = (TileExtender) tile;
					extender.sheduleRemoteUpdate();
				}
			}
		}

	}

	public static void sendUpdateToClients(TileExtender extender) {
		PacketExtenderLiteData pkg = new PacketExtenderLiteData();
		pkg.setExtender(extender);
		pkg.setCoords(extender.xCoord, extender.yCoord, extender.zCoord);
		Packet packet = PacketTypeHandler.populatePacket(pkg);
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		// only server can do!
		if (side == Side.SERVER) {
			// Server
			PacketDispatcher.sendPacketToAllAround(extender.xCoord,
					extender.yCoord, extender.zCoord, 32, extender
							.getWorldObj().getWorldInfo().getDimension(),
					packet);
		}
	}

	public static void requestUpdateFromServer(TileExtender extender) {
		PacketExtenderLiteData pkg = new PacketExtenderLiteData();
		pkg.setExtender(extender);
		pkg.setCoords(extender.xCoord, extender.yCoord, extender.zCoord);
		Packet packet = PacketTypeHandler.populatePacket(pkg);
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		// only client can do!
		if (side == Side.CLIENT) {
			// Server
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

}