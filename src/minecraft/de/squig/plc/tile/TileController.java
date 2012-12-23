package de.squig.plc.tile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import de.squig.plc.PLC;
import de.squig.plc.event.ControllerDataEvent;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.SearchEvent;
import de.squig.plc.event.SearchResponseEvent;
import de.squig.plc.event.SignalEvent;
import de.squig.plc.event.payloads.ControllerDataPayload;
import de.squig.plc.logic.BasicCircuit;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.elements.CircuitElementNetworkData;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.CircuitObjectNetworkData;
import de.squig.plc.logic.objects.LogicInput;
import de.squig.plc.logic.objects.LogicOutput;

public class TileController extends TilePLC {
	public enum STATES {
		STOP, RUN, EDIT, ERROR
	};

	private STATES state = STATES.STOP;

	private Circuit circuit = null;

	private String controllerName = "unknown";

	private int range = 32;

	public TileController() {
		super(PLCEvent.TARGETTYPE.CONTROLLER);
		circuit = new BasicCircuit(this);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (circuit != null && circuit.getSimulator() != null)
			circuit.getSimulator().onTick(worldObj.getTotalWorldTime());
	}

	@Override
	protected void initialize() {
		super.initialize();

	}

	public void onEvent(PLCEvent event) {
		if (isInvalid()) {
			PLC.instance.getNetworkBroker().removeEventListener(this);
			return;
		}
		if (event instanceof SignalEvent) {
			SignalEvent events = (SignalEvent) event;
			CircuitObject obj = circuit.getByType(LogicInput.class,
					events.getChannel());
			if (obj instanceof LogicInput) {
				LogicInput inp = (LogicInput) obj;
				inp.onSignal(events.getSignal());
			}
		} else if (event instanceof ControllerDataEvent) {
			sendUpdatesToExtenders(true);
		} else if (event instanceof SearchEvent) {
			if (event.getSource().getWorldObj() == getWorldObj()) {

				int dx = xCoord - event.getSource().xCoord;
				int dy = yCoord - event.getSource().yCoord;
				int dz = zCoord - event.getSource().zCoord;
				int dist = (int) Math.floor(Math.sqrt(dx * dx + dy * dy + dz
						* dz));
				if (range >= dist) {
					SearchResponseEvent resp = new SearchResponseEvent(this,
							event.getSource().getUuid(), dist, uuid,
							controllerName, circuit.getByType(LogicInput.class)
									.size(), circuit.getByType(
									LogicOutput.class).size());
					PLC.instance.getNetworkBroker().fireEvent(resp);
				}
			}
		}

	}

	@Override
	public void onChunkUnload() {
		circuit.onDestroy();
		super.onChunkUnload();
	}

	public void onDestroy() {
		circuit.onDestroy();
		super.onDestroy();
	}

	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		if (nbtTagCompound.hasKey("name"))
			controllerName = nbtTagCompound.getString("name");
		try {

			InputStream is = new ByteArrayInputStream(
					nbtTagCompound.getByteArray("elements"));
			DataInputStream dis = new DataInputStream(is);
			List<CircuitElementNetworkData> nd = circuit.loadElementsFrom(dis,
					true);
			circuit.injectElements(nd, true);
		} catch (IOException ex) {
			LogHelper.error("exception durring reading elements data!");
		}
		try {

			InputStream is = new ByteArrayInputStream(
					nbtTagCompound.getByteArray("objects"));
			DataInputStream dis = new DataInputStream(is);
			List<CircuitObjectNetworkData> nd = circuit.loadObjectsFrom(dis,
					true);
			circuit.injectObjects(nd, true);
		} catch (IOException ex) {
			LogHelper.error("exception durring reading objects data!");
		}

	}

	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		

		nbtTagCompound.setString("name", controllerName);
		try {
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream w = new DataOutputStream(baos);
				circuit.saveElementsTo(w, true);
				w.flush();
				byte[] result = baos.toByteArray();
				nbtTagCompound.setByteArray("elements", result);
			}
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream w = new DataOutputStream(baos);
				circuit.saveObjectsTo(w, true);
				w.flush();
				byte[] result = baos.toByteArray();
				nbtTagCompound.setByteArray("objects", result);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Circuit getCircuit() {
		return circuit;
	}

	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
	}

	public TileController.STATES getState() {
		return this.state;
	}

	public void setState(TileController.STATES state) {
		this.state = state;
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void sendUpdatesToExtenders(boolean updateAll) {
		List<ControllerDataPayload> tosend = new ArrayList<ControllerDataPayload>();
		for (CircuitObject out : circuit.getByType(LogicOutput.class)) {
			LogicOutput lo = (LogicOutput) out;
			if (lo.isChanged() || updateAll) {
				//LogHelper.info("sending "+lo.getLinkNumber()+" "+lo.getSignal());
				tosend.add(new ControllerDataPayload(lo.getLinkNumber(), lo
						.getSignal()));
				lo.setChanged(false);
			}
		}
		if (tosend.size() > 0)
			PLC.instance.getNetworkBroker().fireEvent(
					new ControllerDataEvent(circuit.getController(), circuit
							.getController().getUuid(), tosend));
	}

}
