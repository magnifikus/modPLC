package de.squig.plc.bc3;

import de.squig.plc.CommonProxy;
import de.squig.plc.tile.TileExtender;
import buildcraft.api.gates.Action;


public class ActionExtender extends Action
{
  int action = 0;

  public ActionExtender(int id, int action) {
    super(id);
    this.action = action;
  }

  public int getIndexInTexture()
  {
	return 1;
  }

  public String getDescription()
  {
	  return "Input Channel "+this.action;
  }


@Override
public String getTexture() {
	return CommonProxy.BC3_PNG;
}

public int getAction() {
	return action;
}


}