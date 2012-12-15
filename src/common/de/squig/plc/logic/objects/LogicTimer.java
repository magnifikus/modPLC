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
import de.squig.plc.logic.elements.functions.ElementFunction;

public class LogicTimer extends CircuitObject {
	public static List<Class> dataTypes = new ArrayList<Class>() {{
		add(Long.class); // timeBase
		add(Boolean.class); // pause
		add(Long.class); // pauseTime
	}};
	
	
	protected CircuitObjectOutputPin out = new CircuitObjectOutputPin(this, "Timer pulse");
	protected CircuitObjectInputPin inStop = new CircuitObjectInputPin(this, "Stop Timer");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this, "Reset Timer");
	
	protected long timeBase = -1;
	protected long timePause = -1;
	protected boolean paused = false;
	
	public LogicTimer(Circuit circuit, short linkNumber) {
		super(circuit,dataTypes);
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
	
	

	public long getNextActivation(long now) {
		return -1;
	}
	
	public void pause() {
		
	}
	public void resume() {
		
	}
	
	public void reset() {
		
	}
	
	
	public void saveTo() {
		
		
	}
	
	public void deserialize(byte[] data) {
		
	}

}
