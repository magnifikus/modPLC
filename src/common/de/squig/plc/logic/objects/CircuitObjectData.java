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
	
	public CircuitObjectData(List<Class> types) {
		data = new ArrayList<Object>();
		this.types = types;
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
		for (Class typ:types) {
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
	
	public static CircuitObjectData readFromStream(List<Class> types, DataInputStream datai) throws IOException {
		CircuitObjectData data = new CircuitObjectData(types);
		int i = 0;
		for (Class typ:types) {
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
	
	public Object get(int i) {
		return data.get(i);
	}
	public void set(int i, Object datai) {
		data.set(i, datai);
	}
	
}
