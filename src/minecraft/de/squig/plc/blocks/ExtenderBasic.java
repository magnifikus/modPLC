package de.squig.plc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.squig.plc.CommonProxy;
import de.squig.plc.PLC;
import de.squig.plc.lib.GuiIds;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketExtenderData;
import de.squig.plc.tile.TileExtender;

public class ExtenderBasic extends BlockPLC {
	public ExtenderBasic(int id) {
		super(id, Material.rock, 0);
		setHardness(4.0F); 
		setStepSound(Block.soundStoneFootstep);
		setBlockName("Basic Extender");
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.BLOCKS_PNG;
	}
	

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	

	@Override
	public TileEntity createNewTileEntity(World var1) {
		if (!var1.isRemote || (var1.isRemote && !(var1 instanceof WorldServer)))
			return TileExtender.createInstance();
		else  return null;
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			TileExtender tileExtender = (TileExtender) world
					.getBlockTileEntity(x, y, z);
			if (tileExtender != null) {
				PacketExtenderData.sendElements(tileExtender, player);
				player.openGui(PLC.instance, GuiIds.EXTENDER, world, x, y, z);
			}

		}

		return true;
	}

	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		TileEntity tile =  world.getBlockTileEntity(i, j, k);
		if (!(tile instanceof TileExtender))
			return;
		TileExtender tileE = (TileExtender)tile;
		tileE.checkRedstonePower();
	}

	/**
	 * Is this block powering the block on the specified side
	 */
	@Override
	public boolean isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2,int par3,int par4,int par5) {
		return isIndirectlyPoweringTo(par1IBlockAccess,par2,par3,par4,par5);
	}
	@Override
	public boolean isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2,int par3,int par4,int par5) {
		return isIndirectlyPoweringTo(par1IBlockAccess,par2,par3,par4,par5);
		
	}
	
	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return true;
	}

	/**
	 * Is this block indirectly powering the block on the specified side
	 */
	
	public boolean isIndirectlyPoweringTo(IBlockAccess iblock, int x, int y, int z, int dir)
	    {
			boolean res;
	    	TileEntity tile = iblock.getBlockTileEntity(x, y, z);
	    	if (tile instanceof TileExtender) {
		    	TileExtender tilee = (TileExtender)tile;
		    	res = tilee.isSidePowered(ForgeDirection.getOrientation(dir).getOpposite());
	    	} else res = false;
	    	
	    	return res;
	    	
	    }

	public void notifyRedpowerChange() {
		
	}
	
	
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}


}
