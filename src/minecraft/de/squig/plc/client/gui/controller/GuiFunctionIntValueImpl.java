package de.squig.plc.client.gui.controller;

import net.minecraft.client.gui.GuiButton;
import de.squig.plc.client.gui.controlls.TouchButton;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;
import de.squig.plc.logic.objects.guiFunctions.GuiFunctionIntValue;

public class GuiFunctionIntValueImpl extends GuiFunctionImpl {
	TouchButton btnDown1;
	TouchButton btnDown2;
	TouchButton btnDown3;
	TouchButton btnDown4;
	TouchButton btnUp1;
	TouchButton btnUp2;
	TouchButton btnUp3;
	TouchButton btnUp4;
	
	public GuiFunctionIntValueImpl(GuiController gu, GuiInfoscreen gs,
			GuiFunction fnct, CircuitObject obj, int ypos) {
		super(gu, gs, fnct, obj, ypos);
		height = 12;
		btnDown1 = new TouchButton(0, gu.infoX +  42, ypos, TouchButton.TYPES.LEFT);
		btnDown2 = new TouchButton(0, gu.infoX +  32, ypos, TouchButton.TYPES.LEFT);
		btnDown3 = new TouchButton(0, gu.infoX +  22, ypos, TouchButton.TYPES.LEFT);
		btnDown4 = new TouchButton(0, gu.infoX +  12, ypos, TouchButton.TYPES.LEFT);
		
		btnUp1 = new TouchButton(0, gu.infoX +  110, ypos, TouchButton.TYPES.RIGHT);
		btnUp2 = new TouchButton(0, gu.infoX +  120, ypos, TouchButton.TYPES.RIGHT);
		btnUp3 = new TouchButton(0, gu.infoX +  130, ypos, TouchButton.TYPES.RIGHT);
		btnUp4 = new TouchButton(0, gu.infoX +  140, ypos, TouchButton.TYPES.RIGHT);
		
		controlls.add(btnDown1);
		controlls.add(btnDown2);
		controlls.add(btnDown3);
		controlls.add(btnDown4);
		controlls.add(btnUp1);
		controlls.add(btnUp2);
		controlls.add(btnUp3);
		controlls.add(btnUp4);
		
		
		super.addControlls();
	}

	@Override
	public void drawForeground(int i, int j) {
		long value = (Short)circuitObject.getObjData().get(guiFunction.getIdx());
		guiController.getFontRenderer().drawString(value+"", guiController.infoX+55, ypos, 0x000000);
	    
	    
	    if (i >= guiController.infoX+55 && i < guiController.infoX+105)
        	if (j >= ypos && j < ypos+10) {
        		int width = guiController.getFontRenderer().getStringWidth(guiFunction.getName());
        		int xx = this.guiController.infoX+55 +3;
        		int yy = this.ypos -12;
        		int xcorr = 0;
        		if (xx+width > 450)
        			xx = xx+(450-(xx+width));
        		guiController.drawGradientRect(xx-2+xcorr,yy-2, xx+width+2+xcorr, yy+8+2, 0xc0000000, 0xf0000000);
        		guiController.getFontRenderer().drawString(guiFunction.getName(), xx+xcorr,yy, 0xffffff);
        	}
	}
	
	@Override
	public void drawBackground() {
		
	}
	@Override
	public boolean actionPerformed (GuiButton btn) {
		if (controlls.contains(btn)) {
			short value = (Short)circuitObject.getObjData().get(guiFunction.getIdx());
			if (btn.equals(btnUp1)) {
				value += 1;
			} else if (btn.equals(btnUp2)) {
				value += 10;
			} else if (btn.equals(btnUp3)) {
				value += 100;
			} else if (btn.equals(btnUp4)) {
				value += 1000;
			} else if (btn.equals(btnDown1)) {
				value -= 1;
			} else if (btn.equals(btnDown2)) {
				value -= 10;
			} else if (btn.equals(btnDown3)) {
				value -= 100;
			} else if (btn.equals(btnDown4)) {
				value -= 1000;
			}
		
			if (value > ((GuiFunctionIntValue)guiFunction).getMax())
				value = (short)((GuiFunctionIntValue)guiFunction).getMax();
			if (value < ((GuiFunctionIntValue)guiFunction).getMin())
				value = (short)((GuiFunctionIntValue)guiFunction).getMin();
			
			circuitObject.getObjData().set(guiFunction.getIdx(),(Short)value);
			circuitObject.setChanged(true);
			guiController.sendUpdate(false);
			return true;
		}
		return false;
	}
	
}
