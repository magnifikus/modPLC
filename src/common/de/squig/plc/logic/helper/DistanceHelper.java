package de.squig.plc.logic.helper;

import de.squig.plc.tile.TilePLC;

public class DistanceHelper {
	public static double getDistance(TilePLC t1, TilePLC t2) {
		if (t1 == null || t2 == null)
			return -1;
		if (t1.getWorldObj() != t2.getWorldObj())
			return -1;
		int d1 = t1.xCoord - t2.xCoord;
		int d2 = t1.yCoord - t2.yCoord;
		int d3 = t1.zCoord - t2.zCoord;
		
		return Math.sqrt(d1*d1+d2*d2+d3*d3);
	}
}
