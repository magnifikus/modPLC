package de.squig.plc.event.payloads;

import de.squig.plc.logic.Signal;

public class ControllerDataPayload {
	public int channel;
	public Signal signal;
	
	public ControllerDataPayload(int channel, Signal signal) {
		this.channel = channel;
		this.signal = signal;
	}
}
