package de.squig.plc.logic.objects;

import de.squig.plc.logic.Signal;

public class CircuitObjectInputPin {
	protected CircuitObject circuitObject;
	protected String name;
	protected ICircuitObjectInputPinListener listener = null;
	protected Signal signal = Signal.OFF;
	
	public CircuitObjectInputPin (CircuitObject circuitObject, String name) {
		this.circuitObject = circuitObject;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setListener(ICircuitObjectInputPinListener listener) {
		this.listener = listener;
	}
	
	
	public void onSignal(Signal signal) {
		this.signal = signal;
		if (listener != null)
			listener.onSignal(this, this.signal);
		
	}
	
	public Signal getSignal() {
		return signal;
	}
	
	

}
