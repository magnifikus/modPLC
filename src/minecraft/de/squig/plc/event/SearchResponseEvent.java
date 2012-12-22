package de.squig.plc.event;

import java.util.UUID;

import de.squig.plc.tile.TilePLC;

public class SearchResponseEvent extends PLCEvent {
	private int distance;
	private UUID uuid;
	private String name;
	private int inChannels;
	private int outChannels;
	
	public SearchResponseEvent(TilePLC source, UUID dest, int distance, UUID uuid, String name,
			int inChannels, int outChannels) {
		super(source, dest);
		setServer(false);
		setClient(true);
		this.distance = distance;
		this.uuid = uuid;
		this.name = name;
		this.inChannels = inChannels;
		this.outChannels = outChannels;
	}
	public int getDistance() {
		return distance;
	}
	public UUID getUuid() {
		return uuid;
	}
	public String getName() {
		return name;
	}
	public int getInChannels() {
		return inChannels;
	}
	public int getOutChannels() {
		return outChannels;
	}
	
	
	
}
