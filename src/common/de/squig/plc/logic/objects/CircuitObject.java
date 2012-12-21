package de.squig.plc.logic.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import buildcraft.core.ByteBuffer;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;

public abstract class CircuitObject {
	private static Class objects[] = new Class[256];
	private static List<Class> objectsData[] = new List[256];
	private List<GuiFunction> guiFunctions = null;
	
	
	private List<Class> dataTypes;
	
	protected Circuit circuit;
	
	protected short linkNumber = -1;
	protected String name = null;
	protected short flags = 0;
	
	protected boolean changed = true;
	
	
	private List<CircuitObjectInputPin> inputs;
	private List<CircuitObjectOutputPin> outputs;
	
	protected CircuitObjectData objData = null;
	
	
	
	
	public CircuitObject(Circuit circuit, List<Class> dataTypes) {
		this.circuit = circuit;
		this.dataTypes = dataTypes;
		if (dataTypes != null)
			objData = new CircuitObjectData(dataTypes);
		inputs = new ArrayList<CircuitObjectInputPin>();
		outputs = new ArrayList<CircuitObjectOutputPin>();
	}
	
	
	protected void setGuiFunctions(List<GuiFunction> guiFunctions) {
		this.guiFunctions = guiFunctions;
	}
	
	public Circuit getCircuit() {
		return circuit;
	}


	public long getNextActivation() {
		return -1;
	}
	
	public void preSimulation() {
	}
	public void postSimulation() {
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
	
	
	public static CircuitObjectNetworkData readFrom(DataInputStream data) throws IOException {
		short type = data.readShort();
		short linkNumberc = data.readShort();
		short flags = data.readShort();
		CircuitObjectData dt = null;
		if (objectsData[type] != null)
			dt = CircuitObjectData.readFromStream(objectsData[type], data);
		CircuitObjectNetworkData obj = new CircuitObjectNetworkData(type,linkNumberc, flags, dt);
		return obj;
	}
	
	public void saveTo(DataOutputStream data) throws IOException {
		data.writeShort(getCircuitObjectId(this.getClass()));
		data.writeShort(getLinkNumber());
		data.writeShort(flags);
		if (objData != null)
			objData.saveToStream(data);
	}


	public short getLinkNumber() {
		return linkNumber;
	}
	public void setLinkNumber(short nmbr) {
		linkNumber = nmbr;
	}

	
	public void commit() {
		
	}

	
	public boolean isChanged() {
		return changed;
	}


	public void setChanged(boolean changed) {
		this.changed = changed;
	}


	public String getData() {
		return "";
	}
	public void setData(String data) {
		
	}
	
	
	public static void addCircuitObjectType(int id, Class type, List<Class> dataTypes) {
		objects[id] = type;
		objectsData[id] = dataTypes;
	}
	public static short getCircuitObjectId(Class type) {
		for (short i = 0; i < objects.length; i++)
			if (type.equals(objects[i]))
				return i;
		return -1;
	}
	public static Class getClassForType(short type) {
		return objects[type];
	}


	public List<GuiFunction> getGuiFunctions() {
		return guiFunctions;
	}


	public CircuitObjectData getObjData() {
		return objData;
	}


	public void setObjData(CircuitObjectData objData) {
		this.objData = objData;
	}
	
	
}
