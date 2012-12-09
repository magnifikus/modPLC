package de.squig.plc.event;

import java.util.UUID;

import net.minecraft.src.World;
import de.squig.plc.event.PLCEvent.TARGETTYPE;
import de.squig.plc.tile.TilePLC;

public class PLCEventSubscriber {
	private UUID uuid;

	TilePLC tile;
	private PLCEvent.TARGETTYPE targetType;
	public PLCEventSubscriber(TilePLC tile) {
		super();
		this.tile = tile;
		this.uuid = tile.getUuid();
		this.targetType = tile.getTargettype();
	}
	
	


	public UUID getUuid() {
		return uuid;
	}



	public PLCEvent.TARGETTYPE getTargetType() {
		return targetType;
	}
	public void setTargetType(PLCEvent.TARGETTYPE targetType) {
		this.targetType = targetType;
	}




	public TilePLC getTile() {
		return tile;
	}
	
	
	
}
