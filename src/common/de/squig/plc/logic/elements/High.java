package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class High extends CircuitElement {

	
	public High(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.HIGH, ElementFunction.HIGH);
		setTexture(LogicTextureTile.LOGIC_HIGH);
		name = "Permanent On";
	}
	
	public void evaluate() {
	}
	
	@Override
	public boolean isEvaluated() {
		return true;
	}
	@Override
	public Signal getSignal() {
		return Signal.ON;
	}
	
}
