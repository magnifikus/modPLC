package de.squig.plc.client;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import de.squig.plc.CommonProxy;
import de.squig.plc.client.renderer.TEControllerRenderer;
import de.squig.plc.client.renderer.TEExtenderRenderer;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TileExtender;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(BLOCKS_PNG);
		MinecraftForgeClient.preloadTexture(BLOCKS2_PNG);
		MinecraftForgeClient.preloadTexture(CONTROLLER_PNG);
		MinecraftForgeClient.preloadTexture(EXTENDER_PNG);
		MinecraftForgeClient.preloadTexture(TOUCH_PNG);
		
		//MinecraftForgeClient.registerItemRenderer(501, blockRenderer);
		blockRenderId = RenderingRegistry.getNextAvailableRenderId();
	    ClientRegistry.bindTileEntitySpecialRenderer(TileController.class, new TEControllerRenderer());
	    ClientRegistry.bindTileEntitySpecialRenderer(TileExtender.class, new TEExtenderRenderer());
		    
		
		 
		
	}
	
}