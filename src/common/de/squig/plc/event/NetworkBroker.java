package de.squig.plc.event;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.logic.helper.DistanceHelper;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TilePLC;

public class NetworkBroker {
	private Hashtable<String, PLCEventSubscriber> eventSubscriberClient = new Hashtable<String, PLCEventSubscriber>();
	private Hashtable<String, PLCEventSubscriber> eventSubscriberServer = new Hashtable<String, PLCEventSubscriber>();
	private Hashtable<String, List<PLCEventSubscriber>> eventSubscriberMulticast = new Hashtable<String, List<PLCEventSubscriber>>();

	
	public void addMulticastListener(UUID dest, TilePLC tile) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (!side.equals(Side.SERVER))
			return;
		if (!eventSubscriberMulticast.containsKey(dest.toString()))
			eventSubscriberMulticast.put(dest.toString(),
					new LinkedList<PLCEventSubscriber>());
		PLCEventSubscriber sub = new PLCEventSubscriber(tile);
		
		List<PLCEventSubscriber> subs = eventSubscriberMulticast.get(dest
				.toString());
		boolean has = false;

		for (PLCEventSubscriber sub1 : subs) {
			if (sub1.getTile().equals(sub))
				has = true;
		}
		if (!has) {
			subs.add(sub);
			LogHelper.info("MultiCast listener added "+dest+" going to "+sub.getUuid());
		}
	}

	public void removeMulticastListener(UUID dest, TilePLC tile) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (!side.equals(Side.SERVER))
			return;
		if (!eventSubscriberMulticast.containsKey(dest.toString()))
			return;
	

		List<PLCEventSubscriber> subs = eventSubscriberMulticast.get(dest.toString());
		
		PLCEventSubscriber toRemove = null;

		for (PLCEventSubscriber sub1 : subs) {
			if (sub1.getTile().equals(tile))
				toRemove = sub1;
		}
		if (toRemove != null) {
			subs.remove(toRemove);
			LogHelper.info("MultiCast listener removed "+dest+" going to "+tile.getUuid());
		}
	}

	public void addEventListener(TilePLC tile) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		PLCEventSubscriber subscriber = new PLCEventSubscriber(tile);
		Hashtable<String, PLCEventSubscriber> eventSubscriber = eventSubscriberServer;
		if (side.equals(Side.CLIENT))
			eventSubscriber = eventSubscriberClient;

		LogHelper.info("Listener  added "+tile.getUuid()+" is "+tile.getTargettype());

		eventSubscriber.put(subscriber.getUuid().toString(), subscriber);
	}

	public void removeEventListener(TilePLC tile) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		Hashtable<String, PLCEventSubscriber> eventSubscriber = eventSubscriberServer;
		if (side.equals(Side.CLIENT))
			eventSubscriber = eventSubscriberClient;

		if (tile.getUuid() == null)
			return;
		PLCEventSubscriber sub = eventSubscriber.get(tile.getUuid().toString());
		if (sub != null)
			eventSubscriber.remove(sub);
	}

	public void fireEvent(PLCEvent event) {
		
		long start = System.nanoTime();
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		
		if ((side == Side.SERVER && !event.isServer())
				|| (side == Side.CLIENT && !event.isClient()))
			return;

		Hashtable<String, PLCEventSubscriber> eventSubscriber = eventSubscriberServer;
		if (side.equals(Side.CLIENT))
			eventSubscriber = eventSubscriberClient;

		List<PLCEventSubscriber> subs = new ArrayList<PLCEventSubscriber>();

		if (event.getDest() != null && !event.getSource().getUuid().equals(event.getDest()) ) {
			// UUID directed
			PLCEventSubscriber sub = eventSubscriber.get(event.getDest().toString());
			if (sub != null) {
				//if (sub.getTile().isInvalid()) {
				//	removeEventListener(sub.getTile());
				//} else
					subs.add(sub);	
			}

		} else if (event.getTarget() != null) {

			// Broadcast
			//List<PLCEventSubscriber> toRemove = new LinkedList<PLCEventSubscriber>();
			
			for (PLCEventSubscriber sub1 : eventSubscriber.values())
				/*if (sub1.tile.isInvalid())
					toRemove.add(sub1);
				else */
					if (sub1.getTargetType().equals(event.getTarget()))
						subs.add(sub1);
			//for (PLCEventSubscriber sub1 : toRemove)
			//	removeEventListener(sub1.getTile());
			
		} else if (event.getDest() != null) {
			// Multicast
			List<PLCEventSubscriber> subs2 = eventSubscriberMulticast.get(event.getDest().toString());
			if (subs2 != null) {
				/*List<PLCEventSubscriber> toRemove = new LinkedList<PLCEventSubscriber>();
				for (PLCEventSubscriber sub1 : subs2)
					if (sub1.tile.isInvalid())
						toRemove.add(sub1);
				for (PLCEventSubscriber sub1 : toRemove)
					removeMulticastListener(event.getDest(), sub1.getTile());*/
				subs = subs2;
			}
			
		}

		if (subs.size() == 0) {
			LogHelper.info("No receipients for message found!");
		}
		long start2 = 0;
		for (PLCEventSubscriber sub : subs) {
			TilePLC tile = sub.getTile();
			if (tile instanceof TilePLC) {
				if (event.getRange() == null
						|| DistanceHelper.getDistance(event.getSource(), tile) <= event
								.getRange()) {
					start2 = System.nanoTime();
					((TilePLC) tile).onEvent(event);
					//LogHelper.info("message delivered to "+tile.getTargettype()+" from "+event.getSource().getTargettype());
				}
			}

		}
		//LogHelper.info("took "+(start2-start)+" ns   onEventTook:"+(System.nanoTime()-start2));
	}

}
