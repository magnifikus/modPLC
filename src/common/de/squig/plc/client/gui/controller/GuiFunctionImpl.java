package de.squig.plc.client.gui.controller;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.GuiButton;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;

public class GuiFunctionImpl {
	protected List<GuiButton> controlls = new LinkedList<GuiButton>();
	protected GuiController guiController;
	protected GuiInfoscreen guiInfoscreen;
	protected GuiFunction guiFunction;
	protected CircuitObject circuitObject;
	protected int ypos;
	protected int height = 16;
	
	public GuiFunctionImpl (GuiController gu, GuiInfoscreen gs, GuiFunction fnct, CircuitObject obj, int ypos) {
		this.guiController = gu;
		this.guiInfoscreen = gs;
		this.guiFunction = fnct;
		this.circuitObject = obj;
		this.ypos = ypos;
	}
	
	public void onClose() {
		guiController.getControllList().removeAll(controlls);
	}
	protected void addControlls () {
		guiController.getControllList().addAll(controlls);
	}

	public int getHeight() {
		return height;
	}
	
	public void drawForeground(int i, int j) {
		
	}
	public void drawBackground() {
		
	}
	public void actionPerformed (GuiButton btn) {
		
	}
	
}
