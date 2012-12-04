package de.squig.plc.logic.elements;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.helper.LogHelper;

public class Line extends CircuitElement  {
	
	
	public static char FLAG_TOP = 0x01;
	public static char FLAG_BOT = 0x02;
	public static char FLAG_LEF = 0x04;
	public static char FLAG_RIG = 0x08;
	
	private boolean connLeft = false;
	private boolean connRight = false;
	private boolean connTop = false;
	private boolean connBot = false;
	
	private boolean lastCycleTop = true;
	private int connstate = 0;
	private boolean right;
	private boolean left;
	private boolean top;
	private boolean bot;
	
	private List<Line> connectedLines = new ArrayList<Line>();
	
	public Line(Circuit circuit, int mapX, int mapY) {
		super(circuit, mapX, mapY, TYPES.LINE, ElementFunction.LINE);
		connLeft = true;
		connRight = true;
		updateConnections();
		name = "Connection Line";
	}
	
	public void updateConnections() {
		tags.clear();
		if (connLeft)
			tags.add(LogicTextureTile.LINELEFT);
		if (connRight)
			tags.add(LogicTextureTile.LINERIGHT);
		if (connTop)
			tags.add(LogicTextureTile.LINETOP);
		if (connBot)
			tags.add(LogicTextureTile.LINEBOTTOM);
		
	}
	
	public void updateNeighboors () {
		 CircuitElement nleft = circuit.getMap().getElementAt(mapX-1, mapY);
		 CircuitElement nright = circuit.getMap().getElementAt(mapX+1, mapY);
		 CircuitElement ntop = circuit.getMap().getElementAt(mapX, mapY-1);
		 CircuitElement nbot = circuit.getMap().getElementAt(mapX, mapY+1);
		 
		 left = nleft != null;
		 right = nright != null;
		 bot = nbot != null && nbot instanceof Line;
		 top = ntop != null && ntop instanceof Line;
	}
	
	public void onNeighboursUpdate() {
		updateNeighboors();
		updateConnstate();
	}
	
	
	public void functionCycle() {
		updateNeighboors();
		
		if (top && !bot) {
			setConnTop(!isConnTop());
		}
		else if (!top && !bot) {
			setConnBot(!isConnBot());
		} else {
			if (lastCycleTop) {
				if (top)
					setConnTop(!isConnTop());
			} else {
				if (bot)
					setConnBot(!isConnBot());
			}
			lastCycleTop = !lastCycleTop;
		}
		
		updateConnstate();
		
	}
	
	private void updateConnstate() {
		LogHelper.info(mapX+" "+mapY+" updateConnstate called()");
		boolean sleft = left;
		boolean sright = right;
		if (!top && isConnTop())
			setConnTop(false);
		if (!bot && isConnBot())
			setConnBot(false);
		
		if (!isConnTop() && !isConnBot()) {
			sleft = true;
			sright = true;
		}
		
		
		setConnLeft(sleft);
		setConnRight (sright);
		
		// check top and bot if they are connected...
		CircuitElement ntop = circuit.getMap().getElementAt(mapX, mapY-1);
		CircuitElement nbot = circuit.getMap().getElementAt(mapX, mapY+1);
		if (nbot != null && nbot instanceof Line) {
			Line ebot = (Line) nbot;
			if ( isConnBot() != ebot.isConnTop()) {
				ebot.setConnTop(!ebot.isConnTop());
				ebot.updateConnections();
			}
		}
		if (ntop != null && ntop instanceof Line) {
			Line etop = (Line) ntop;		
			if ( isConnTop() != etop.isConnBot()) {
				etop.setConnBot(!etop.isConnBot());
				etop.updateConnections();
			}
		}
		
		updateConnections();
	}
	
	private boolean tryState(int state) {
		if (state % 1 == 0 && !left)
			return false;
		if (state % 2 == 0 && !right)
			return false;
		if (state % 4 == 0 && !top)
			return false;
		if (state % 8 == 0 && !bot)
			return false;
		
		
		return true;
	}

	public void afterLoad() {
		updateConnections();
		super.afterLoad();
	}

	public void setCustomFlags(short flags) {
		//System.out.println("got flags :"+flags);
		//LogHelper.info("got flags "+flags);
		setConnTop((flags & FLAG_TOP) == FLAG_TOP);
		setConnBot((flags & FLAG_BOT) == FLAG_BOT);
		setConnLeft((flags & FLAG_LEF) == FLAG_LEF);
		setConnRight((flags & FLAG_RIG) == FLAG_RIG);
		//LogHelper.info("flags would be now: "+getCustomFlags());
		setChanged(false);
	}
	public short getCustomFlags() {
		short ret = 0;
		if (connTop) ret |= FLAG_TOP;
		if (connBot) ret |= FLAG_BOT;
		if (connLeft) ret |= FLAG_LEF;
		if (connRight) ret |= FLAG_RIG;
		
		//LogHelper.info("Sending flags: "+ret);
		return ret;
	}
	public boolean isConnLeft() {
		return connLeft;
	}

	public void setConnLeft(boolean connLeft) {
		if (this.connLeft != connLeft)
			setChanged(true);
		this.connLeft = connLeft;
	}

	public boolean isConnRight() {
		return connRight;
	}

	public void setConnRight(boolean connRight) {
		if (this.connRight != connRight)
			setChanged(true);
		this.connRight = connRight;
	}

	public boolean isConnTop() {
		return connTop;
	}

	public void setConnTop(boolean connTop) {
		if (this.connTop != connTop) {
			setChanged(true);
			LogHelper.info(mapX+" "+mapY+" top is now "+connTop);
		}
		this.connTop = connTop;
	}

	public boolean isConnBot() {
		return connBot;
	}

	public void setConnBot(boolean connBot) {
		if (this.connBot != connBot) {
			setChanged(true);
			LogHelper.info(mapX+" "+mapY+" bot is now "+connBot);
		}

		this.connBot = connBot;
	}
	
	public List<CircuitElement> getConnectedInputs() {
		return getConnectedInputs(null ,SIDES.RIGHT);
	}
	private List<CircuitElement> getConnectedInputs(List<CircuitElement> ins, SIDES side) {
		if (ins == null)
			ins = new ArrayList<CircuitElement>();
		if (side == null)
			side = SIDES.RIGHT;
		
		CircuitElement eTemp= null;
		Line lTop = null;
		Line lBot = null;
		Line lLeft = null;
		CircuitElement eLeft = null;
		
		if (isConnTop()) {
			eTemp = circuit.getMap().getElementAt(mapX, mapY-1);
			if (eTemp instanceof Line)
				lTop  = (Line)eTemp;
		}
		if (isConnBot()) {
			eTemp = circuit.getMap().getElementAt(mapX, mapY+1);
			if (eTemp instanceof Line)
				lBot = (Line)eTemp;
		}
		eTemp = circuit.getMap().getElementAt(mapX-1, mapY);
		if (isConnLeft()) {
			if (eTemp instanceof Line)
				lLeft = (Line) eTemp;
		}
		eLeft = eTemp;
		
		if (eLeft != null && !(eLeft instanceof Line) && !(eLeft instanceof Deleted)) {
			ins.add(eLeft);
		}
		
		if (lTop != null && side != SIDES.TOP) {
			lTop.getConnectedInputs(ins, SIDES.BOTTOM);
		}
		if (lBot != null && side != SIDES.BOTTOM) {
			lBot.getConnectedInputs(ins, SIDES.TOP);
		}
		if (lLeft != null) {
			lLeft.getConnectedInputs(ins, SIDES.BOTTOM);
		}
		return ins;
	} 
	
	@Override
	public void evaluate() {
		List<CircuitElement> deps = getConnectedInputs();
		Signal signal = Signal.OFF;
		//LogHelper.info("has "+deps.size()+" deps...");
		for (CircuitElement dep : deps) {
			if (!dep.isEvaluated())
				dep.evaluate();
			signal = signal.getHigherSignal(dep.getSignal());
			if (signal == Signal.ON) {
				break;
			}
		}
		this.signal = signal;
		if (this.signal.equals(Signal.ON))
			powered = true;
		else powered = false;
		//LogHelper.info(powered+"");
	}	
}
