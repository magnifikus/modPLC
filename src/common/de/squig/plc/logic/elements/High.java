package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.controller.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class High extends CircuitElement {

	
	public High(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY,  ElementFunction.HIGH);
		setTexture(LogicTextureTile.LOGIC_HIGH);
		name = "Permanent On";
	}
	
	public static String getDisplayName() {
		return "Permanent On";
	}
	public static int getDisplayTextureId() {
		return 228;
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
