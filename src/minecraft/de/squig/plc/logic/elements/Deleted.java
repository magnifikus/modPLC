package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.controller.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Deleted extends CircuitElement {

	
	public Deleted(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY,  null);
		name = "I AM DELETED";
	}
	public static String getDisplayName() {
		return "Delete this element [BACKSPACE]";
	}
	public static int getDisplayTextureId() {
		return 240;
	}
}
