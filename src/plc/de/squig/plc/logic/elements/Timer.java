package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Timer extends CircuitElement {

	
	public Timer(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.TIMER ,ElementFunction.TIMEROUTPUT);
		
		functions.add(ElementFunction.TIMERRESET);
		functions.add(ElementFunction.TIMERSTOP);
		functions.add(ElementFunction.TIMEROUTPUT);
		
		setTexture(LogicTextureTile.LOGIC_TIMER);
		name = "Timer";
	
	}
	
	
	
}
