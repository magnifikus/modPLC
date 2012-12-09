package de.squig.plc;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import de.squig.plc.client.gui.GuiController;
import de.squig.plc.client.gui.extender.GuiExtender;
import de.squig.plc.container.ContainerController;
import de.squig.plc.container.ContainerExtender;
import de.squig.plc.lib.GuiIds;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TileExtender;

public class CommonProxy implements IGuiHandler {
	public static String BLOCKS_PNG = "/ressources/art/gui/blocks.png";
	public static String BLOCKS2_PNG = "/ressources/art/gui/block2.png";
	public static String CONTROLLER_PNG = "/ressources/art/gui/controller.png";
	public static String EXTENDER_PNG = "/ressources/art/gui/extender.png";
	public static String TOUCH_PNG = "/ressources/art/gui/touch.png";
	
	public static String BC3_PNG = "/ressources/art/gui/bc3.png";
	
	
	protected static int blockRenderId = -1;
	
	
	// Client stuff
	public void registerRenderers() {
		// Nothing here as this is the server side proxy
	}

	 public void initTileEntities() {
	    	// TODO: Constant
	    	GameRegistry.registerTileEntity(TileController.class, "tileController");
	    	
	    	// Try bc3 first
	    	try {
		      Class bc3Extender = PLC.class.getClassLoader().loadClass("de.squig.plc.bc3.compat.TileExtenderBC3");
		      GameRegistry.registerTileEntity(bc3Extender, "tileExtender");
		    } catch (Exception t) {
		    	GameRegistry.registerTileEntity(TileExtender.class, "tileExtender");
		    }
	    	
	 }
	
	
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == GuiIds.CONTROLLER) {
			TileController controller = (TileController)world.getBlockTileEntity(x, y, z);
			return new ContainerController(player.inventory, controller);
		}
		else if (ID == GuiIds.EXTENDER) {
			TileExtender extender = (TileExtender)world.getBlockTileEntity(x, y, z);
			return new ContainerExtender(player.inventory, extender);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == GuiIds.CONTROLLER) {
			TileController controller = (TileController)world.getBlockTileEntity(x, y, z);
			return new GuiController(controller);
		}
		else if (ID == GuiIds.EXTENDER) {
			TileExtender extender = (TileExtender)world.getBlockTileEntity(x, y, z);
			return new GuiExtender(player.inventory, extender);
		} 
		return null;
	}

	public static int getBlockRenderId() {
		return blockRenderId;
	}
	
	
	
}
