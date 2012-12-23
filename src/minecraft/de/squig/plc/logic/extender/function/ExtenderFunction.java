package de.squig.plc.logic.extender.function;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;

public abstract class ExtenderFunction {
	
	private String displayName;
	private List<ExtenderChannel.TYPES>  ioTypes;
	private List<ExtenderTrigger> triggers = null;

	private int id;
	
	
	private static List<ExtenderFunction> functions = null;
	
	
	protected ExtenderFunction(int id, String displayName, List<ExtenderChannel.TYPES> ioTypes ,List<ExtenderTrigger> triggers) {
		
		this.displayName = displayName;
		this.triggers = triggers;
		this.ioTypes = ioTypes;
		this.id = id;
	}
	
	public static ExtenderFunction disabledFunction = new DisabledFunction();
	public static List<ExtenderFunction> getAviavableFunction(ExtenderChannel channel) {
		 if (functions == null) {
			 functions = new ArrayList<ExtenderFunction>();
			 functions.add (disabledFunction);
			 functions.add (new RedstoneFunction());
			 //functions.add (new InventoryFunction());
			 functions.add (new BC3Function());
			 
		 }
		 List<ExtenderFunction> res = new ArrayList<ExtenderFunction>();
		 for (ExtenderFunction fncts : functions) {
			 if (fncts.ioTypes.contains(channel.getType()))
				 res.add(fncts);
		 }
		 return res;
	}
	
	public static ExtenderFunction getFunction(ExtenderChannel chn, int functionid) {
		if (chn == null && functionid == 0)
			return disabledFunction;
		for (ExtenderFunction fnct : getAviavableFunction(chn)) 
			if (fnct.getId() == functionid)
				return fnct;
		
		return null;
		
	}

	public void onDestroy(ExtenderChannel channel) {
	}
	public void onCreate(ExtenderChannel channel) {
	}
	public void onUpdate(ExtenderChannel channel,long worldTotalTime) {
	}
	
	
	
	

	public String getDisplayName() {
		return displayName;
	}
	
	public void onTriggerChanged() {
		
	}
	
	

	public int getId() {
		return id;
	}




	public List<ExtenderTrigger> getTriggers() {
		return triggers;
	}

	public String getTriggerValue() {
		// TODO Auto-generated method stub
		return "no value";
	}

	public void onSignal(ExtenderChannel channel, Signal signal) {
		
	}
	
}
