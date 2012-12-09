package de.squig.plc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.Configuration;
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
import de.squig.plc.event.NetworkBroker;
import de.squig.plc.event.PLCEvent;
import de.squig.plc.event.PLCEventSubscriber;
import de.squig.plc.handlers.PacketHandler;
import de.squig.plc.handlers.TickHandler;
import de.squig.plc.lib.StaticData;
import de.squig.plc.logic.helper.DistanceHelper;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TilePLC;

@Mod(modid = "PLC", name = "ProgrammableLogicControllers", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class, channels = { "modPLCChannel12" })
public class PLC {
	
	
	
	public static Block controller = null;
	public static Block extenderBasic = null;
	
	private NetworkBroker networkBroker = new NetworkBroker();
	
	
	// The instance of your mod that Forge uses.
	@Instance("PLC")
	public static PLC instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "de.squig.plc.client.ClientProxy", serverSide = "de.squig.plc.CommonProxy")
	public static CommonProxy proxy;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		LogHelper.init();
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		StaticData.BlockController = config.getBlock("controller", 500).getInt();
		StaticData.BlockExtender = config.getBlock("extender", 501).getInt();
		StaticData.SimulatorDelay = config.get("simulator", "delay",5, "how often the simulator will run (5 means ever 5 ticks)").getInt();
		config.save();
		
		
		controller = new Controller(StaticData.BlockController);
		extenderBasic = new ExtenderBasic(StaticData.BlockExtender);
		
		
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
		
		
		
		
		// trying to load bc3 int
		
		try
	    {
	      Class plcmod = PLC.class.getClassLoader().loadClass("de.squig.plc.bc3.BC3Integration");
	      plcmod.getMethod("init", new Class[0]).invoke(null, new Object[0]);
	    } catch (Exception t) {
	      LogHelper.warn("could not load BC3 Integration! "+t.getMessage());
	      t.printStackTrace();
	    }

	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {

	}

	public NetworkBroker getNetworkBroker() {
		return networkBroker;
	}
	
}
