package de.squig.plc;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import de.squig.plc.blocks.Controller;
import de.squig.plc.blocks.ExtenderBasic;
import de.squig.plc.event.NetworkBroker;
import de.squig.plc.handlers.PacketHandler;
import de.squig.plc.handlers.TickHandler;
import de.squig.plc.item.ItemMulti;
import de.squig.plc.item.ItemRawWafer;
import de.squig.plc.lib.StaticData;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.Counter;
import de.squig.plc.logic.elements.Delay;
import de.squig.plc.logic.elements.Deleted;
import de.squig.plc.logic.elements.High;
import de.squig.plc.logic.elements.Input;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.elements.Not;
import de.squig.plc.logic.elements.Output;
import de.squig.plc.logic.elements.Pulse;
import de.squig.plc.logic.elements.Timer;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.logic.objects.LogicCounter;
import de.squig.plc.logic.objects.LogicDelay;
import de.squig.plc.logic.objects.LogicInput;
import de.squig.plc.logic.objects.LogicMemory;
import de.squig.plc.logic.objects.LogicOutput;
import de.squig.plc.logic.objects.LogicTimer;

@Mod(modid = "PLC", name = "modPLC", version = "0.0.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class, channels = { "modPLCChannel12" })
public class PLC {

	public static Block controller = null;
	public static Block extenderBasic = null;

	public static Item multiItem = null;
	public static Item itemWaferRaw = null;
	public static ItemStack itemWafer = null;
	public static ItemStack itemWaferRed = null;
	public static ItemStack itemWaferGreen = null;
	public static ItemStack itemWaferBlue = null;
	public static ItemStack itemChipRed = null;
	public static ItemStack itemChipGreen = null;
	public static ItemStack itemChipBlue = null;
	public static ItemStack itemInterface = null;
	public static ItemStack itemInterfaceRedstone = null;

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
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		config.load();

		StaticData.BlockController = config.getBlock("controller", 500)
				.getInt();
		StaticData.BlockExtender = config.getBlock("extender", 501).getInt();
		StaticData.range = config.get("extenders", "range", 32,
				"Range of Extenders to connect controllers (-1 no limit)")
				.getInt();

		StaticData.ItemsBase = config.getItem("itemsBase", 1900).getInt();
		StaticData.ItemWaferRaw = config.getItem("waferRaw", 1901).getInt();

		config.save();

		CircuitElement.addCircuitElementType(0, Deleted.class);
		CircuitElement.addCircuitElementType(1, Line.class);
		CircuitElement.addCircuitElementType(2, Input.class);
		CircuitElement.addCircuitElementType(3, Output.class);
		CircuitElement.addCircuitElementType(50, Not.class);
		CircuitElement.addCircuitElementType(51, High.class);
		CircuitElement.addCircuitElementType(100, Counter.class);
		CircuitElement.addCircuitElementType(101, Pulse.class);
		CircuitElement.addCircuitElementType(102, Timer.class);
		CircuitElement.addCircuitElementType(103, Delay.class);

		CircuitObject.addCircuitObjectType(1, LogicInput.class, null, null);
		CircuitObject.addCircuitObjectType(2, LogicOutput.class, null, null);
		CircuitObject.addCircuitObjectType(3, LogicMemory.class, null, null);
		CircuitObject.addCircuitObjectType(10, LogicDelay.class,
				LogicDelay.dataTypes, LogicDelay.dataStatics);
		CircuitObject.addCircuitObjectType(11, LogicTimer.class,
				LogicTimer.dataTypes, LogicTimer.dataStatics);
		CircuitObject.addCircuitObjectType(12, LogicCounter.class,
				LogicCounter.dataTypes, LogicCounter.dataStatics);

	}

	@Init
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		proxy.initTileEntities();

		// TickRegistry.registerTickHandler(new TickHandler(), Side.SERVER);

		NetworkRegistry.instance().registerGuiHandler(instance, proxy);

		controller = new Controller(StaticData.BlockController);
		extenderBasic = new ExtenderBasic(StaticData.BlockExtender);

		multiItem = new ItemMulti(StaticData.ItemsBase);
		itemWaferRaw = new ItemRawWafer(StaticData.ItemWaferRaw);

		LogHelper.info("multiItem @" + StaticData.ItemsBase);
		LogHelper.info("WafterRaw @" + StaticData.ItemWaferRaw);

		itemWafer = new ItemStack(multiItem, 1, StaticData.ItemsShiftWafer);
		itemWaferRed = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftRedWafer);
		itemWaferGreen = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftGreenWafer);
		itemWaferBlue = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftBlueWafer);
		itemChipRed = new ItemStack(multiItem, 1, StaticData.ItemsShiftRedChip);
		itemChipGreen = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftGreenChip);
		itemChipBlue = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftBlueChip);
		itemInterface = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftInterface);
		itemInterfaceRedstone = new ItemStack(multiItem, 1,
				StaticData.ItemsShiftInterfaceRedstone);

		LanguageRegistry.addName(controller, "Controller");
		MinecraftForge.setBlockHarvestLevel(controller, "pickaxe", 3);
		GameRegistry.registerBlock(controller, "plc.controller");

		LanguageRegistry.addName(extenderBasic, "Basic Extender");
		MinecraftForge.setBlockHarvestLevel(extenderBasic, "pickaxe", 3);
		GameRegistry.registerBlock(extenderBasic, "plc.extender");

		GameRegistry.registerItem(itemWaferRaw, "plc.rawwafer");
		GameRegistry.registerItem(multiItem, "plc.multiitem");

		LanguageRegistry.addName(itemWaferRaw, "Raw Wafer");
		LanguageRegistry.addName(itemWafer, "Sliced Wafer");
		LanguageRegistry.addName(itemWaferRed, "Red Wafer");
		LanguageRegistry.addName(itemWaferGreen, "Green Wafer");
		LanguageRegistry.addName(itemWaferBlue, "Blue Wafer");
		LanguageRegistry.addName(itemChipRed, "Power Chip");
		LanguageRegistry.addName(itemChipGreen, "Controller Chip");
		LanguageRegistry.addName(itemChipBlue, "Memory Chip");
		LanguageRegistry.addName(itemInterface, "Interface Card");
		LanguageRegistry.addName(itemInterfaceRedstone, "Redstone Interface");

		GameRegistry.addRecipe(new ItemStack(itemWaferRaw, 1), new Object[] {
				"SCS", "CSC", "SCS", 'C', Item.coal, 'S', Block.sand });

		GameRegistry.addSmelting(itemWaferRaw.shiftedIndex, new ItemStack(
				multiItem, 8, StaticData.ItemsShiftWafer), 0.1f);

		GameRegistry.addRecipe(new ItemStack(multiItem, 3,
				StaticData.ItemsShiftRedWafer), new Object[] { "   ", " R ",
				"WWW", 'W', itemWafer, 'R', Item.redstone });
		GameRegistry.addRecipe(new ItemStack(multiItem, 3,
				StaticData.ItemsShiftGreenWafer), new Object[] { "   ", "GRG",
				"WWW", 'W', itemWafer, 'R', Item.redstone, 'G',
				Item.lightStoneDust });
		GameRegistry.addRecipe(new ItemStack(multiItem, 3,
				StaticData.ItemsShiftBlueWafer), new Object[] { "   ", "LRL",
				"WWW", 'W', itemWafer, 'R', Item.redstone, 'L',
				new ItemStack(Item.dyePowder, 1, 4) });

		GameRegistry.addRecipe(new ItemStack(multiItem, 4,
				StaticData.ItemsShiftRedChip), new Object[] { "WIW", "ICI",
				"WIW", 'C', itemWaferRed, 'I', Item.ingotIron, 'W',
				Block.cloth});
		GameRegistry.addRecipe(new ItemStack(multiItem, 4,
				StaticData.ItemsShiftBlueChip), new Object[] { "WWW", "WCG",
				"WWW", 'C', itemWaferBlue, 'G', Item.goldNugget, 'W',
				Block.cloth});
		GameRegistry.addRecipe(new ItemStack(multiItem, 4,
				StaticData.ItemsShiftGreenChip), new Object[] { "WGW", "ICG",
				"WGW", 'C', itemWaferGreen, 'G', Item.goldNugget, 'I', Item.ingotIron,'W',
				Block.cloth});
		
		GameRegistry.addRecipe(new ItemStack(controller,1), new Object[] { "MMW", "PCG",
				"MMW", 'M', itemChipBlue, 'C', itemChipGreen, 
				'P',itemChipRed,'G', Block.glass,'W',
				Block.planks});
		
		GameRegistry.addRecipe(new ItemStack(extenderBasic,1), new Object[] { "WWI", "PCI",
			"WWI",  'C', itemChipGreen, 
			'P',itemChipRed,
			'I', itemInterface,
			'W', Block.planks});
	
		GameRegistry.addRecipe(new ItemStack(multiItem,8,StaticData.ItemsShiftInterface), new Object[] {
			"   ", "III","PWW", 
			'I', Item.ingotIron,
			'W', Block.woodSingleSlab,
			'P', Item.paper});
	
		GameRegistry.addRecipe(new ItemStack(multiItem,6,StaticData.ItemsShiftInterfaceRedstone), new Object[] {
			" R ", " C "," I ", 
			'C',itemChipRed,
			'I', itemInterface,
			'R', Item.redstone});
		
		// trying to load bc3 int

		try {
			Class plcmod = PLC.class.getClassLoader().loadClass(
					"de.squig.plc.bc3.BC3Integration");
			plcmod.getMethod("init", new Class[0]).invoke(null, new Object[0]);
		} catch (Throwable t) {
			LogHelper.warn("could not load BC3 Integration! " + t.getMessage());
		}

	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {

	}

	public NetworkBroker getNetworkBroker() {
		return networkBroker;
	}

}
