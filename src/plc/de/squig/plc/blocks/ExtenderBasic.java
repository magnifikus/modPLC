package de.squig.plc.blocks;

import java.util.Map;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import de.squig.plc.PLC;
import de.squig.plc.lib.GuiIds;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketControllerData;
import de.squig.plc.network.PacketExtenderData;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TileExtender;

public class ExtenderBasic extends BlockContainer {
	public ExtenderBasic(int id) {
		super(id, Material.rock);
		setHardness(4.0F); // 33% harder than diamond
		setStepSound(Block.soundStoneFootstep);
		setBlockName("Basic Extender");
		setCreativeTab(CreativeTabs.tabBlock);

	}

	@Override
	public String getTextureFile() {
		return super.getTextureFile();// return CommonProxy.BLOCK_PNG;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileExtender();
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			
			TileExtender tileExtender = (TileExtender) world
					.getBlockTileEntity(x, y, z);
			if (tileExtender != null) {
				player.sendChatToPlayer("UUID: "+tileExtender.getUuid().toString());
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
		TileExtender tile = (TileExtender) world.getBlockTileEntity(i, j, k);
		if (tile != null) {
			tile.checkRedstonePower();
		}
		
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
	    	TileEntity tile = iblock.getBlockTileEntity(x, y, z);
	    	if (tile instanceof TileExtender) {
		    	TileExtender tilee = (TileExtender)tile;
		    	return tilee.isSidePowered(ForgeDirection.getOrientation(dir).getOpposite());
	    	} else return false;
	    }

	public void notifyRedpowerChange() {
		
	}
	
	
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isACube() {
		return false;
	}

}
