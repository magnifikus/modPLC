package de.squig.plc.logic.objects;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.PLC;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.SignalEvent;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.ISignalListener;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.helper.LogHelper;

public class LogicOutput extends CircuitObject implements ICircuitObjectInputPinListener {
	protected CircuitObjectOutputPin out = new CircuitObjectOutputPin(this, "Output");
	protected CircuitObjectInputPin in = new CircuitObjectInputPin(this, "Output");
	protected CircuitObjectInputPin inSet = new CircuitObjectInputPin(this, "Output Set");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Output Reset");
	 
	
	
	protected Signal signal = Signal.OFF;
	
	public LogicOutput(Circuit circuit, String linkNumber) {
		super(circuit, TYPES.OUTPUT);
		setLinkNumber(linkNumber);
		addInputPin(in);
		addInputPin(inSet);
		addInputPin(inReset);
		addOutputPin(out);
		in.setListener(this);
		inSet.setListener(this);
		inReset.setListener(this);
		name = "Internal Output";
	}
	

	public CircuitObjectInputPin getInputPin(ElementFunction funct) {
		if (ElementFunction.OUTPUTSET == funct)
			return inSet;
		if (ElementFunction.OUTPUTRESET == funct)
			return inReset;
		if (ElementFunction.OUTPUT == funct)
			return in;
		return null;
	}
	
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		if (ElementFunction.OUTPUTREAD == funct)
			return out;
		return null;
	}
	
	public void updateRemotes(Signal signal) {
		//LogHelper.info("got signal: "+signal);
		SignalEvent event = new SignalEvent(getCircuit().getController(),PLCEvent.TARGETTYPE.EXTENDER,
				signal,this.getLinkNumberInt(), circuit.getController().getRange());
		PLC.instance.fireEvent(event);
	}
	
	
	public boolean getPowered () {
		return true;
	}


	@Override
	public void onSignal(CircuitObjectInputPin pin, Signal signal) {
		if (pin == in) {
			this.signal = signal;
			
		} else if (pin == inSet && (signal.equals(Signal.ON) || signal.equals(Signal.PULSE))) {
			this.signal = Signal.ON;
			
		} else if (pin == inReset && (signal.equals(Signal.ON) || signal.equals(Signal.PULSE))) {
			this.signal = Signal.OFF;
			
		}
		out.onSignal(this.signal);
	}
	
	@Override
	public void commit() {
		updateRemotes(this.signal);
	}
	
}
