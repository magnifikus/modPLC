package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.controller.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Delay extends CircuitElement {
	
	
	public Delay(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY,ElementFunction.DELAY);
		setTexture(LogicTextureTile.LOGIC_DELAY);
		name = "Delay";
		
	}
	public static String getDisplayName() {
		return "Delay (Delays a Signal)";
	}
	public static int getDisplayTextureId() {
		return 232;
	}
}
