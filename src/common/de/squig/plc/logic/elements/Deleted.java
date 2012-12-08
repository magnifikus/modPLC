package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Deleted extends CircuitElement {

	
	public Deleted(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.DELETED, null);
		name = "I AM DELETED";
	}

}
