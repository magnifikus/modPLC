package de.squig.plc.logic.elements;

import java.io.DataInputStream;

public class CircuitElementNetworkData {
	public char mapY;
	public char mapX;
	public short typeID;
	public short functionID;
	public char linkNumber;
	public short flags;
	public short customFlags;
	
	public CircuitElementNetworkData(char mapX, char mapY, short typeID,
			short functionID, char linkNumber, short flags, short customFlags) {
		super();
		this.mapY = mapY;
		this.mapX = mapX;
		this.typeID = typeID;
		this.functionID = functionID;
		this.linkNumber = linkNumber;
		this.flags = flags;
		this.customFlags = customFlags;
	}

	

}
