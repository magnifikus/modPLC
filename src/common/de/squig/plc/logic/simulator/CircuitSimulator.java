package de.squig.plc.logic.simulator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.Deleted;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.CircuitObjectInputPin;
import de.squig.plc.network.PacketControllerData;

public class CircuitSimulator {
	private long nextTick = 0;
	private short runEvery = 10;
	private Circuit circuit;

	public CircuitSimulator(Circuit circuit) {
		this.circuit = circuit;
	}

	public synchronized void onTick(long tick) {
		long start = System.nanoTime();
		//if (nextTick > tick)
		//	return;
		//nextTick = tick + 1;//StaticData.SimulatorDelay;
		
		if (!circuit.isNeedsSimulation() && circuit.getNeedsSimInTicks() >= 0)
			return;
		circuit.setNeedsSimulation(false);
		long siminTicks = circuit.getNeedsSimInTicks();
		if (siminTicks >= 0)
			circuit.setNeedsSimInTicks(siminTicks);
		
		
		
		if (!circuit.isEvaluated())
			evaluateCircuit();
		simulateCircuit();
		PacketControllerData.updateArroundWithPowermap(circuit.getController(), 8);
		long took = System.nanoTime()-start;
		//LogHelper.info("took "+took+" nanos");
	}
	
	
	private void evaluateCircuit () {
		List<CircuitElement> simObjects = new ArrayList<CircuitElement>();
		List<Object> comObjects = new ArrayList<Object>();
		for (int x=0; x < circuit.getMap().getWidth(); x++)
			for (int y=0; y < circuit.getMap().getHeight(); y++) {
				CircuitElement ele = circuit.getMap().getElementAt(x, y);
				if (ele != null && !(ele instanceof Deleted)) {
					simObjects.add(ele);
					if (ele.getInputPin() != null)
						comObjects.add(ele.getInputPin());
				}
			}
		circuit.setSimulationList(simObjects);
		circuit.setCommitList(comObjects);
		circuit.setEvaluated(true);
	}
	private void simulateCircuit () {
		for (CircuitElement ele : circuit.getSimulationList()) {
			ele.setSimulated(false);
		}
		
		for (CircuitElement ele : circuit.getSimulationList()) {
			// lets check for left Signal
			CircuitElement eleLeft = circuit.getMap().getElementAt(ele.getMapX()-1, ele.getMapY());
			if (eleLeft != null) {
				if (eleLeft instanceof Line) {
					if (((Line) eleLeft).isConnRight()) {
						if (ele instanceof Line) {
							if (((Line) ele).isConnLeft()) {
								 ele.setInSignal(eleLeft.getSignal());
							}
						} else {
							ele.setInSignal(eleLeft.getSignal());
							//LogHelper.info("setting in from left line "+eleLeft.getSignal());
						}
						
					} 
				} else {
					
					ele.setInSignal(eleLeft.getSignal());
					//LogHelper.info("setting in from left line "+eleLeft.getSignal());
				}
			} else {
				if (!(ele instanceof Line)) {
					if (ele.getInputPin() == null)
						ele.setInSignal(Signal.ON);
				}
			} 
			//if (!ele.isSimulated())
			ele.simulate();
			
		}
		for (Object com : circuit.getCommitList()) {
			if (com instanceof CircuitObjectInputPin)
				((CircuitObjectInputPin) com).commit();
		}
		
		for (CircuitObject obj : circuit.getObjects())
			obj.commit();
		
		circuit.getController().sendUpdatesToExtenders(false);
		
	}
	
}
