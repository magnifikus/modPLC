
/**
 * PacketTypeHandler
 * 
 * Handler that routes packets to the appropriate destinations depending on what kind of packet they are
 * 
 * @author pahimar
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 * modified by magnifikus with packettypes
 * 
 */
package de.squig.plc.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;

public enum PacketTypeHandler {
	BASEDATA(PacketPLCBasedata.class),
	CIRCUITDATA(PacketControllerData.class),
	EXTENDERDATA(PacketExtenderData.class),
	TILE(PacketTileUpdate.class),
	EXTENDERLITEDATA(PacketExtenderLiteData.class);
	
	private Class<? extends PLCPacket> clazz;

	PacketTypeHandler(Class<? extends PLCPacket> clazz) {
		this.clazz = clazz;
	}

	public static PLCPacket buildPacket(byte[] data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		int selector = bis.read();
		
		DataInputStream dis = new DataInputStream(bis);

		PLCPacket packet = null;

		try {
			packet = values()[selector].clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		packet.readPopulate(dis);

		return packet;
	}

	public static PLCPacket buildPacket(PacketTypeHandler type) {
		PLCPacket packet = null;

		try {
			packet = values()[type.ordinal()].clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return packet;
	} 

	public static Packet populatePacket(PLCPacket plcPacket) {
		byte[] data = plcPacket.populate();

		Packet250CustomPayload packet250 = new Packet250CustomPayload();
		packet250.channel = "modPLCChannel12";
		packet250.data = data;
		packet250.length = data.length;
		packet250.isChunkDataPacket = plcPacket.isChunkDataPacket;

		return packet250;
	}
}