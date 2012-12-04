package de.squig.plc.logic.simulator;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketControllerData;

public class CircuitSimulator {
	private long nextTick = 0;
	private short runEvery = 10;
	private Circuit circuit;

	public CircuitSimulator(Circuit circuit) {
		this.circuit = circuit;
	}

	public synchronized void onTick(long tick) {
		if (nextTick > tick)
			return;
		nextTick = tick + 5;
	
		for (CircuitElement element : circuit.getMap().getAllElement())
			element.resetEvaluated();

		for (int y = 0; y < circuit.getMap().getHeight(); y++) {
			for (int x = 0; x < circuit.getMap().getWidth(); x++) {
				CircuitElement ele = circuit.getMap().getElementAt(x, y);
				if (ele != null) {
					if (ele.getInputPin() != null) {
						
						List<CircuitElement> deps = null;
						CircuitElement eleIn = circuit.getMap().getElementAt(
								x - 1, y);
						if (eleIn instanceof Line) {
							deps = ((Line) eleIn)
									.getConnectedInputs();
						} else {
							if (eleIn != null) {
								deps = new ArrayList<CircuitElement>();
								deps.add(eleIn);
							} else
								deps = null;
						}
						Signal signal = null;
						if (deps != null) {
							for (CircuitElement dep : deps) {
								signal = dep.getSignal();
								if (signal == Signal.ON)
									break;
							}
						}
						if (signal != null)
							ele.getInputPin().onSignal(signal);
						ele.evaluate();
						//LogHelper.info("result on "+signal.toString()+" on "+ele.toString());
						
					}
				}
			}

		}
		PacketControllerData.updateArroundWithPowermap(circuit.getController(), 8);
	}
}
