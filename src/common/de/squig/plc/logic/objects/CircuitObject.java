package de.squig.plc.logic.objects;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;

public abstract class CircuitObject {
	
	public static enum TYPES {
		INPUT, OUTPUT, MEMORY, COUNTER, TIMER, DELAY
	}
	protected TYPES type = null;
	
	protected Circuit circuit;
	
	protected String linkNumber = null;
	protected String name = null;
	protected short flags = 0;
	
	
	private List<CircuitObjectInputPin> inputs;
	private List<CircuitObjectOutputPin> outputs;
	
	
	public CircuitObject(Circuit circuit, TYPES type) {
		this.circuit = circuit;
		this.type = type;
		inputs = new ArrayList<CircuitObjectInputPin>();
		outputs = new ArrayList<CircuitObjectOutputPin>();
	}
	
	
	public Circuit getCircuit() {
		return circuit;
	}



	public String getLinkNumber() {
		return linkNumber;
	}




	public void setLinkNumber(String linkNumber) {
		this.linkNumber = linkNumber;
	}




	public CircuitObjectInputPin getInputPin(ElementFunction funct) {
		return null;
	}
	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		return null;
	}
	
	
	public void addInputPin(CircuitObjectInputPin pin) {
		inputs.add(pin);
	}
	public void addOutputPin(CircuitObjectOutputPin pin) {
		outputs.add(pin);
	}

	public CircuitObjectInputPin getInputPin(int number) {
		return inputs.get(number);
	}
	public CircuitObjectOutputPin getOutputPin(int number) {
		return outputs.get(number);
	}

	public List<CircuitObjectInputPin> getInputs() {
		return inputs;
	}

	public List<CircuitObjectOutputPin> getOutputs() {
		return outputs;
	}

	public String getName() {
		return name;
	}
	
	
	public int getType() {
		return type.ordinal();
	}
	
	
	public String saveState() {
		return "";
	}
	public void loadState(String state) {
		
	}
	
	public void setFlags(short flags) {
		this.flags = flags;
	}
	public short getFlags() {
		return flags;
	}
	
	
	public CircuitObjectNetworkData readFrom(DataInputStream data) throws IOException {
		short type = data.readShort();
		char linkNumberc = data.readChar();
		short flags = data.readShort();
		String state = data.readUTF();
		CircuitObjectNetworkData obj = new CircuitObjectNetworkData(type,linkNumberc, flags, state);
		return obj;
	}


	public int getLinkNumberInt() {
		if (linkNumber == null)
			return 255;
		else return Integer.parseInt(linkNumber);
	}
	public void setLinkNumber(int nmbr) {
		if (nmbr == 255)
			linkNumber = "";
		else linkNumber = ""+nmbr;
	}

	
	public void commit() {
		
	}

}
