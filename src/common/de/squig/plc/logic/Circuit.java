package de.squig.plc.logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.handlers.ITickNotified;
import de.squig.plc.handlers.TickHandler;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.CircuitElementNetworkData;
import de.squig.plc.logic.elements.Deleted;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.CircuitObjectNetworkData;
import de.squig.plc.logic.simulator.CircuitSimulator;
import de.squig.plc.tile.TileController;

public abstract class Circuit implements Serializable, ITickNotified {
	protected List<CircuitObject> objects;

	protected CircuitMap map;

	protected TileController controller;
	protected CircuitSimulator simulator;
	
	
	
	protected List<CircuitElement> simulationList = null;
	protected List<Object> commitList = null;
	protected List<CircuitObject> watchList = null;
	
	

	protected boolean evaluated = false;
	
	protected boolean gotUpdatedForSimulator = true;
	protected boolean needsSimulation = true;
	protected long needsSimInTick = -1;
	protected long simulationTime = -1;
	

	public Circuit(TileController controller, int width, int height) {
		map = new CircuitMap(width, height);
		objects = new ArrayList<CircuitObject>();
		this.controller = controller;
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			//TickHandler.getInstance().addListener(this);
			simulator = new CircuitSimulator(this);
		}
	}

	public void addCircuitObject(CircuitObject object) {
		objects.add(object);
	}

	public void onDestroy() {
		TickHandler.getInstance().removeListener(this);
	}

	public CircuitObject getByType(short typeid, int number) {
		Class type = CircuitObject.getClassForType(typeid);
		for (CircuitObject obj : objects)
			if (obj.getClass().equals(type) && number == obj.getLinkNumber())
				return obj;
		return null;
	}

	public CircuitObject getByType(Class typ, int number) {
		for (CircuitObject obj : objects)
			if (obj.getClass().equals(typ))
				if (obj.getLinkNumber() == number)
					return obj;
		return null;
	}

	public List<CircuitObject> getByType(Class type) {
		List result = new ArrayList();
		for (CircuitObject obj : objects)
			if (obj.getClass().equals(type))
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
		List<CircuitObject> tosave = new LinkedList<CircuitObject>();
		for (CircuitObject obj : objects) {
			if (all || obj.isChanged()) {
				tosave.add(obj);
			}
		}
		data.writeShort(tosave.size());
		for (CircuitObject obj : tosave) {
			obj.saveTo(data);
		}
		
	}

	public void saveStateTo(DataOutputStream data) throws IOException {

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
		evaluated = false;

	}

	public void injectObjects(List<CircuitObjectNetworkData> objects,
			boolean all) {
		for (CircuitObjectNetworkData cond : objects) {
			CircuitObject obj = getByType(cond.getTypeID(), cond.getLinkName());
			obj.setFlags(cond.getFlags());
			obj.setObjData(cond.getData());
			obj.setChanged(true);
		}
	}


	public void savePoweredTo(DataOutputStream data) throws IOException {
		String injmap = "";
		data.writeInt(map.width*map.height);
		for (int y = 0; y < map.height; y++) {
			for (int x = 0; x < map.width; x++) {
				CircuitElement ele = map.getElementAt(x, y); 
				if (ele != null && !(ele instanceof Deleted)) {
					data.writeBoolean(ele.isPowered());
					injmap += ""+ele.isPowered();
				}
				else {
					data.writeBoolean(false);
				}
			}
		}
		//LogHelper.info(injmap);
	}

	public void injectPowered(PoweredMapNetworkData netPowered) {
		int i = 0;
		String injmap = "";
		for (int y = 0; y < map.height; y++)
			for (int x = 0; x < map.width; x++) {
				CircuitElement ele = map.getElementAt(x, y); 
				if (ele != null && !(ele instanceof Deleted)) {
					ele.setPowered(netPowered.getMap().get(i));

					injmap += ""+ele.isPowered();
				}
				i += 1;
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
		List<CircuitObjectNetworkData> objs = new ArrayList<CircuitObjectNetworkData>();
		int num = data.readShort();
		for (int i = 0; i < num; i++) {
			objs.add(CircuitObject.readFrom(data));
		}
		return objs;
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

	public List<CircuitElement> getSimulationList() {
		return simulationList;
	}

	public void setSimulationList(List<CircuitElement> simulationList) {
		this.simulationList = simulationList;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

	public List<Object> getCommitList() {
		return commitList;
	}

	public void setCommitList(List<Object> commitList) {
		this.commitList = commitList;
	}

	public CircuitSimulator getSimulator() {
		return simulator;
	}

	public boolean isNeedsSimulation() {
		return needsSimulation;
	}

	public void setNeedsSimulation(boolean needsSimulation) {
		this.needsSimulation = needsSimulation;
	}

	public long getNeedsSimInTick() {
		return needsSimInTick;
	}

	public void setNeedsSimInTick(long needsSimInTicks) {
		this.needsSimInTick = needsSimInTicks;
	}

	public long getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(long simulationTime) {
		this.simulationTime = simulationTime;
	}

	public List<CircuitObject> getWatchList() {
		return watchList;
	}

	public void setWatchList(List<CircuitObject> watchList) {
		this.watchList = watchList;
	}

}
