package de.squig.plc.logic.objects.guiFunctions;

public class GuiFunctionTime extends GuiFunction {
	
	private long min;
	private long max;
	
	public GuiFunctionTime(short idx, long min, long max) {
		super(idx);
		
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
