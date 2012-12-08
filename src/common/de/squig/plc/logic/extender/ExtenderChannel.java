package de.squig.plc.logic.extender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraftforge.common.ForgeDirection;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.ISignalListener;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.function.ExtenderFunction;
import de.squig.plc.logic.extender.function.ExtenderTrigger;
import de.squig.plc.logic.extender.function.RedstoneFunction;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.tile.TileExtender;

public class ExtenderChannel {
	public static enum TYPES {
		INPUT, OUTPUT
	}

	private TYPES type;
	private int number;
	private TileExtender extender;
	private int linkedChannel = -1;
	
	private String functionData = "";
	private String triggerData = "";
	private Object functionLocalData = "";
	private Object triggerLocalData = "";
	
	private int resetTimer = -1;
	

	private ExtenderFunction function = ExtenderFunction.disabledFunction;
	private int side = 6;

	private Signal signal = Signal.OFF;
	private ExtenderTrigger trigger;

	
	
	
	public ExtenderChannel(TileExtender extender, TYPES type, int number) {
		this.number = number;
		this.type = type;
		this.extender = extender;
	}
	
	public void setSignalWithReset(Signal signal, int timer) {
		
	}

	public int getLinkedChannel() {
		return linkedChannel;
	}


	public void setLinkedChannel(int linkedChannel) {
		this.linkedChannel = linkedChannel;
	}


	public TYPES getType() {
		return type;
	}

	public void setType(TYPES type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		if (type == TYPES.INPUT)
			return ("INPUT " + number);
		else
			return ("OUTPUT " + number);
	}

	public ExtenderFunction getFunction() {
		return function;
	}

	public void setFunction(int functionid) {
		if (function != null) {
			function.onDestroy(this);
		}
		function = ExtenderFunction.getFunction(this,functionid);
		if (function != null)
			function.onCreate(this);
	}

	public TileExtender getExtender() {
		return extender;
	}

	public Signal getSignal() {
		return signal;
	}

	public void setSignal(Signal signal) {
		this.signal = signal;
	}

	public void saveTo(DataOutputStream data) throws IOException {
		data.writeChar(number);
		data.writeChar(linkedChannel);
		data.writeChar(type.ordinal());
		data.writeChar(side);
		
		data.writeChar(function.getId());
		if (trigger != null)
			data.writeChar(trigger.getTriggerId());
		else data.writeChar(255);
		data.writeUTF(functionData);
		data.writeUTF(triggerData);
		
	}

	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	public static ExtenderChannelNetworkData readFrom(DataInputStream data)
			throws IOException {

		char number = data.readChar();
		char linkNumber = data.readChar();
		char type = data.readChar();
		char side = data.readChar();
		char functiontype = data.readChar();
		char triggerid = data.readChar();
		String functionData = data.readUTF();
		String triggervalue = data.readUTF();
		return new ExtenderChannelNetworkData(number,linkNumber, type, side, functiontype, triggerid, functionData,triggervalue);

	}

	public void inject(ExtenderChannelNetworkData dt) {
		if (this.type.ordinal() != dt.getType()) {
			LogHelper.error("Trying to Update Channel of wrong type! "
					+ this.getType().ordinal() + " != " + dt.getType());
			return;
		}
		this.linkedChannel = dt.getLinkNumber();
		
		side = dt.getSide();
		if (side < 6)
			extender.getSideChannels(side).add(this);
		else if (side == 6)
			for (int i = 0; i < 6; i++)
				extender.getSideChannels(i).add(this);

		setFunction(dt.getFunctionType());
		if (dt.getFunctionType() != 0)
			LogHelper.info("funct set to "+dt.getFunctionType()+ " chan: "+dt.getNumber());
			
		if (dt.getTriggerType() < 255 && function != null && function.getTriggers() != null) {
			for (ExtenderTrigger trg : function.getTriggers()) 
				if (trg.getTriggerId() == dt.getTriggerType()) 
					setTrigger(trg);
				
		} else trigger = null;
			
		functionData = dt.getFunctionData();
		triggerData = dt.getTriggerData();

	}

	public void onSignal(Signal signal) {
		
		if (type.equals(TYPES.INPUT)) {
			if (function != null)
				function.onSignal(this,signal);
			this.signal = signal;

		} else if (type.equals(TYPES.OUTPUT)) {
			if (function != null)
				function.onSignal(this,signal);
			
			//this.signal = signal;
		}
	}
	public void setSidePowered(Signal signal) {
		extender.setSidePowered(this, signal);
	}
	
	public void onRedstoneChanged(boolean isRedstonePowered, boolean b) {
		if (function instanceof RedstoneFunction)
			((RedstoneFunction) function).onRedstoneChanged(this,isRedstonePowered,b);
	}

	public ExtenderTrigger getTrigger() {
		return trigger;
	}
	public void setTrigger(ExtenderTrigger trigger) {
		this.trigger = trigger;
	}


	public String getFunctionData() {
		return functionData;
	}


	public void setFunctionData(String functionData) {
		this.functionData = functionData;
	}


	public String getTriggerData() {
		return triggerData;
	}


	public void setTriggerData(String triggerData) {
		this.triggerData = triggerData;
	}


	public Object getFunctionLocalData() {
		return functionLocalData;
	}


	public void setFunctionLocalData(Object functionLocalData) {
		this.functionLocalData = functionLocalData;
	}


	public Object getTriggerLocalData() {
		return triggerLocalData;
	}


	public void setTriggerLocalData(Object triggerLocalData) {
		this.triggerLocalData = triggerLocalData;
	}
	

}
