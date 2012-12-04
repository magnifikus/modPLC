package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Pulse extends CircuitElement {
	
	private Signal oldSignal = Signal.OFF;
	
	public Pulse(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.PULSE, ElementFunction.PULSE);
		setTexture(LogicTextureTile.LOGIC_PULSE);
		name = "Pulse Generator";
	}
	protected Signal manipulateSignal(Signal signal) {
		// my old was on/pulse/nega  ==> off
		//Signal newRemb = oldSignal;
		if (oldSignal.equals(Signal.OFF)) {
			if (signal.equals(Signal.ON)) {
				oldSignal = signal;
				return Signal.PULSE;
			}
			if (signal.equals(Signal.PULSE)) {
				oldSignal = Signal.OFF;
				return Signal.PULSE;
			}
			return Signal.OFF;
		}
		if (oldSignal.equals(Signal.ON)) {
			oldSignal = signal;
			return Signal.OFF;
		}
		
		/*
		Signal newOld = oldSignal;
		
		if (signal.equals(Signal.OFF))
			newOld = signal;
		
		if (!oldSignal.equals(Signal.OFF)) {
			signal = Signal.OFF;
		} else {
			if (!signal.equals(Signal.OFF))
				signal = Signal.PULSE;
		}
		
		oldSignal = newOld;
		*/
		return signal;
	}
	
}
