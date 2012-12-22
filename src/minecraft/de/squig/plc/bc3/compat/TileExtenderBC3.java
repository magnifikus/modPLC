package de.squig.plc.bc3.compat;

import de.squig.plc.bc3.ActionExtender;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.function.BC3Function;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TileExtender;

import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionReceptor;

public class TileExtenderBC3 extends TileExtender implements IActionReceptor {
	
	public TileExtenderBC3() {
		super();
	}
	
	@Override
	public void actionActivated(IAction action) {
		if (action instanceof ActionExtender) {
			int chn = ((ActionExtender) action).getAction();
			if (chn < getChannelsIn().size()) {
				ExtenderChannel channel = getChannelsIn().get(chn);
				if (channel.getFunction() instanceof BC3Function) 
					((BC3Function) channel.getFunction()).onBCAction(channel);
			}
			
		}
		
	}

}
