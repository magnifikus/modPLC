package de.squig.plc.client.gui.controller;

import java.text.DecimalFormat;

import net.minecraft.client.gui.GuiButton;
import de.squig.plc.client.gui.controlls.TouchButton;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.LogicTimer;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;
import de.squig.plc.logic.objects.guiFunctions.GuiFunctionTime;

public class GuiFunctionTimeImpl extends GuiFunctionImpl {
	TouchButton btnDown1;
	TouchButton btnDown2;
	TouchButton btnDown3;
	TouchButton btnDown4;
	TouchButton btnUp1;
	TouchButton btnUp2;
	TouchButton btnUp3;
	TouchButton btnUp4;
	
	public GuiFunctionTimeImpl(GuiController gu, GuiInfoscreen gs,
			GuiFunction fnct, CircuitObject obj, int ypos) {
		super(gu, gs, fnct, obj, ypos);
		height = 20;
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
		long value = (Long)circuitObject.getObjData().get(guiFunction.getIdx());
		float val2 = value / 20f;
		DecimalFormat dec = new DecimalFormat("0.00");
		
	    guiController.getFontRenderer().drawString(dec.format(val2)+"s", guiController.infoX+55, ypos, 0x000000);
	    
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
	    
	    
	    
	    if (circuitObject instanceof LogicTimer) {
	    	circuitObject.getCircuit().setSimulationTime(guiController.controller.getWorldObj().getTotalWorldTime());
		    long intick =   circuitObject.getNextActivation() - guiController.controller.getWorldObj().getTotalWorldTime();
		    float intick2 = intick / 20f;
		    dec = new DecimalFormat("0.0");
		    guiController.getFontRenderer().drawString("next activation in "+dec.format(intick2)+"s", guiController.infoX+12, ypos+13, 0x000000);

	    }
	    
	    
	    
	    
	}
	
	@Override
	public void drawBackground() {
		
	}
	@Override
	public boolean actionPerformed (GuiButton btn) {
		if (controlls.contains(btn)) {
			long value = (Long)circuitObject.getObjData().get(guiFunction.getIdx());
			if (btn.equals(btnUp1)) {
				value += 1;
			} else if (btn.equals(btnUp2)) {
				value += 20;
			} else if (btn.equals(btnUp3)) {
				value += 200;
			} else if (btn.equals(btnUp4)) {
				value += 2000;
			} else if (btn.equals(btnDown1)) {
				value -= 1;
			} else if (btn.equals(btnDown2)) {
				value -= 20;
			} else if (btn.equals(btnDown3)) {
				value -= 200;
			} else if (btn.equals(btnDown4)) {
				value -= 2000;
			}
			GuiFunctionTime fnct = (GuiFunctionTime) guiFunction;
			if (value < fnct.getMin()) {
				value = fnct.getMin();
			}
			if (value > fnct.getMax())
				value = fnct.getMax();
			
			circuitObject.getObjData().set(guiFunction.getIdx(),(Long)value);
			if (circuitObject instanceof LogicTimer) {
				((LogicTimer) circuitObject).reset();
			}
			circuitObject.setChanged(true);
			guiController.sendUpdate(false);
			return true;
		}
		return false;
	}
}
