package de.squig.plc.logic.elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

import de.squig.plc.client.gui.tiles.LogicTextureTile;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.elements.functions.IoType;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.CircuitObjectInputPin;
import de.squig.plc.logic.objects.CircuitObjectOutputPin;

public class CircuitElement implements Serializable {
	public static enum TYPES {
		INPUT, OUTPUT, LINE, NOT, PULSE, HIGH, COUNTER, TIMER, DELETED
	}
	public enum SIDES {UNDEF,TOP,BOTTOM,LEFT,RIGHT}
	
	
	public TYPES getType() {
		return type;
	}

	protected boolean evaluated = false;
	protected boolean simulated = false;

	protected TYPES type = null;

	protected IoType iotype = null;
	protected int mapX = -1;
	protected int mapY = -1;

	protected Circuit circuit = null;

	protected LogicTextureTile texture = null;
	protected List<LogicTextureTile> tags = new ArrayList<LogicTextureTile>();

	protected String linkNumber = null;
	protected boolean displayLink = false;

	protected boolean powered = false;
	protected boolean inpowered = false;
	
	
	protected Signal signal = Signal.OFF;
	protected Signal inSignal = Signal.OFF;
	
	protected boolean isChanged = true;
	

	protected ElementFunction defaultFunction = null;
	protected ElementFunction function = null;

	protected List<ElementFunction> functions = new ArrayList<ElementFunction>();

	protected CircuitObject linkedObject = null;
	protected CircuitObjectInputPin inputPin = null;
	protected CircuitObjectOutputPin outputPin = null;

	
	protected CircuitElement elementLeft = null;
	
	protected boolean inverted = false;
	protected boolean allowInvert = false;

	protected String name = null;
	
	public CircuitElement(Circuit circuit, int mapX, int mapY, TYPES type) {
		this.circuit = circuit;
		this.mapX = mapX;
		this.mapY = mapY;
		this.type = type;
	}

	public CircuitElement(Circuit circuit, int mapX, int mapY, TYPES type,
			ElementFunction defaultFunction) {
		this(circuit, mapX, mapY, type);
		this.defaultFunction = defaultFunction;
		this.function = defaultFunction;
		if (function != null && function.getTag() != null)
			this.tags.add(function.getTag());
	}

	public static CircuitElement createFromPacket(Circuit circuit, CircuitElementNetworkData data) {
		CircuitElement element = null;
		if (TYPES.INPUT.ordinal() == data.typeID) {
			element = new Input(null, data.mapX, data.mapY);
		} else if (TYPES.OUTPUT.ordinal() == data.typeID) {
			element = new Output(null, data.mapX, data.mapY);
		} else if (TYPES.LINE.ordinal() == data.typeID) {
			element = new Line(null, data.mapX, data.mapY);
		} else if (TYPES.NOT.ordinal() == data.typeID) {
			element = new Not(null, data.mapX, data.mapY);
		} else if (TYPES.HIGH.ordinal() == data.typeID) {
			element = new High(null,  data.mapX, data.mapY);
		} else if (TYPES.PULSE.ordinal() == data.typeID) {
			element = new Pulse(null, data.mapX, data.mapY);
		} else if (TYPES.TIMER.ordinal() == data.typeID) {
			element = new Timer(null,  data.mapX, data.mapY);
		} else if (TYPES.COUNTER.ordinal() == data.typeID) {
			element = new Counter(null, data.mapX, data.mapY);
		} else if (TYPES.DELETED.ordinal() == data.typeID) {
			element = new Deleted(null, data.mapX, data.mapY);
		}
		if (element != null) {
			element.setCircuit(circuit);
			element.setFunction(ElementFunction.getById(data.functionID));
			
			int link = data.linkNumber;
			LogHelper.info("setting link "+link);
			
			if (link != 255)
				element.setLinkNumber("" + link);
			else element.setLinkNumber("");
			
			
			element.setFlags(data.flags);
			element.setCustomFlags(data.customFlags);
			element.onObjectChange();
			element.setChanged(false);
		}

		return element;
	}
	
	public void tryIdInput(char c) {
		String linkTemp = getLinkNumber();
		if (linkTemp == null || linkTemp.equals("X"))
			linkTemp = new String("" + c);
		else if (linkTemp.length() == 1)
			linkTemp = linkTemp + c;
		else if (linkTemp.length() == 2)
			linkTemp = new String("" + c);
		setLinkNumber(linkTemp);
		if (!validateNumber()) {
			setLinkNumber(new String(""+c));
			validateNumber();
		}
	}

	public boolean validateNumber() {
		
		if (linkNumber == null || linkNumber.equals("X"))
			return false;
		
		CircuitObject co = null;
		
		if (function != null && function.getLinkType() != null) {
			co = circuit.getByType(function.getLinkType(), linkNumber);
			if (co != null) {
				if (co != linkedObject) {
					linkedObject = co;
					onObjectChange();
					return true;
				}
			} else 
				setLinkNumber("X");
		} else
			setLinkNumber("");
		return false;
	}

	public void onObjectChange() {
		System.out.println("Object change!");
		if (function != null && function.getLinkType() != null && linkedObject != null) {
			if (function.getIotype() == IoType.INPUT) {
				outputPin = linkedObject.getOutputPin(function);
				inputPin = null;
			} else if (function.getIotype() == IoType.OUTPUT) {
				outputPin = null;
				inputPin = linkedObject.getInputPin(function);
			}
		} else {
			inputPin = null;
			outputPin = null;
		}
	}

	public void functionCycle() {
		ElementFunction newfunct = function;
		if (functions.size() > 1) {
			int nowidx = functions.indexOf(function);

			int maxidx = functions.size() - 1;
			System.out.println("now: " + nowidx + " max:" + maxidx);

			if (nowidx < 0 || nowidx + 1 > maxidx)
				newfunct = functions.get(0);
			else
				newfunct = functions.get(nowidx + 1);
		}
		
		setFunction(newfunct);
		
		validateNumber();
	}
	public void setFunction(ElementFunction function) {
		if (this.function == function)
			return;
		if (this.function != null && this.function.getTag() != null)
			tags.remove(this.function.getTag());
		if (function != null && function.getTag() != null)
			tags.add(function.getTag());
		this.function = function;
		onObjectChange();
		isChanged = true;
		LogHelper.info("fnct set to"+function.getId());
	}

	public void tryInvert() {
		if (isAllowInvert())
			setInverted(!isInverted());
	}

	public void onNeighboursUpdate() {

	}

	public void onSignalUpdate() {

	}

	public boolean getPowered() {
		return powered;
	}

	public int getMapX() {
		return mapX;
	}

	public void setMapX(int mapX) {
		this.mapX = mapX;
	}

	public int getMapY() {
		return mapY;
	}

	public void setMapY(int mapY) {
		this.mapY = mapY;
	}

	public LogicTextureTile getTexture() {
		return texture;
	}

	public void setTexture(LogicTextureTile texture) {
		this.texture = texture;
	}

	public String getLinkNumber() {
		return linkNumber;
	}

	public void setLinkNumber(String displayNumber) {
		if ((displayNumber != null) != (this.linkNumber != null))
			setChanged(true);
		else {
			if (this.linkNumber != null && !this.linkNumber.equals(displayNumber))
				setChanged(true);
			if (displayNumber != null && !displayNumber.equals(this.linkNumber))
				setChanged(true);			
		}
		
		this.linkNumber = displayNumber;
	}

	public boolean isPowered() {	
		return powered;
	}

	public boolean isDisplayLink() {
		return displayLink;
	}

	public void setDisplayLink(boolean displayLink) {
		this.displayLink = displayLink;
	}

	public void setPowered(boolean powered) {
		
		this.powered = powered;
	}

	public Circuit getCircuit() {
		return circuit;
	}

	public List<LogicTextureTile> getTags() {
		return tags;
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		if (inverted != this.inverted) {
			setChanged(true);
			if (!inverted)
				this.tags.remove(LogicTextureTile.TAG_INVERT);
			else
				this.tags.add(LogicTextureTile.TAG_INVERT);

			this.inverted = !this.inverted;
		}

	}

	public boolean isAllowInvert() {
		return allowInvert;
	}

	protected void setAllowInvert(boolean allowInvert) {
		this.allowInvert = allowInvert;
	}

	public String getName() {
		return name;
	}

	public IoType getIotype() {
		return iotype;
	}

	public void setIotype(IoType iotype) {
		this.iotype = iotype;
	}

	public CircuitObject getLinkedObject() {
		return linkedObject;
	}

	public void setLinkedObject(CircuitObject linkedObject) {
		this.linkedObject = linkedObject;
	}

	public CircuitObjectInputPin getInputPin() {
		return inputPin;
	}

	public void setInputPin(CircuitObjectInputPin inputPin) {
		this.inputPin = inputPin;
	}

	public CircuitObjectOutputPin getOutputPin() {
		return outputPin;
	}

	public void setOutputPin(CircuitObjectOutputPin outputPin) {
		this.outputPin = outputPin;
	}

	public int getTypeID() {
		return type.ordinal();
	}


	public ElementFunction getFunction() {
		return function;
	}


	public short getFlags() {
		short ret = 0;
		//if (powered) ret |= 1;
		if (inverted) ret |= 2;
		return ret;
	}

	public void setFlags(short flags) {
		//setPowered((flags & 1) == 1);
		setInverted((flags & 2) == 2);
	}

	public void setCustomFlags(short flags) {

	}

	public short getCustomFlags() {
		return 0;
	}

	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
	}
	
	public void afterLoad() {
		validateNumber();
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
	
	
	public void saveTo(DataOutputStream data) throws IOException {
		data.writeChar(this.getMapX());
		data.writeChar(this.getMapY());
		data.writeShort(this.getTypeID());
		
		if (this.getFunction() != null) {
			data.writeShort(this.getFunction().getId());
			
		} else
			data.writeShort(0);

		if (this.getLinkNumber() != null) {
			try {
				int val = Integer.parseInt(this.getLinkNumber());
				data.writeChar(val);
			} catch (Exception ex) {
				data.writeChar(255);
			}
		} else
			data.writeChar(255);
		data.writeShort(this.getFlags());
		data.writeShort(this.getCustomFlags());
	}
 	


	public static CircuitElementNetworkData read(DataInputStream data) throws IOException {
		char mapX = data.readChar();
		char mapY = data.readChar();
		short typeID = data.readShort();
		short functionID = data.readShort();
		char linkNumber = data.readChar();
		short flags = data.readShort();
		short customFlags = data.readShort();
		return new CircuitElementNetworkData(mapX,
				mapY, typeID, functionID, linkNumber, flags, customFlags);
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}
	
	public void resetEvaluated() {
		this.evaluated = false;
	}
	
	public Signal getSignal() {
		//if (!isEvaluated())
		//	evaluate();
		if (inverted)
			return signal.invert();
		else return signal;	
	}
	
	public void setSignal (Signal signal) {
		this.signal = signal;
		if (signal.equals(Signal.ON)) {
			powered = true;
			
		}

	}
	
	

	protected Signal manipulateSignal(Signal signal) {
		return signal;
	}
	protected Signal manipulateSignal(Signal signal, Signal oldsignal) {
		return signal;
	}

	public CircuitElement getElementLeft() {
		return elementLeft;
	}

	public void setElementLeft(CircuitElement elementLeft) {
		this.elementLeft = elementLeft;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
		if (simulated == false) {
			powered = false;
			inpowered = false;
			inSignal = Signal.OFF;
			signal = Signal.OFF;
		}
	}

	public boolean isInpowered() {
		return inpowered;
	}

	public void setInpowered(boolean inpowered) {
		this.inpowered = inpowered;
	}

	public Signal getInSignal() {
		return inSignal;
	}

	public void setInSignal(Signal inSignal) {
		this.inSignal = inSignal;
		if (inSignal.equals(Signal.ON))
			inpowered = true;
	}

	public void simulate() {
		if (getOutputPin() != null) {
			setSignal(inSignal.getLowerSignal(getOutputPin().getSignal()));
		} else setSignal(inSignal);
		if (getInputPin() != null) {
			getInputPin().onSignal(getSignal());
		}
	}
	
}
