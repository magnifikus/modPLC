package de.squig.plc.logic;

import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.LogicCounter;
import de.squig.plc.logic.objects.LogicInput;
import de.squig.plc.logic.objects.LogicMemory;
import de.squig.plc.logic.objects.LogicOutput;
import de.squig.plc.logic.objects.LogicTimer;
import de.squig.plc.tile.TileController;

public class BasicCircuit extends Circuit {
	
	public BasicCircuit(TileController controller) {
		super(controller,8,20);
	
		
		for (int i=0; i < 16; i++) {
			addCircuitObject(new LogicInput(this, i+""));
		}
		for (int i=0; i < 8; i++) {
			addCircuitObject(new LogicOutput(this, i+""));
		}
		for (int i=0; i < 16; i++) {
			addCircuitObject(new LogicMemory(this, i+""));
		}
		for (int i=0; i < 8; i++) {
			addCircuitObject(new LogicTimer(this, i+""));
		}
		for (int i=0; i < 8; i++) {
			addCircuitObject(new LogicCounter(this, i+""));
		}

		
		//Input input1 = new Input(this, 0, 0);
		//map.addElement(input1, -1, -1);
		
		
		
		
	}

	
	
	
	
}
