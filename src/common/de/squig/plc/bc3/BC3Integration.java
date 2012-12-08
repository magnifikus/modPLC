package de.squig.plc.bc3;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.squig.plc.tile.TileExtender;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.TileEntity;

import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionProvider;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;

public class BC3Integration implements ITriggerProvider, IActionProvider {

	private static List<TriggerExtender> triggersExtender;
	private static List<ActionExtender> actionsExtender;

	public static boolean init() {
		triggersExtender = new ArrayList<TriggerExtender>();
		actionsExtender = new ArrayList<ActionExtender>();
		for (int i = 0; i < 32; i++)
			triggersExtender.add(new TriggerExtender(250 + i, i));
		for (int i = 0; i < 64; i++)
			actionsExtender.add(new ActionExtender(250 + i, i));
		BC3Integration bc3int = new BC3Integration();
		ActionManager.registerTriggerProvider(bc3int);
		ActionManager.registerActionProvider(bc3int);
		
		System.out.println("[PLC] BuildCraft integration loaded");
		
		return true;
	}

	public LinkedList getPipeTriggers(IPipe pipe) {
		return null;
		
	}

	public LinkedList getNeighborTriggers(Block block, TileEntity tile) {
		LinkedList temp = new LinkedList();
		if (tile instanceof TileExtender) {
			for (int i = 0; i < ((TileExtender) tile).getOutChannels(); i++)
				temp.add(triggersExtender.get(i));
		}
		return temp;
	}

	@Override
	public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile) {
		LinkedList temp = new LinkedList();
		if (tile instanceof de.squig.plc.tile.TileExtender) {
			for (int i = 0; i < ((de.squig.plc.tile.TileExtender) tile).getInChannels(); i++)
				temp.add(actionsExtender.get(i));
		}
		return temp;
	}

}