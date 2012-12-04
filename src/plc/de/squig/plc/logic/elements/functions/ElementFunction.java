package de.squig.plc.logic.elements.functions;

import java.util.Hashtable;
import java.util.Vector;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.objects.LogicCounter;
import de.squig.plc.logic.objects.LogicInput;
import de.squig.plc.logic.objects.LogicMemory;
import de.squig.plc.logic.objects.LogicOutput;
import de.squig.plc.logic.objects.LogicTimer;

public class ElementFunction {
	
	private static Hashtable<String, ElementFunction> idRegistry = new Hashtable<String, ElementFunction>();
	
	public final static ElementFunction INPUT = new ElementFunction((short) 1,LogicTextureTile.TAG_INPUT, LogicInput.class,IoType.INPUT);

	public final static ElementFunction NOT = new ElementFunction((short) 2,null, null, IoType.INTERN);
	public final static ElementFunction HIGH = new ElementFunction((short) 3,null, null, IoType.INTERN);
	public final static ElementFunction PULSE = new ElementFunction((short) 4,null, null, IoType.INTERN);
	public final static ElementFunction LINE = new ElementFunction((short) 5,null, null, IoType.INTERN);

	public final static ElementFunction OUTPUTREAD = new ElementFunction((short) 6,LogicTextureTile.TAG_OUTPUTREAD, LogicOutput.class,IoType.INPUT);
	public final static ElementFunction OUTPUT = new ElementFunction((short) 7,LogicTextureTile.TAG_OUTPUT, LogicOutput.class, IoType.OUTPUT);
	public final static ElementFunction OUTPUTSET = new ElementFunction((short) 8,LogicTextureTile.TAG_OUTSET, LogicOutput.class, IoType.OUTPUT);
	public final static ElementFunction OUTPUTRESET = new ElementFunction((short) 9,LogicTextureTile.TAG_OUTRESET, LogicOutput.class, IoType.OUTPUT);
	
	public final static ElementFunction MEMORY = new ElementFunction((short) 10,LogicTextureTile.TAG_MEM, LogicMemory.class,IoType.INPUT);
	public final static ElementFunction MEMORYSET = new ElementFunction((short) 11,LogicTextureTile.TAG_MEMSET, LogicMemory.class, IoType.OUTPUT);
	public final static ElementFunction MEMORYRESET = new ElementFunction((short) 12,LogicTextureTile.TAG_MEMRESET, LogicMemory.class, IoType.OUTPUT);
	
	public final static ElementFunction TIMERSTOP = new ElementFunction((short) 13,LogicTextureTile.TAG_STOP, LogicTimer.class, IoType.OUTPUT);
	public final static ElementFunction TIMERRESET = new ElementFunction((short) 14,LogicTextureTile.TAG_RESET,  LogicTimer.class, IoType.OUTPUT);
	public final static ElementFunction TIMEROUTPUT = new ElementFunction((short) 15,LogicTextureTile.TAG_OUTPUT, LogicTimer.class, IoType.INPUT);
	
	public final static ElementFunction COUNTERSTOP = new ElementFunction((short) 16,LogicTextureTile.TAG_STOP, LogicCounter.class, IoType.OUTPUT);
	public final static ElementFunction COUNTERRESET = new ElementFunction((short) 17,LogicTextureTile.TAG_RESET,  LogicCounter.class, IoType.OUTPUT);
	public final static ElementFunction COUNTERCOUNTUP = new ElementFunction((short) 18,LogicTextureTile.TAG_COUNTUP,  LogicCounter.class, IoType.OUTPUT);
	public final static ElementFunction COUNTERCOUNTDOWN = new ElementFunction((short) 19,LogicTextureTile.TAG_COUNTDOWN,   LogicCounter.class, IoType.OUTPUT);
	public final static ElementFunction COUNTERTOP = new ElementFunction((short) 20,LogicTextureTile.TAG_TOP,  LogicCounter.class,IoType.INPUT);
	public final static ElementFunction COUNTERBOTTOM = new ElementFunction((short) 21,LogicTextureTile.TAG_BOTTOM,  LogicCounter.class,IoType.INPUT);
	
	private LogicTextureTile tag;
	private Class linkType;
	private IoType iotype = null;
	private short id;
	
	public ElementFunction(short id, LogicTextureTile tag, Class linkType, IoType iotype) {
		this.id = id;
		this.tag = tag;
		this.linkType = linkType;
		this.iotype = iotype;
	
		idRegistry.put(""+id,this);
	}
	
	public static ElementFunction getById(int id) {
		return idRegistry.get(""+id);
	}
	
	public LogicTextureTile getTag() {
		return tag;
	}
	public Class getLinkType() {
		return linkType;
	}
	public IoType getIotype() {
		return iotype;
	}

	public short getId() {
		return id;
	}
	
	
	
}
