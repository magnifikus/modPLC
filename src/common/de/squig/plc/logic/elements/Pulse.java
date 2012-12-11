package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.controller.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.helper.LogHelper;

public class Pulse extends CircuitElement {
	
	private Signal oldSignal = Signal.OFF;
	
	public Pulse(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, ElementFunction.PULSE);
		setTexture(LogicTextureTile.LOGIC_PULSE);
		name = "Pulse Generator";
	}

	@Override
	public void simulate() {
		
		simulated = true;
		if (oldSignal.equals(Signal.OFF)) {
		
			if (inSignal.equals(Signal.ON)) {
				oldSignal = inSignal;
				setSignal(Signal.PULSE);
			}
			if (inSignal.equals(Signal.PULSE)) {
				oldSignal = Signal.OFF;
				setSignal(Signal.PULSE);
			}
			//setSignal(Signal.OFF);
		} else if (oldSignal.equals(Signal.ON)) {
			setSignal(Signal.OFF);
		} 
		if (oldSignal.equals(Signal.ON) && inSignal.equals(Signal.OFF))
			oldSignal = Signal.OFF;
		
	}
	
	
}
