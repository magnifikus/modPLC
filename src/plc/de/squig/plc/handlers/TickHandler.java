package de.squig.plc.handlers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import de.squig.plc.logic.helper.LogHelper;

public class TickHandler implements ITickHandler
{
	
	private static TickHandler instance = null;
	
	public TickHandler() {
		this.instance = this;
	}
	
	
	private List<ITickNotified> listeners = new ArrayList<ITickNotified>();
	private long tick = 0;
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
            if(type.equals(EnumSet.of(TickType.SERVER)))
            {
                    onTickInGame();
            }
    }


            public EnumSet ticks()
        {
            return EnumSet.of(TickType.SERVER);
        }

        public String getLabel()
        {
            return null;
        }
        

        private void onTickInGame() 
        {
        	for (ITickNotified listener : listeners)
        		listener.onTick(tick);
        	tick++;
        }
        
        public static TickHandler getInstance() {
        	return instance;
        }
        
        public void addListener(ITickNotified listener) {
        	if (!listeners.contains(listener))
        		listeners.add(listener);
        }
        public void removeListener(ITickNotified listener) {
        	listeners.remove(listener);
        }

}