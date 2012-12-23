package de.squig.plc.logic.objects;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.ISignalListener;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;
import de.squig.plc.logic.objects.guiFunctions.GuiFunctionTime;

public class LogicDelay extends CircuitObject implements ICircuitObjectInputPinListener  {
	private enum dataMap {VALUE,SIGSET};
	
	public static List<Class> dataTypes = new ArrayList<Class>() {{
		add(Long.class); // value
		add(Long.class); // tickspassed
	}};
	public static List<Boolean> dataStatics = new ArrayList<Boolean>() {{
		add(true);
		add(false);
	}};
	private static List<GuiFunction> guiFunctions = new ArrayList<GuiFunction>() {{
		add(new GuiFunctionTime((short)dataMap.VALUE.ordinal(), "Stable Time",1, 32767));
	}};

	
	protected CircuitObjectOutputPin out = new CircuitObjectOutputPin(this, "Output");
	protected CircuitObjectInputPin in = new CircuitObjectInputPin(this, "Input");
	
	
	public LogicDelay(Circuit circuit, short linkNumber) {
		super(circuit,dataTypes,dataStatics);
		setGuiFunctions(guiFunctions);
		addInputPin(in);
		addOutputPin(out);
		in.setListener(this);
		setLinkNumber(linkNumber);
		setSigSet(0l);
		setValue(1l);
		name = "Delay";
		
	}
	
	@Override
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		return out;
	}
	@Override
	public CircuitObjectInputPin getInputPin(ElementFunction fucnt) {
		return in;
	}

	@Override
	public long getNextActivation() {
		long nextSim = -1;
		if (circuit.getSimulationTime()-getSigSet() < getValue()) {
				nextSim = getSigSet()+getValue();
			}
		return nextSim;
	}
	

	@Override
	public void onSignal(CircuitObjectInputPin pin, Signal signal) {
		boolean powered = signal.equals(Signal.ON);
		if (isOn() != powered) {
			setSigSet(circuit.getSimulationTime());
		}
		setOn(powered);
	}

	@Override
	public void preSimulation() {
		//LogHelper.info((circuit.getSimulationTime()-getSigSet())+ " passed "+getValue());
		if (circuit.getSimulationTime()-getSigSet() == getValue()) {
			//LogHelper.info("executing preSim");
			setWasOn(isOn());
			if (isWasOn())
				out.onSignal(Signal.ON);
			else out.onSignal(Signal.OFF);
		} else {
			//LogHelper.info("executing alternate "+isWasOn());
			if (isWasOn())
				out.onSignal(Signal.ON);
			else out.onSignal(Signal.OFF);
		}
		
	}
	
	
	
	@Override
	public void commit() {
		
	}
	
	
	public boolean isOn() {
		return (getFlags() & 8192) == 8192;
	}
	public boolean isWasOn() {
		return (getFlags() & 16384) == 16384;
	}	
	public void setWasOn(boolean on) {
		if (!on && isWasOn())
			setFlags((short)(getFlags()-16384));
		else if (on && !isWasOn())
			setFlags((short)(getFlags() | 16384));
	}
	public void setOn(boolean on) {
		if (!on && isOn())
			setFlags((short)(getFlags()-8192));
		else if (on && !isOn())
			setFlags((short)(getFlags() | 8192));
	}	
	
	
	public void setValue(Long value) {
		objData.set(dataMap.VALUE.ordinal(), value);
	}
	public long getValue() {
		return (Long)objData.get(dataMap.VALUE.ordinal());
	}
	public void setSigSet(long value) {
		objData.set(dataMap.SIGSET.ordinal(), value);
	}
	public long getSigSet() {
		return (Long)objData.get(dataMap.SIGSET.ordinal());
	}

	
	
}
