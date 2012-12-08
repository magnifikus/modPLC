package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Output extends CircuitElement {


	public Output(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.OUTPUT,ElementFunction.OUTPUT);
		setTexture(LogicTextureTile.OUTPUT);
		
		functions.add(ElementFunction.OUTPUT);
		functions.add(ElementFunction.OUTPUTSET);
		functions.add(ElementFunction.OUTPUTRESET);
		//functions.add(ElementFunction.MEMORYSET);
		//functions.add(ElementFunction.MEMORYRESET);
		name = "Logic Output";
		setDisplayLink(true);
	
	}

}
