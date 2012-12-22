package de.squig.plc.logic.objects.guiFunctions;

import de.squig.plc.client.gui.controller.GuiFunctionImpl;

public class GuiFunctionTime extends GuiFunction {

	private long min;
	private long max;
	
	public GuiFunctionTime(short idx, String name, long min, long max) {
		super(idx, name);
		this.min = min;
		this.max = max;
	}

	
	
	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}
	
	
}
