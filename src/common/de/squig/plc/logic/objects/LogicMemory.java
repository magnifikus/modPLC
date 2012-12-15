package de.squig.plc.logic.objects;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class LogicMemory extends CircuitObject {
	protected CircuitObjectOutputPin out = new CircuitObjectOutputPin(this, "Memory Output");;
	protected CircuitObjectInputPin inSet = new CircuitObjectInputPin(this, "Memory Set");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Memory Reset");
	
	
	public LogicMemory(Circuit circuit, short linkNumber) {
		super(circuit,null);
		setLinkNumber(linkNumber);
		addInputPin(inSet);
		addInputPin(inReset);
		addOutputPin(out);
		name = "Internal Memory";
	}
	

	public CircuitObjectInputPin getInputPin(ElementFunction funct) {
		if (ElementFunction.MEMORYSET == funct)
			return inSet;
		if (ElementFunction.MEMORYRESET == funct)
			return inReset;
		return null;
	}
	
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		if (ElementFunction.MEMORY == funct)
			return out;
		return null;
	}

}
