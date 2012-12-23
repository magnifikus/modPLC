package de.squig.plc.logic.objects;
/**
 * 
 * timeBase - starttime to be used
 * timeTick - length of a tick
 * 
 * long getNextActivated(long now) -  to get next event 
 * boolean paused - no next, on deactivation timeBase needs shift
 * long pauseStart - to restore timer after pause
 * 
 * reset()
 * pause(boolean pause)
 * 
 * 
 * 
 * @author Magnifikus
 */


import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;
import de.squig.plc.logic.objects.guiFunctions.GuiFunctionTime;

public class LogicTimer extends CircuitObject implements ICircuitObjectInputPinListener {
	public static List<Class> dataTypes = new ArrayList<Class>() {{
		add(Long.class); // timeBase
		add(Boolean.class); // pause
		add(Long.class); // pauseTime
		add(Long.class); // duration
	}};
	public static List<Boolean> dataStatics = new ArrayList<Boolean>() {{
		add(false);
		add(false);
		add(false);
		add(true);
	}};
	
	
	private static List<GuiFunction> guiFunctions = new ArrayList<GuiFunction>() {{
		add(new GuiFunctionTime((short)dataMap.TIME_DURATION.ordinal(), "Interval",2, 32767));
	}};

	private enum dataMap {TIME_BASE, PAUSE, TIME_PAUSE, TIME_DURATION};
	
	private long nextActionCache = -1;
	
	
	protected CircuitObjectOutputPin out = new CircuitObjectOutputPin(this, "Timer pulse");
	protected CircuitObjectInputPin inStop = new CircuitObjectInputPin(this, "Stop Timer");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Reset Timer");
	

	public LogicTimer(Circuit circuit, short linkNumber) {
		super(circuit,dataTypes,dataStatics);
		addOutputPin(out);
		addInputPin(inStop);
		addInputPin(inReset);
		setGuiFunctions(guiFunctions);
		setLinkNumber(linkNumber);
		inStop.setListener(this);
		inReset.setListener(this);
		name = "Internal Timer";
		setPause(false);
		setTimeBase(circuit.getSimulationTime());
		setTimeDuration(200);
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
	
	
	@Override
	public long getNextActivation() {
		if (isPause())
			return -1;
		if (nextActionCache > circuit.getSimulationTime() &&  !this.isChanged())
			return nextActionCache;
		
		long now = circuit.getSimulationTime();
		long base = getTimeBase();
		long duration = getTimeDuration();
		nextActionCache = base + (((now - base) / duration)+1)*duration;
		
		return nextActionCache;
	}
	
	
	@Override
	public void preSimulation() {
		if (isPause() || inReset.getSignal().equals(Signal.ON)) {
			out.onSignal(Signal.OFF);
			return;
		}
		if (nextActionCache  == circuit.getSimulationTime())
			out.onSignal(Signal.PULSE);
		else out.onSignal(Signal.OFF);
	}
	
	
	
	

	@Override
	public void onSignal(CircuitObjectInputPin pin, Signal signal) {
		if (pin.equals(inStop)) {
			if (signal.equals(Signal.ON))
				pause();
			else if (signal.equals(Signal.OFF))
				resume();
		} else if (pin.equals(inReset)) {
			if (signal.equals(Signal.ON) || signal.equals(Signal.PULSE))
				reset();
		}
		
	}
	
	
	public void pause() {
		if (!isPause()) {
			setPause(true);
			setTimePause(circuit.getSimulationTime());
		}	
	}
	
	public void resume() {
		if (isPause()) {
			setPause(false);
			setTimeBase( getTimeBase()+ circuit.getSimulationTime()-getTimePause());		
		}
	}
	
	public void reset() {
		setTimeBase(circuit.getSimulationTime());
		nextActionCache = -1;
	}
	
	
	public long getTimeBase() {
		return (Long)objData.get(dataMap.TIME_BASE.ordinal());
	}
	public boolean isPause() {
		return (Boolean)objData.get(dataMap.PAUSE.ordinal());
	}
	public long getTimePause() {
		return (Long)objData.get(dataMap.TIME_PAUSE.ordinal());
	}
	public long getTimeDuration() {
		return (Long)objData.get(dataMap.TIME_DURATION.ordinal());
	}
	
	public void setTimeBase(long timeBase) {
		objData.set(dataMap.TIME_BASE.ordinal(),timeBase);
	}
	public void setPause(boolean pause) {
		objData.set(dataMap.PAUSE.ordinal(),pause);
	}
	public void setTimePause(long timePause) {
		objData.set(dataMap.TIME_PAUSE.ordinal(),timePause);
	}
	public void setTimeDuration(long timeDuration) {
		objData.set(dataMap.TIME_DURATION.ordinal(),timeDuration);
	}


}
