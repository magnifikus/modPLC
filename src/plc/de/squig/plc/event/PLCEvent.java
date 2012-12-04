package de.squig.plc.event;

import java.util.UUID;

import de.squig.plc.tile.TilePLC;

public class PLCEvent {
	public enum TARGETTYPE {EXTENDER,CONTROLLER}
	private TilePLC source;
	private UUID dest;
	private Integer x = null;
	private Integer y = null;
	private Integer z = null;
	private Integer dim = null;
	private Integer range = null;
	private TARGETTYPE target = null;
	private boolean server = true;
	private boolean client = false;
	
	// directed
	public PLCEvent(TilePLC source, int dim, int x, int y, int z) {
		super();
		this.source = source;
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	// directed uuid
	public PLCEvent(TilePLC source, UUID dest) {
		super();
		this.source = source;
		this.dest = dest;
	}
	// broadcast
	public PLCEvent(TilePLC source, TARGETTYPE target, Integer range) {
		super();
		this.source = source;
		this.target = target;
		this.range = range;
	}

	public TilePLC getSource() {
		return source;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
	public int getDim() {
		return dim;
	}
	public TARGETTYPE getTarget() {
		return target;
	}
	public boolean isServer() {
		return server;
	}
	public void setServer(boolean server) {
		this.server = server;
	}
	public boolean isClient() {
		return client;
	}
	public void setClient(boolean client) {
		this.client = client;
	}
	public Integer getRange() {
		return range;
	}
	public UUID getDest() {
		return dest;
	}
	
	
	
}
