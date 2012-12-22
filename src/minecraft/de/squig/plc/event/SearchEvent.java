package de.squig.plc.event;

import de.squig.plc.event.PLCEvent.TARGETTYPE;
import de.squig.plc.logic.Signal;
import de.squig.plc.tile.TilePLC;

public class SearchEvent extends PLCEvent {
	public SearchEvent(TilePLC source, TARGETTYPE target, Integer range) {
		super(source,target,range);
		setServer(false);
		setClient(true);
	}
}
