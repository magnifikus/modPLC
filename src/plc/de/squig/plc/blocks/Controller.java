package de.squig.plc.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.PLC;
import de.squig.plc.lib.GuiIds;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketControllerData;
import de.squig.plc.tile.TileController;

public class Controller extends BlockContainer {
	public Controller(int id) {
		super(id, Material.rock);
		setHardness(4.0F); // 33% harder than diamond
		setStepSound(Block.soundStoneFootstep);
		setBlockName("Controller");
		setCreativeTab(CreativeTabs.tabBlock);

	}

	@Override
	public String getTextureFile() {
		return super.getTextureFile();// return CommonProxy.BLOCK_PNG;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileController();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		TileController tileController = (TileController) world
				.getBlockTileEntity(x, y, z);
		tileController.onDestroy();

		super.breakBlock(world, x, y, z, par5, par6);
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			TileController tileController = (TileController) world
					.getBlockTileEntity(x, y, z);
			if (tileController != null) {
				player.sendChatToPlayer("UUID: "+tileController.getUuid().toString());
				
				player.openGui(PLC.instance, GuiIds.CONTROLLER, world, x, y, z);

				// Circuit circuit = tileController.getCircuit();

				PacketControllerData.sendElements(tileController, player, true);

			}

		}

		return true;
	}

}
