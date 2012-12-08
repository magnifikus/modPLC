package de.squig.plc.logic.extender.function;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.extender.ExtenderChannel;

public class InventoryFunction extends ExtenderFunction {
	private static ExtenderTrigger defaultTrigger = new ExtenderTrigger("is empty",false,0, ExtenderChannel.TYPES.INPUT);
	private static List<ExtenderTrigger> myTriggers = new ArrayList<ExtenderTrigger>() {{
		add(defaultTrigger);
		add(new ExtenderTrigger("is full",false,1, ExtenderChannel.TYPES.INPUT));
		add(new ExtenderTrigger("has space",false,2, ExtenderChannel.TYPES.INPUT));
		add(new ExtenderTrigger("has items", true,3, ExtenderChannel.TYPES.INPUT));
	}};
	public static List<ExtenderChannel.TYPES> myIoTypes = new ArrayList<ExtenderChannel.TYPES>() {
		{
			add(ExtenderChannel.TYPES.INPUT);
		}
	};
	public InventoryFunction() {
		super(2, "INV",myIoTypes, myTriggers);
	}
	

	public void onCreate(ExtenderChannel channel) {
		channel.setTrigger(defaultTrigger);
	}


}
