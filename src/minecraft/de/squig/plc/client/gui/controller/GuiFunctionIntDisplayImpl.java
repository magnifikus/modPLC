package de.squig.plc.client.gui.controller;

import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;

public class GuiFunctionIntDisplayImpl extends GuiFunctionImpl {
	
	
	public GuiFunctionIntDisplayImpl(GuiController gu, GuiInfoscreen gs,
			GuiFunction fnct, CircuitObject obj, int ypos) {
		super(gu, gs, fnct, obj, ypos);
		height = 12;
		
		
	}

	@Override
	public void drawForeground(int i, int j) {
		long value = (Short)circuitObject.getObjData().get(guiFunction.getIdx());
		guiController.getFontRenderer().drawString(guiFunction.getName()+": "+value, guiController.infoX+10, ypos, 0x000000);
	}
	
	@Override
	public void drawBackground() {
		
	}
	
}
