package de.squig.plc.logic.objects;

public class CircuitObjectNetworkData {
	private short typeID;
	private char linkName;
	private short flags;
	private CircuitObjectData data;
	
	
	public CircuitObjectNetworkData(short typeID, char linkName, short flags,
			CircuitObjectData data) {
		super();
		this.typeID = typeID;
		this.linkName = linkName;
		this.flags = flags;
		this.data = data;
	}
	
	
	public short getTypeID() {
		return typeID;
	}
	public void setTypeID(short typeID) {
		this.typeID = typeID;
	}
	public char getLinkName() {
		return linkName;
	}
	public void setLinkName(char linkName) {
		this.linkName = linkName;
	}
	public short getFlags() {
		return flags;
	}
	public void setFlags(short flags) {
		this.flags = flags;
	}
	public CircuitObjectData getData() {
		return data;
	}
	public void setData(CircuitObjectData data) {
		this.data = data;
	}
	
	
}
