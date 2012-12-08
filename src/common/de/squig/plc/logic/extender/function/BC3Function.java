package de.squig.plc.logic.extender.function;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

import de.squig.plc.PLC;
import de.squig.plc.event.SignalEvent;
import de.squig.plc.logic.ISignalListener;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.ExtenderChannel.TYPES;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.LogicInput;
import de.squig.plc.logic.objects.LogicOutput;
import de.squig.plc.tile.TileExtender;

public class BC3Function extends ExtenderFunction {
	private static ExtenderTrigger defaultTrigger = new ExtenderTrigger(
			"direct to/from BC3 Gate", false, 0, null);
	private static List<ExtenderTrigger> myTriggers = new ArrayList<ExtenderTrigger>() {
		{
			add(defaultTrigger);
			//add(new ExtenderTrigger("inverted to/from BC3 Gate", false, 1, null));
			
			

		}
	};
	public static List<ExtenderChannel.TYPES> myIoTypes = new ArrayList<ExtenderChannel.TYPES>() {
		{
			add(ExtenderChannel.TYPES.INPUT);
			add(ExtenderChannel.TYPES.OUTPUT);
		}
	};

	public BC3Function() {
		super(3, "BC", myIoTypes, myTriggers);
	}

	@Override
	public void onDestroy(ExtenderChannel channel) {
	
	}

	@Override
	public void onCreate(ExtenderChannel channel) {
		channel.setTrigger(defaultTrigger);
		channel.setFunctionLocalData(Signal.OFF);
		channel.setFunctionLocalData(new Integer(-1));
	
	}

	public void onBCAction(ExtenderChannel channel) {
		int lastReset = -1;

		if (channel.getFunctionLocalData() instanceof Integer) {
			lastReset = (Integer) channel.getFunctionLocalData();
		}
		if (channel.getSignal().equals(Signal.OFF)) {
			SignalEvent event = new SignalEvent(channel.getExtender(), channel
					.getExtender().getConnectedController(), Signal.ON,
					channel.getLinkedChannel());
			PLC.instance.fireEvent(event);
			channel.getExtender().setSheduleRemoteUpdate(true);
		}
		channel.setSignal(Signal.ON);
		channel.setFunctionLocalData(new Integer(15));	
		
	}

	public void onUpdate(ExtenderChannel channel) {
		if (channel.getType().equals(ExtenderChannel.TYPES.INPUT)) {
			if (channel.getFunctionLocalData() instanceof Integer) {
				int lastReset = (Integer) channel.getFunctionLocalData();
				if (lastReset >= 0)
					lastReset--;
				if (lastReset == 0 ) {
					SignalEvent event = new SignalEvent(channel.getExtender(),
							channel.getExtender().getConnectedController(),
							Signal.OFF, channel.getNumber());
					PLC.instance.fireEvent(event);
					channel.setSignal(Signal.OFF);
					channel.setFunctionLocalData(new Integer(-1));
					channel.getExtender().setSheduleRemoteUpdate(true);
				}
				
				if (lastReset >= 0) {
					channel.setFunctionLocalData(new Integer(lastReset));
				}
			}
		} 
	}

	public void onSignal(ExtenderChannel channel, Signal signal) {
		if (channel.getType() == ExtenderChannel.TYPES.OUTPUT) {
			if (!channel.getSignal().equals(signal)) {
				channel.setSignal(signal);
				channel.getExtender().setSheduleRemoteUpdate(true);
			}
		}
	}

}
