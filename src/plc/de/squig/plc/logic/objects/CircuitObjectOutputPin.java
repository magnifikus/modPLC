package de.squig.plc.logic.objects;

import de.squig.plc.logic.Signal;
import de.squig.plc.logic.helper.LogHelper;

public class CircuitObjectOutputPin {
	protected CircuitObject circuitObject;
	protected String name;
	protected Signal signal = Signal.OFF;

	public CircuitObjectOutputPin(CircuitObject circuitObject, String name) {
		this.circuitObject = circuitObject;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	
	public void afterCycle() {
		
	}

	public void onSignal(Signal signal) {
		//LogHelper.info("onSignal "+circuitObject.getLinkNumber()+" on circ "+circuitObject.getCircuit());
		this.signal = signal;
	}

	public Signal getSignal() {
		//LogHelper.info("getSignal "+circuitObject.getLinkNumber()+" on circ "+circuitObject.getCircuit());
		return signal;
	}
 
}
