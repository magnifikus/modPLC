package de.squig.plc.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.handlers.ITickNotified;
import de.squig.plc.handlers.TickHandler;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.CircuitElementNetworkData;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.CircuitObject.TYPES;
import de.squig.plc.logic.objects.CircuitObjectNetworkData;
import de.squig.plc.logic.simulator.CircuitSimulator;
import de.squig.plc.tile.TileController;

public abstract class Circuit implements Serializable, ITickNotified {
	protected List<CircuitObject> objects;

	protected CircuitMap map;

	protected TileController controller;
	protected CircuitSimulator simulator;
	
	protected boolean gotUpdatedForSimulator = true;

	public Circuit(TileController controller, int width, int height) {
		map = new CircuitMap(width, height);
		objects = new ArrayList<CircuitObject>();
		this.controller = controller;
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			TickHandler.getInstance().addListener(this);
			simulator = new CircuitSimulator(this);
		}
	}

	public void addCircuitObject(CircuitObject object) {
		objects.add(object);
	}

	public void onDestroy() {
		TickHandler.getInstance().removeListener(this);
	}

	public List getByType(Class typ) {
		List result = new ArrayList();
		for (CircuitObject obj : objects)
			if (obj.getClass().isInstance(typ))
				result.add(obj);
		return result;
	}

	public CircuitObject getByType(int typeid, String number) {
		for (CircuitObject obj : objects)
			if (obj.getType() == typeid && number.equals(obj.getLinkNumber()))
				return obj;
		return null;
	}
	public CircuitObject getByType(TYPES type, String number) {
		for (CircuitObject obj : objects)
			if (obj.getType() == type.ordinal() && number.equals(obj.getLinkNumber()))
				return obj;
		return null;
	}

	public CircuitObject getByType(Class typ, String number) {
		for (CircuitObject obj : objects)
			if (obj.getClass().equals(typ))
				if (obj.getLinkNumber() != null
						&& obj.getLinkNumber().equals(number))
					return obj;
		return null;
	}

	public List<CircuitObject> getByType(CircuitObject.TYPES type) {
		List result = new ArrayList();
		for (CircuitObject obj : objects)
			if (obj.getType() == type.ordinal())
				result.add(obj);
		return result;
	}

	public CircuitMap getMap() {
		return map;
	}

	public List<CircuitObject> getObjects() {
		return objects;
	}


	public void saveElementsTo(DataOutputStream data, boolean all)
			throws IOException {
		List<CircuitElement> elements;
		if (all)
			elements = map.getAllElement();
		else
			elements = map.getChangedElements(true);
		data.writeShort(elements.size());
		for (CircuitElement ele : elements)
			ele.saveTo(data);

	}

	public void saveObjectsTo(DataOutputStream data, boolean all)
			throws IOException {

	}

	public void saveStateTo(DataOutputStream data) throws IOException {

	}

	public void savePoweredTo(DataOutputStream data) throws IOException {
		data.writeInt(map.width*map.height);
		for (int y = 0; y < map.height; y++) {
			for (int x = 0; x < map.width; x++) {
				CircuitElement ele = map.getElementAt(x, y); 
				if (ele != null) {
					data.writeBoolean(ele.isPowered());
					
				}
				else data.writeBoolean(false);
			}
		}
	}

	public void injectState(CircuitStateNetworkData netState) {

	}

	public void injectElements(List<CircuitElementNetworkData> elements,
			boolean all) {

		if (all) {
			getMap().empty();
		}
		List<CircuitElement> toFire = new ArrayList<CircuitElement>();

		
		for (CircuitElementNetworkData netElement : elements) {
			CircuitElement element;
			if ((element = getMap().getElementAt(netElement.mapX,
					netElement.mapY)) != null)
				getMap().removeElement(element);

			element = CircuitElement.createFromPacket(this, netElement);
			getMap().addElement(element, -1, -1, false);
			toFire.add(element);
			
		}
		
		for (CircuitElement ele : toFire) {
			ele.afterLoad();
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
				ele.setChanged(true);

			}
		}
		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			for (CircuitElement element : getMap().getAllElement()) {
				if (element instanceof Line) {
					Line line = (Line) element;
				}
			}
		gotUpdatedForSimulator = true;

	}

	public void injectObjects(List<CircuitObjectNetworkData> objects,
			boolean all) {

	}

	public void injectPowered(PoweredMapNetworkData netPowered) {
		int i = 0;
		for (int y = 0; y < map.height; y++)
			for (int x = 0; x < map.height; x++) {
				CircuitElement ele = map.getElementAt(x, y); 
				if (ele != null) {
					ele.setPowered(netPowered.getMap().get(i));
					//LogHelper.info(""+netPowered.getMap().get(i));
				}
				i++;
			}
		
	}

	public static List<CircuitElementNetworkData> loadElementsFrom(
			DataInputStream data, boolean all) throws IOException {
		List<CircuitElementNetworkData> eles = new ArrayList<CircuitElementNetworkData>();
		int num = data.readShort();
		for (int i = 0; i < num; i++) {
			eles.add(CircuitElement.read(data));
		}
		return eles;
	}

	public static List<CircuitObjectNetworkData> loadObjectsFrom(
			DataInputStream data, boolean all) throws IOException {
		return null;
	}

	public static CircuitStateNetworkData loadStateFrom(DataInputStream data)
			throws IOException {
		return null;
	}

	public static PoweredMapNetworkData loadPoweredFrom(DataInputStream data)
			throws IOException {
		int size = data.readInt();
		List<Boolean> map = new ArrayList<Boolean>();
		for (int i = 0; i < size; i++) {
			map.add(data.readBoolean());
		}
		return new PoweredMapNetworkData(size, map);
		
	}

	public TileController getController() {
		return controller;
	}

	public void onTick(long tick) {
		if (controller != null)
			// if (controller.getState() == TileController.STATE_RUN)
			simulator.onTick(tick);
	}

	

}
