package de.squig.plc.logic.objects;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.ISignalListener;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.helper.LogHelper;

public class LogicInput extends CircuitObject {
	protected CircuitObjectOutputPin out;
	
	public LogicInput(Circuit circuit, String linkNumber) {
		super(circuit, TYPES.INPUT);
		out = new CircuitObjectOutputPin(this,"Input");
		addOutputPin(out);
		setLinkNumber(linkNumber);
		name = "Internal Input";
		
	}
	
	@Override
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		return out;
	}
	
	
	
	
	
	public void onSignal(Signal signal) {
		out.onSignal(signal);
	}
	
	
	
}
