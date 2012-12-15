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
		for (Class typ:types) {
			if (typ.equals(Short.class))
				data.data.add(datai.readShort());
			else if (typ.equals(Character.class))
				data.data.add(datai.readChar());
			else if (typ.equals(Integer.class))
				data.data.add(datai.readInt());
			else if (typ.equals(Long.class))
				data.data.add(datai.readLong());
			else if (typ.equals(String.class))
				data.data.add(datai.readUTF());
			else if (typ.equals(Float.class))
				data.data.add(datai.readFloat());
			else if (typ.equals(Double.class))
				data.data.add(datai.readDouble());
			else if (typ.equals(Boolean.class))
				data.data.add(datai.readBoolean());
			else if (typ.equals(Byte.class))
				data.data.add(datai.readByte());
			else {
				LogHelper.error("CircuitObjectData.readFromStream: could not read "+typ.getSimpleName());
				data.data.add(null);
			}
		}	
		return data;
	}
	
	
	
}
