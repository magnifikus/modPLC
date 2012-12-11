package de.squig.plc.bc3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionProvider;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.function.BC3Function;
import de.squig.plc.tile.TileExtender;

public class BC3Integration implements ITriggerProvider, IActionProvider {

	private static List<TriggerExtender> triggersExtender;
	private static List<ActionExtender> actionsExtender;

	public static boolean init() {
		triggersExtender = new ArrayList<TriggerExtender>();
		actionsExtender = new ArrayList<ActionExtender>();
		for (int i = 0; i < 32; i++)
			triggersExtender.add(new TriggerExtender(820 + i, i));
		for (int i = 0; i < 64; i++)
			actionsExtender.add(new ActionExtender(820 + i, i));
		BC3Integration bc3int = new BC3Integration();
		ActionManager.registerTriggerProvider(bc3int);
		ActionManager.registerActionProvider(bc3int);
		
		System.out.println("[PLC] BuildCraft integration loaded");
		
		return true;
	}
	
	@Override
	public LinkedList getPipeTriggers(IPipe pipe) {
		return null;
		
	}
	
	@Override
	public LinkedList getNeighborTriggers(Block block, TileEntity tile) {
		LinkedList temp = new LinkedList();
		if (tile instanceof TileExtender) {
			TileExtender tileE = (TileExtender) tile;
			int i = 0;
			for (ExtenderChannel chn : tileE.getChannelsOut()) {
				if (chn.getFunction() instanceof BC3Function)
					temp.add(triggersExtender.get(i));
				i++;
			}
		}
		return temp;
	}

	@Override
	public LinkedList<IAction> getNeighborActions(Block block, TileEntity tile) {
		LinkedList temp = new LinkedList();
		if (tile instanceof de.squig.plc.tile.TileExtender) {
			TileExtender tileE = (TileExtender) tile;
			int i = 0;
			for (ExtenderChannel chn : tileE.getChannelsIn()) {
				if (chn.getFunction() instanceof BC3Function)
					temp.add(actionsExtender.get(i));
				i++;
			}
		}
		return temp;
	}

}