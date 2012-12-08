package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.helper.LogHelper;

public class Not extends CircuitElement {

	
	public Not(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.NOT, ElementFunction.NOT);
		setTexture(LogicTextureTile.LOGIC_NOT);
		name = "NOT (Inverter)";
	}
	protected Signal manipulateSignal(Signal signal) {
		return signal.invert();
	}
	@Override
	public void simulate() {
		setSimulated(true);
		setSignal(inSignal.invert());
	
		
	}
}
