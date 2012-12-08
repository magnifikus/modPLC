package de.squig.plc.logic.objects;

import de.squig.plc.logic.Signal;

public interface ICircuitObjectInputPinListener {
	public void onSignal(CircuitObjectInputPin pin, Signal signal);
}	
