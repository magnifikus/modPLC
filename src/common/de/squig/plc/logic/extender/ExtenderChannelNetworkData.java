package de.squig.plc.logic.extender;

import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.extender.function.ExtenderFunction;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.tile.TileExtender;

public class ExtenderChannelNetworkData {
	private char number;
	private char linkNumber;
	private char type;
	private char side;

	private char functionType;
	private char triggerType;
	private String functionData;
	private String triggerData;
	public ExtenderChannelNetworkData(char number, char linkNumber, char type,
			 char side,
			char functionType, char triggerType, String functionData, String triggerData) {
		super();
		this.number = number;
		this.linkNumber = linkNumber;
		this.type = type;
	
		this.side = side;
	
		this.functionType = functionType;
		this.triggerType = triggerType;
		this.functionData = functionData;
		this.triggerData = triggerData;
	}
	
	public char getLinkNumber() {
		return linkNumber;
	}

	public char getNumber() {
		return number;
	}
	public char getType() {
		return type;
	}

	public char getFunctionType() {
		return functionType;
	}
	

	public char getTriggerType() {
		return triggerType;
	}

	public char getSide() {
		return side;
	}

	public String getFunctionData() {
		return functionData;
	}

	public String getTriggerData() {
		return triggerData;
	}
	
	
}
