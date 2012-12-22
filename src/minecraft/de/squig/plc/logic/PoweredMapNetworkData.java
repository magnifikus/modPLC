package de.squig.plc.logic;

import java.util.ArrayList;
import java.util.List;

public class PoweredMapNetworkData {
	private int size;
	private List<Boolean> map = new ArrayList<Boolean>();
	
	
	
	public PoweredMapNetworkData(int size, List<Boolean> map) {
		super();
		this.size = size;
		this.map = map;
	}



	public int getSize() {
		return size;
	}



	public List<Boolean> getMap() {
		return map;
	}
	
	
}
