package de.squig.plc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import de.squig.plc.blocks.Controller;
import de.squig.plc.blocks.ExtenderBasic;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.PLCEventSubscriber;
import de.squig.plc.handlers.PacketHandler;
import de.squig.plc.handlers.TickHandler;
import de.squig.plc.logic.helper.DistanceHelper;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TilePLC;

@Mod(modid = "PLC", name = "ProgrammableLogicControllers", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class, channels = { "modPLCChannel12" })
public class PLC {
	public final static Block controller = new Controller(501);
	public final static Block extenderBasic = new ExtenderBasic(502);

	private Hashtable<String, PLCEventSubscriber> eventSubscriberClient = new Hashtable<String, PLCEventSubscriber>();
	private Hashtable<String, PLCEventSubscriber> eventSubscriberServer = new Hashtable<String, PLCEventSubscriber>();

	// The instance of your mod that Forge uses.
	@Instance("PLC")
	public static PLC instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "de.squig.plc.client.ClientProxy", serverSide = "de.squig.plc.CommonProxy")
	public static CommonProxy proxy;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		LogHelper.init();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		proxy.initTileEntities();

		TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);
		
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);

		LanguageRegistry.addName(controller, "Controller");
		MinecraftForge.setBlockHarvestLevel(controller, "pickaxe", 3);
		GameRegistry.registerBlock(controller);

		LanguageRegistry.addName(extenderBasic, "Basic Extender");
		MinecraftForge.setBlockHarvestLevel(extenderBasic, "pickaxe", 3);
		GameRegistry.registerBlock(extenderBasic);

	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {

	}

	public void addEventListener(PLCEventSubscriber subscriber) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		Hashtable<String, PLCEventSubscriber> eventSubscriber = eventSubscriberServer;
		if (side.equals(Side.CLIENT))
			 eventSubscriber = eventSubscriberClient;
			
			
			LogHelper.info("Tile registered at " + subscriber.getUuid()  + "-" + subscriber.getX() + "-"
					+ subscriber.getY()  + "-" + subscriber.getZ() );
			
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
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		//LogHelper.info("Event: " + event.getClass().getName()+" from "+ event.getSource().getUuid() + " to "+ event.getDest());
		
		
		if ((side == Side.SERVER  && !event.isServer()) 
				|| (side == Side.CLIENT && !event.isClient())) 
			return;
		
		Hashtable<String, PLCEventSubscriber> eventSubscriber = eventSubscriberServer;
		if (side.equals(Side.CLIENT))
			 eventSubscriber = eventSubscriberClient;
		
		List<PLCEventSubscriber> subs = new ArrayList<PLCEventSubscriber>();
		
		// Directed 
		if (event.getTarget() == null) {
			// UUID directed
			if (event.getDest() != null) 
				subs.add(eventSubscriber.get(event.getDest().toString()));
			else {
			// XZY directed
				for (PLCEventSubscriber sub1 : eventSubscriber.values()) 
					if (sub1.getDim() == event.getDim() && sub1.getX() == event.getX()
							&& sub1.getY() == event.getY() && sub1.getZ() == event.getZ())
						subs.add(sub1);
				
			}
		} else { // Broadcast
			for (PLCEventSubscriber sub1 : eventSubscriber.values())
				if (sub1.getTargetType().equals(event.getTarget()))
					subs.add(sub1);
		}
		
		if (subs.size() == 0) {
			LogHelper.info("No receipients for message found!");
		}
		
		for (PLCEventSubscriber sub : subs) {
			// @Todo interdimensional events....
			TileEntity tile = event.getSource().getWorldObj().getBlockTileEntity(sub.getX(),sub.getY(),sub.getZ());
			if (tile instanceof TilePLC) {
				 if (event.getRange() == null || DistanceHelper.getDistance(event.getSource(), tile) <= event.getRange())
					 ((TilePLC) tile).onEvent(event);
			}
			
		}
		
		
	}
}
