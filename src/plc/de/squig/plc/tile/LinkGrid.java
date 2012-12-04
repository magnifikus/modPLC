package de.squig.plc.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.squig.plc.logic.helper.LogHelper;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class LinkGrid {
	private static Map WorldpowernetMap = new HashMap();
	
	
	static class Worldlinknet {
		private Map<Integer, TileController> controllers = new Hashtable<Integer, TileController>();
		private Map<Integer, TileExtender> extenders = new Hashtable<Integer, TileExtender>();
	


		public Map<Integer, TileController> getControllers() {
			return controllers;
		}


		public Map<Integer, TileExtender> getExtenders() {
			return extenders;
		}

		public int newID(TileEntity tileEntity)
		{
			Random random = new Random();
			int tempID = random.nextInt();

			if(tileEntity instanceof TileController){

				while (controllers.get(tempID) != null) {
					tempID = random.nextInt();
				}
				controllers.put(tempID, (TileController) tileEntity);

			}

			if(tileEntity instanceof TileExtender){

				while (extenders.get(tempID) != null) {
					tempID = random.nextInt();
				}
				extenders.put(tempID, (TileExtender) tileEntity);

			}

			return tempID;
		}




		public static int myRandom(int low, int high) {
			return (int) (Math.random() * (high - low) + low);
		}

		public List<TileController> conInRange(int xCoordr, int yCoordr,
				int zCoordr, int i) {
			List<TileController> result = new ArrayList<TileController>();
			for (TileController controller : controllers.values()) {
				
					int dx = controller.xCoord - xCoordr;
					int dy = controller.yCoord - yCoordr;
					int dz = controller.zCoord - zCoordr;

					if (i >= Math.sqrt(dx * dx + dy * dy + dz * dz)) {
						result.add(controller);
					}
				
			}


			return result;
		}
	}

	public static Worldlinknet getWorldMap(World world) {
		if (world != null) {
			if (!WorldpowernetMap.containsKey(world)) {
				LogHelper.info("new world created in Linknet");
				WorldpowernetMap.put(world, new Worldlinknet());
			}
			return (Worldlinknet) WorldpowernetMap.get(world);
		}

		return null;
	}
}
