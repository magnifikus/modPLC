package de.squig.plc.client.gui.controller;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.CircuitElement;

public class DisplayTile {
	private int gridx;
	private int gridy;
	private int x;
	private int y;
	private int width;
	private int height;
	
	
	public DisplayTile(int gridx, int gridy,int x, int y, int width, int height) {
		this.gridx = gridx;
		this.gridy = gridy;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean checkClicked(int cx, int cy) {
		if (cx < x || cy < y)
			return false;
		if (cx <= x+width && cy <= y+height)
			return true;
		return false;
	}

	public int getGridx() {
		return gridx;
	}

	public int getGridy() {
		return gridy;
	}
	
	
}
