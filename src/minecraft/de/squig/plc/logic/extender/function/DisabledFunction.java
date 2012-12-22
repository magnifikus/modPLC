package de.squig.plc.logic.extender.function;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.extender.ExtenderChannel;

public class DisabledFunction extends ExtenderFunction {
	public static List<ExtenderChannel.TYPES> myIoTypes = new ArrayList<ExtenderChannel.TYPES>() {
		{
			add(ExtenderChannel.TYPES.INPUT);
			add(ExtenderChannel.TYPES.OUTPUT);
		}
	};
	public DisabledFunction() {
		super(0, "OFF",myIoTypes,null);
	}
	
}
