package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Counter extends CircuitElement {
	
	
	public Counter(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.COUNTER,ElementFunction.COUNTERTOP);
		setTexture(LogicTextureTile.LOGIC_COUNTER);
		functions.add(ElementFunction.COUNTERTOP);
		functions.add(ElementFunction.COUNTERBOTTOM);
		functions.add(ElementFunction.COUNTERRESET);
		functions.add(ElementFunction.COUNTERSTOP);
		functions.add(ElementFunction.COUNTERCOUNTUP);
		functions.add(ElementFunction.COUNTERCOUNTDOWN);
		
		name = "Counter";
		
	}
}
