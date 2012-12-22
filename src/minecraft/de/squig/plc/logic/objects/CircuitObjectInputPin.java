package de.squig.plc.logic.objects;

import de.squig.plc.logic.Signal;
import de.squig.plc.logic.helper.LogHelper;

public class CircuitObjectInputPin {
	protected CircuitObject circuitObject;
	protected String name;
	protected ICircuitObjectInputPinListener listener = null;
	protected Signal signal = Signal.OFF;
	protected Signal lastSignal = Signal.OFF;
	
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
	
	public void commit() {
		if (listener != null ) { // && !lastSignal.equals(this.signal) ) {
			listener.onSignal(this, this.signal);
			if (signal.equals(Signal.PULSE))
					lastSignal = Signal.OFF;
			else if (signal.equals(Signal.NEGATIVEPULSE))
				lastSignal = Signal.ON;
			else lastSignal = this.signal;
		}
	}
	
	
	public void onSignal(Signal signal) {
		this.signal = signal;
	}
	
	public Signal getSignal() {
		return signal;
	}
	
	

}
