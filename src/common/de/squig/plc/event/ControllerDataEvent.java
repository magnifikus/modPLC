package de.squig.plc.event;

import java.util.List;
import java.util.UUID;

import de.squig.plc.event.payloads.ControllerDataPayload;
import de.squig.plc.logic.Signal;
import de.squig.plc.tile.TilePLC;

public class ControllerDataEvent extends PLCEvent {
	private boolean request = true;
	
	private List<ControllerDataPayload> payload;
		
	public ControllerDataEvent(TilePLC source, UUID dest,  List<ControllerDataPayload> payload ) {
		super(source, dest);
		this.payload = payload;
	}

	public List<ControllerDataPayload> getPayload() {
		return payload;
	}

	
	

}
