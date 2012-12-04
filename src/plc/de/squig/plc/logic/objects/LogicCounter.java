package de.squig.plc.logic.objects;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class LogicCounter extends CircuitObject {
	protected CircuitObjectOutputPin outTop = new CircuitObjectOutputPin(this, "Maximum reached");
	protected CircuitObjectOutputPin outBottom = new CircuitObjectOutputPin(this, "Minumum reached");
	protected CircuitObjectInputPin inCountUp = new CircuitObjectInputPin(this, "Count up");
	protected CircuitObjectInputPin inCountDown = new CircuitObjectInputPin(this, "Count down");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Reset Counter");
	protected CircuitObjectInputPin inStop = new CircuitObjectInputPin(this, "Stop Counting");
	
	public LogicCounter(Circuit circuit, String linkNumber) {
		super(circuit, TYPES.COUNTER);
		addOutputPin(outTop);
		addOutputPin(outBottom);
		addInputPin(inCountUp);
		addInputPin(inCountDown);
		addInputPin(inReset);
		addInputPin(inStop);
			
		setLinkNumber(linkNumber);
		name = "Internal Counter";
	}
	
	
	public CircuitObjectInputPin getInputPin(ElementFunction funct) {
		if (ElementFunction.COUNTERCOUNTUP == funct)
			return inCountUp;
		if (ElementFunction.COUNTERCOUNTDOWN == funct)
			return inCountDown;
		if (ElementFunction.COUNTERRESET == funct)
			return inReset;
		if (ElementFunction.COUNTERSTOP == funct)
			return inStop;
		return null;
	}
	
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		if (ElementFunction.COUNTERTOP == funct)
			return outTop;
		if (ElementFunction.COUNTERBOTTOM == funct)
			return outBottom;	
		return null;
	}
	
}
