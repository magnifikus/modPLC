package de.squig.plc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.squig.plc.CommonProxy;
import de.squig.plc.PLC;
import de.squig.plc.lib.GuiIds;
import de.squig.plc.network.PacketControllerData;
import de.squig.plc.tile.TileController;

public class Controller extends BlockPLC {
	
	
	public Controller(int id) {
		super(id, Material.rock, 16);
		setHardness(4.0F); // 33% harder than diamond
		setStepSound(Block.soundStoneFootstep);
		setBlockName("Controller");
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.BLOCKS_PNG;
	}
	

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}


	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	
	
	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileController();
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k,
			EntityLiving entityliving) {
		super.onBlockPlacedBy(world, i, j, k, entityliving);
		TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
		if (tileEntity instanceof TileController) {
			((TileController) tileEntity).setControllerName(entityliving.getEntityName()+"'s");
		}
		
		
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			TileController tileController = (TileController) world
					.getBlockTileEntity(x, y, z);
			if (tileController != null) {
				player.openGui(PLC.instance, GuiIds.CONTROLLER, world, x, y, z);
				PacketControllerData.sendElements(tileController, player, true);
				PacketControllerData.updateArroundWithPowermap(tileController, 8);
			}
			

		}

		return true;
	}

}
