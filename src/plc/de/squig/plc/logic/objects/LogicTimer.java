package de.squig.plc.logic.objects;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class LogicTimer extends CircuitObject {
	protected CircuitObjectOutputPin out = new CircuitObjectOutputPin(this, "Timer pulse");
	protected CircuitObjectInputPin inStop = new CircuitObjectInputPin(this, "Stop Timer");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Reset Timer");
	
	
	public LogicTimer(Circuit circuit, String linkNumber) {
		super(circuit, TYPES.TIMER);
		addOutputPin(out);
		addInputPin(inStop);
		addInputPin(inReset);
		
		setLinkNumber(linkNumber);
		name = "Internal Timer";
	}
	

	public CircuitObjectInputPin getInputPin(ElementFunction funct) {
		if (ElementFunction.TIMERRESET == funct)
			return inReset;
		if (ElementFunction.TIMERSTOP == funct)
			return inStop;
		return null;
	}
	
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		if (ElementFunction.TIMEROUTPUT == funct)
			return out;
		return null;
	}
	
	
	

}
