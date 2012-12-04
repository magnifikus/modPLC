package de.squig.plc.logic.elements;

import java.util.List;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.LogicInput;
import de.squig.plc.logic.objects.LogicMemory;
import de.squig.plc.logic.objects.LogicOutput;

public class Input extends CircuitElement {
	
	public Input(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.INPUT,  ElementFunction.INPUT);
		setTexture(LogicTextureTile.INPUT);
		functions.add(ElementFunction.INPUT);
		functions.add(ElementFunction.OUTPUTREAD);
		functions.add(ElementFunction.MEMORY);
		setDisplayLink(true);
		setAllowInvert(true);
		name = "Logic Input";
	}

}
