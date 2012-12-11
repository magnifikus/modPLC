/**
 * 
 * 
 * 
 * 
 * contributors: 
 * 
 * Thunderdark:  detection of side when placed and side detection for textures
 * 
 */

package de.squig.plc.blocks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TilePLC;

public abstract class BlockPLC extends BlockContainer {
	private int textureindex;
	
	
	
	
	public BlockPLC(int id, Material material, int textureindex) {
		super(id, material);
		this.textureindex = textureindex;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k,
			EntityLiving entityliving) {

		TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
		if (tileEntity instanceof TilePLC) {
			TilePLC tileP = (TilePLC) tileEntity;

			int l = MathHelper
					.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			int i1 = Math.round(entityliving.rotationPitch);
			if (i1 >= 65) {
				tileP.setSide((short) 1);
			} else if (i1 <= -65) {
				tileP.setSide((short) 0);
			} else if (l == 0) {
				tileP.setSide((short) 2);
			} else if (l == 1) {
				tileP.setSide((short) 5);
			} else if (l == 2) {
				tileP.setSide((short) 3);
			} else if (l == 3) {
				tileP.setSide((short) 4);
			}
		}

	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		TilePLC tileP = (TilePLC) world.getBlockTileEntity(x, y, z);
		tileP.onDestroy();

		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	
	
	@Override
	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k,
			int l) {
		TileEntity tileEntity = iblockaccess.getBlockTileEntity(i, j, k);
		
		int facing = getFrontSide(tileEntity);
		if (tileEntity instanceof TilePLC)
			facing = ((TilePLC) tileEntity).getSide();
		
		
		if (facing == l)
			return textureindex;
		if (l  == 1)
			return textureindex+2;
		else return textureindex+1;
		
	}
	
	@Override
	public int getBlockTextureFromSide(int s) {
		if (s == 3)
			return textureindex;
		if (s == 1)
			return textureindex +2;
		return textureindex +1;
	}
	
	
	protected int getFrontSide(TileEntity tileEntity) {
		return 1;
	}

	

}
