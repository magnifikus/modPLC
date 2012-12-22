package de.squig.plc.logic.elements;

import de.squig.plc.client.gui.controller.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class Output extends CircuitElement {


	public Output(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, ElementFunction.OUTPUT);
		setTexture(LogicTextureTile.OUTPUT);
		functions.add(ElementFunction.OUTPUT);
		functions.add(ElementFunction.OUTPUTSET);
		functions.add(ElementFunction.OUTPUTRESET);
		//functions.add(ElementFunction.MEMORYSET);
		//functions.add(ElementFunction.MEMORYRESET);
		name = "Logic Output";
		setDisplayLink(true);
	}
	public static String getDisplayName() {
		return "Output (Signal to Extenders)";
	}
	public static int getDisplayTextureId() {
		return 225;
	}
}
