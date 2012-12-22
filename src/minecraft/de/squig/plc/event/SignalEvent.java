package de.squig.plc.event;

import java.util.UUID;

import de.squig.plc.PLC;
import de.squig.plc.logic.Signal;
import de.squig.plc.tile.TilePLC;

public class SignalEvent extends PLCEvent {
	private Signal signal;
	private int channel;
	private UUID dest;
	public SignalEvent(TilePLC source, UUID dest, Signal signal, int channel) {
		super(source, dest);
		this.signal = signal;
		this.channel = channel;
	}
	
	
	public Signal getSignal() {
		return signal;
	}
	public int getChannel() {
		return channel;
	}

	
	public static void fireDirected (TilePLC src, UUID dest, Signal signal, int chn) {
		SignalEvent event = new SignalEvent(src,dest, signal,chn);
		PLC.instance.getNetworkBroker().fireEvent(event);
	}
	
}
