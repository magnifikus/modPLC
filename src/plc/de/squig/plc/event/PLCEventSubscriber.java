package de.squig.plc.event;

import java.util.UUID;

import net.minecraft.src.World;
import de.squig.plc.event.PLCEvent.TARGETTYPE;

public class PLCEventSubscriber {
	private UUID uuid;
	private int dim;
	private int x;
	private int y;
	private int z;
	private PLCEvent.TARGETTYPE targetType;
	public PLCEventSubscriber(UUID uuid,int dim, int x, int y, int z,
			TARGETTYPE targetType) {
		super();
		this.uuid = uuid;
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	
		this.targetType = targetType;
	}
	
	


	public UUID getUuid() {
		return uuid;
	}

	public int getDim() {
		return dim;
	}
	public void setDim(int dim) {
		this.dim = dim;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}

	public PLCEvent.TARGETTYPE getTargetType() {
		return targetType;
	}
	public void setTargetType(PLCEvent.TARGETTYPE targetType) {
		this.targetType = targetType;
	}
	
	
	
}
