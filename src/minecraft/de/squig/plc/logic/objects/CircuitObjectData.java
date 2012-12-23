package de.squig.plc.logic.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.helper.LogHelper;

public class CircuitObjectData {
	private List<Object> data;
	private List<Class> types;
	private List<Boolean> statics;
	
	private boolean transmitStatic = true;
	private boolean transmitNonStatic = true;
	
	public CircuitObjectData(List<Class> types, List<Boolean> statics) {
		data = new ArrayList<Object>();
		this.types = types;
		this.statics = statics;
		for (Class typ : types)
			if (typ.equals(Short.class))
				data.add(new Short((short)0));
			else if (typ.equals(Character.class))
				data.add(new Character((char)0));
			else if (typ.equals(Integer.class))
				data.add(new Integer(0));
			else if (typ.equals(Long.class))
				data.add(new Long(0));
			else if (typ.equals(String.class))
				data.add(new String(""));
			else if (typ.equals(Float.class))
				data.add(new Float(0));
			else if (typ.equals(Double.class))
				data.add(new Double(0));
			else if (typ.equals(Boolean.class))
				data.add(new Boolean(false));
			else if (typ.equals(Byte.class))
				data.add(new Byte((byte)0));
		
	}
	
	public void saveToStream(DataOutputStream datas) throws IOException {
		int i = 0;
		datas.writeBoolean(transmitNonStatic);
		datas.writeBoolean(transmitStatic);
		for (Class typ:types) {
			boolean isStatic = statics.get(i);
			if ((isStatic && !transmitStatic) || (!isStatic && !transmitNonStatic)) {
				i++;
				continue;
			}
			if (typ.equals(Short.class))
				datas.writeShort((Short) data.get(i));
			else if (typ.equals(Character.class))
				datas.writeChar((Character) data.get(i));
			else if (typ.equals(Integer.class))
				datas.writeInt((Integer) data.get(i));
			else if (typ.equals(Long.class))
				datas.writeLong((Long) data.get(i));
			else if (typ.equals(String.class))
				datas.writeUTF((String) data.get(i));
			else if (typ.equals(Float.class))
				datas.writeFloat((Float) data.get(i));
			else if (typ.equals(Double.class))
				datas.writeDouble((Double) data.get(i));
			else if (typ.equals(Boolean.class))
				datas.writeBoolean((Boolean) data.get(i));
			else if (typ.equals(Byte.class))
				datas.writeByte((Byte) data.get(i));
			else {
				LogHelper.error("CircuitObjectData.saveToStream: could not write "+typ.getSimpleName());
			}
			i++;
		}	
	}
	
	public static CircuitObjectData readFromStream(List<Class> types, List<Boolean> statics, DataInputStream datai) throws IOException {
		CircuitObjectData data = new CircuitObjectData(types,statics);
		data.transmitNonStatic = datai.readBoolean();
		data.transmitStatic = datai.readBoolean();
		int i = 0;
		for (Class typ:types) {
			boolean isStatic = data.statics.get(i);
			if ((isStatic && !data.transmitStatic) || (!isStatic && !data.transmitNonStatic)) {
				i++;
				continue;
			}
			if (typ.equals(Short.class))
				data.data.set(i++,datai.readShort());
			else if (typ.equals(Character.class))
				data.data.set(i++,datai.readChar());
			else if (typ.equals(Integer.class))
				data.data.set(i++,datai.readInt());
			else if (typ.equals(Long.class))
				data.data.set(i++,datai.readLong());
			else if (typ.equals(String.class))
				data.data.set(i++,datai.readUTF());
			else if (typ.equals(Float.class))
				data.data.set(i++,datai.readFloat());
			else if (typ.equals(Double.class))
				data.data.set(i++,datai.readDouble());
			else if (typ.equals(Boolean.class))
				data.data.set(i++,datai.readBoolean());
			else if (typ.equals(Byte.class))
				data.data.set(i++,datai.readByte());
			else {
				LogHelper.error("CircuitObjectData.readFromStream: could not read "+typ.getSimpleName());
				data.data.set(i++,null);
			}
		}	
		return data;
	}
	
	public List<Object> getData() {
		return data;
	}

	public Object get(int i) {
		return data.get(i);
	}
	public void set(int i, Object datai) {
		data.set(i, datai);
	}
	public List<Boolean> getStatics() {
		return statics;
	}

	public boolean isTransmitStatic() {
		return transmitStatic;
	}

	public void setTransmitStatic(boolean transmitStatic) {
		this.transmitStatic = transmitStatic;
	}

	public boolean isTransmitNonStatic() {
		return transmitNonStatic;
	}

	public void setTransmitNonStatic(boolean transmitNonStatic) {
		this.transmitNonStatic = transmitNonStatic;
	}
	
}
