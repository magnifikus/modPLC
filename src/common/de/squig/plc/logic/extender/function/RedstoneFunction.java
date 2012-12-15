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

public class RedstoneFunction extends ExtenderFunction {
	private static ExtenderTrigger defaultTrigger = new ExtenderTrigger("direct signal", false, 0, null);
	private static List<ExtenderTrigger> myTriggers = new ArrayList<ExtenderTrigger>() {
		{
			add(defaultTrigger);
			add(new ExtenderTrigger("inverted input", false, 1, ExtenderChannel.TYPES.INPUT));
			add(new ExtenderTrigger("pulse on raising", false, 2, ExtenderChannel.TYPES.INPUT));
			add(new ExtenderTrigger("pulse on falling", false, 3, ExtenderChannel.TYPES.INPUT));
			add(new ExtenderTrigger("pulse on change", false, 4, ExtenderChannel.TYPES.INPUT));
			//add(new ExtenderTrigger("stable input", false, 5, ExtenderChannel.TYPES.INPUT));

		}
	};
	public static List<ExtenderChannel.TYPES> myIoTypes = new ArrayList<ExtenderChannel.TYPES>() {
		{
			add(ExtenderChannel.TYPES.INPUT);
			add(ExtenderChannel.TYPES.OUTPUT);
		}
	};

	public RedstoneFunction() {
		super(1, "RS",myIoTypes, myTriggers);
	}


	
	@Override
	public void onDestroy(ExtenderChannel channel) {
		channel.getExtender().sheduleNeighbourUpdate();
	}
	@Override
	public void onCreate(ExtenderChannel channel) {
		channel.setTrigger(defaultTrigger);
		channel.setFunctionLocalData(Signal.OFF);
		if (channel.getType().equals(ExtenderChannel.TYPES.OUTPUT))
			channel.getExtender().requestUpdateFromController();
		
	}



	
	public void onRedstoneChanged(ExtenderChannel channel, boolean isRedstonePowered, boolean statePulse) {
		//LogHelper.info("on change");
		if (channel.getType() == ExtenderChannel.TYPES.INPUT) {
			Signal tosend = null;
			boolean lastpowered = false;
			if (channel.getFunctionLocalData() instanceof Signal)
				lastpowered = ((Signal)channel.getFunctionLocalData()).equals(Signal.ON);
			
			
			switch (channel.getTrigger().getTriggerId()) {
				case 0: // direct
					if (isRedstonePowered)
						tosend = Signal.ON;
					else tosend = Signal.OFF;
					break;
				case 1: // invert
					if (isRedstonePowered)
						tosend = Signal.OFF;
					else tosend = Signal.ON;
					break;
						
				case 2: // flank positive
					if (!lastpowered && isRedstonePowered)
						tosend = Signal.PULSE;
					else tosend = Signal.OFF;
					break;
				case 3: // flank negative
					if (lastpowered && !isRedstonePowered)
						tosend = Signal.PULSE;
					else tosend = Signal.OFF;
					break;
				case 4:	// change
					//LogHelper.info(lastpowered+" "+isRedstonePowered);
					if (lastpowered != isRedstonePowered)
						tosend = Signal.PULSE;
					
					break;
				case 5:
					// todo
					break;

				default:
					LogHelper
							.warn("Redstone Function has undefined Trigger type "
									+ channel.getTrigger().getTriggerId());
					break;
				}
			if (isRedstonePowered)
				channel.setFunctionLocalData(Signal.ON);
			else channel.setFunctionLocalData(Signal.OFF);
			
			if (tosend != null) {
				channel.getExtender().sheduleRemoteUpdate();
				channel.setSignal(tosend);
				SignalEvent.fireDirected(channel.getExtender(), channel.getExtender().getConnectedController(), tosend, channel.getNumber());
			}
		}
	}
	
	@Override
	public void onSignal(ExtenderChannel channel, Signal signal) {
		if (channel.getType() == ExtenderChannel.TYPES.OUTPUT) {
			Signal lastSignal = Signal.OFF;
			if (channel.getFunctionLocalData() instanceof Signal)
				lastSignal = (Signal)channel.getFunctionLocalData();
			if (!lastSignal.equals(signal)) {
				channel.getExtender().sheduleNeighbourUpdate();
				channel.getExtender().sheduleRemoteUpdate();
				lastSignal = signal;
				if (signal.equals(Signal.PULSE)) {
					signal = Signal.ON;
					channel.enableShedule(2);
				}
				
			}
			channel.setFunctionLocalData(lastSignal);
			channel.setSignal(signal);
			
		}
	}
	
	@Override
	public void onUpdate(ExtenderChannel channel,long worldTotalTime) {
		if (channel.getType() == ExtenderChannel.TYPES.OUTPUT)
			if (channel.getFunctionLocalData() instanceof Signal) {
				Signal lastSignal = (Signal)channel.getFunctionLocalData();
				if (lastSignal.equals(Signal.PULSE)) {
					channel.setFunctionLocalData(Signal.OFF);
					channel.setSignal(Signal.OFF);
					channel.getExtender().sheduleNeighbourUpdate();
					channel.getExtender().sheduleRemoteUpdate();
				}
			}
		
	}
}
