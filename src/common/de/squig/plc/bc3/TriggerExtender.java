package de.squig.plc.bc3;

import de.squig.plc.CommonProxy;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.tile.TileExtender;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.gates.Trigger;
import net.minecraft.src.TileEntity;

public class TriggerExtender extends Trigger
{
  int action = 0;

  public TriggerExtender(int id, int action) {
    super(id);
    this.action = action;
  }

  public String getTextureFile()
  {
    return CommonProxy.BC3_PNG;
  }

  public int getIndexInTexture()
  {
	  return 0;
  }

  public String getDescription()
  {
	  return "Output Channel "+this.action;
  }

  public boolean isTriggerActive(TileEntity tile, ITriggerParameter parameter)
  {
    if (tile == null) return false;
    if ((tile instanceof TileExtender)) {
    	TileExtender teb = (TileExtender)tile;
    	if (teb.getChannelsOut().size() > action) {
    		ExtenderChannel chn = teb.getChannelsOut().get(action);
    		return chn.getSignal().equals(Signal.ON);
    	}
    }
    return false;
  }
}