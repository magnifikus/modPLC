package de.squig.plc.client.gui.tiles;

public class LogicTextureTile {
	private final static int ONOffset = 4;
	
	public final static LogicTextureTile OUTPUT = new LogicTextureTile(0,4,ONOffset);
	//public final static LogicTextureTile INPUT = new LogicTextureTile(1,4,ONOffset);
	
	public final static LogicTextureTile LINELEFT = new LogicTextureTile(3,3,ONOffset);
	public final static LogicTextureTile LINERIGHT = new LogicTextureTile(4,3,ONOffset);
	public final static LogicTextureTile LINETOP = new LogicTextureTile(2,3,ONOffset);
	public final static LogicTextureTile LINEBOTTOM = new LogicTextureTile(1,3,ONOffset);
	public final static LogicTextureTile LINE = new LogicTextureTile(0,3,ONOffset);
	
	public final static LogicTextureTile INPIN = new LogicTextureTile(1,4,ONOffset);
	public final static LogicTextureTile OUTPIN = new LogicTextureTile(2,4,ONOffset);
	
	
	
	
	public final static LogicTextureTile TAG_INPUT = new LogicTextureTile(0,11);
	public final static LogicTextureTile TAG_OUTPUT = new LogicTextureTile(1,11);
	public final static LogicTextureTile TAG_MEM = new LogicTextureTile(2,11);
	public final static LogicTextureTile TAG_MEMSET = new LogicTextureTile(3,11);
	public final static LogicTextureTile TAG_MEMRESET = new LogicTextureTile(4,11);
	public final static LogicTextureTile TAG_OUTSET = new LogicTextureTile(5,11);
	public final static LogicTextureTile TAG_OUTRESET = new LogicTextureTile(6,11);
	public final static LogicTextureTile TAG_OUTPUTREAD = new LogicTextureTile(14,11);
	public final static LogicTextureTile TAG_INVERT = new LogicTextureTile(7,11);
	public final static LogicTextureTile TAG_STOP = new LogicTextureTile(8,11);
	public final static LogicTextureTile TAG_RESET = new LogicTextureTile(9,11);
	public final static LogicTextureTile TAG_COUNTUP = new LogicTextureTile(10,11);
	public final static LogicTextureTile TAG_COUNTDOWN = new LogicTextureTile(11,11);
	public final static LogicTextureTile TAG_TOP = new LogicTextureTile(12,11);
	public final static LogicTextureTile TAG_BOTTOM = new LogicTextureTile(13,11);
	
	
	
	
	public final static LogicTextureTile CURSOR = new LogicTextureTile(3,1);
	
	
	public final static LogicTextureTile LOGIC_PULSE = new LogicTextureTile(0,12);
	public final static LogicTextureTile LOGIC_NOT = new LogicTextureTile(1,12,1);
	public final static LogicTextureTile LOGIC_HIGH = new LogicTextureTile(2,12);
	public final static LogicTextureTile LOGIC_TIMER = new LogicTextureTile(3,12);
	public final static LogicTextureTile LOGIC_COUNTER = new LogicTextureTile(4,12);
	
	
	public final static int MODIFIER_ON = 4;

	
	
	public int x;
	public int y;
	public int xon;
	public int yon;
	public LogicTextureTile(int x, int y) {
		this.x = x*16;
		this.y = y*16;
		this.xon = this.x;
		this.yon = this.y;
	}
	public LogicTextureTile(int x, int y, int offset) {
		this(x,y);
		this.yon = this.y+offset*16;
	}

}
