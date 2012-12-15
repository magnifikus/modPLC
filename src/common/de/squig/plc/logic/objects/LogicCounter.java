package de.squig.plc.logic.objects;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public class LogicCounter extends CircuitObject {
	public static List<Class> dataTypes = new ArrayList<Class>() {{
			add(Short.class); // value
			add(Short.class); // low
			add(Short.class); // mid
			add(Short.class); // down
			add(Short.class); // incV
			add(Short.class); // decV
	}};
	
	protected CircuitObjectOutputPin outTop = new CircuitObjectOutputPin(this, "Maximum reached");
	protected CircuitObjectOutputPin outBottom = new CircuitObjectOutputPin(this, "Minumum reached");
	protected CircuitObjectOutputPin outMid = new CircuitObjectOutputPin(this, "Median reached");
	protected CircuitObjectInputPin inCountUp = new CircuitObjectInputPin(this, "Count up");
	protected CircuitObjectInputPin inCountDown = new CircuitObjectInputPin(this, "Count down");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Reset Counter");
	protected CircuitObjectInputPin inStop = new CircuitObjectInputPin(this, "Stop Counting");
	
	public LogicCounter(Circuit circuit, short linkNumber) {
		super(circuit,dataTypes);
		addOutputPin(outTop);
		addOutputPin(outMid);
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
