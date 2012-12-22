package de.squig.plc.logic.extender.function;

import de.squig.plc.logic.extender.ExtenderChannel;

public class ExtenderTrigger {
	private String name;
	private boolean hasValue;
	private int triggerId;
	ExtenderChannel.TYPES channelType;
	public ExtenderTrigger(String name, boolean hasValue, int triggerid, ExtenderChannel.TYPES channelType) {
		this.name = name;
		this.hasValue = hasValue;
		this.triggerId = triggerid;
		this.channelType = channelType;
	}
	public String getName() {
		return name;
	}
	public boolean hasValue() {
		return hasValue;
	}
	public int getTriggerId() {
		return triggerId;
	}
	public ExtenderChannel.TYPES getChannelType() {
		return channelType;
	}
	
	
}
