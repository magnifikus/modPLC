/**
 * 
 *  Contributors:
 *   
 *  Thunderdark - siding transformation and and rotation 
 *  from https://github.com/Thunderdark/ModularForceFieldSystem/blob/master/src/minecraft/chb/mods/mffs/client/TECapacitorRenderer.java
 * 
 */
package de.squig.plc.client.renderer;

import java.nio.FloatBuffer;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import de.squig.plc.tile.TileController;

public class TEControllerRenderer extends TileEntitySpecialRenderer {
	private FloatBuffer working = BufferUtils.createFloatBuffer(4);

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y,
			double z, float f) {

		TileController topview = (TileController) tile;

		GL11.glPushMatrix();

		// @TODO side impl

		int side = 3;

		float dx = 1F / 16;
		float dz = 1F / 16;
		float displayWidth = 1 - 2F / 16;
		float displayHeight = 1 - 2F / 16;
		GL11.glTranslatef((float) x, (float) y, (float) z);
		switch (side) {
		case 1:

			break;
		case 0:
			GL11.glTranslatef(1, 1, 0);
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glRotatef(180, 0, 1, 0);

			break;
		case 3:
			GL11.glTranslatef(0, 1, 0);
			GL11.glRotatef(0, 0, 1, 0);
			GL11.glRotatef(90, 1, 0, 0);

			break;
		case 2:
			GL11.glTranslatef(1, 1, 1);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glRotatef(90, 1, 0, 0);

			break;
		case 5:
			GL11.glTranslatef(0, 1, 1);
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glRotatef(90, 1, 0, 0);

			break;
		case 4:
			GL11.glTranslatef(1, 1, 0);
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glRotatef(90, 1, 0, 0);

			break;
		}
		GL11.glTranslatef(dx + displayWidth / 2, 1F, dz + displayHeight / 2);
		GL11.glRotatef(-90, 1, 0, 0);

		

		float topLeft = -0.4995f;

		float unit = 1 / 16f;
		float tu = 1/8f;
		
		
		
		
		
		GL11.glDisable(GL11.GL_LIGHTING);
		bindTextureByName("/ressources/art/txt/white.png");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	
       
		GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
	
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0,0);
		GL11.glVertex3f(topLeft + 15 * unit, topLeft + 14 * unit, 0.0001f); 
		GL11.glTexCoord2f(0,1);																	// corner
		GL11.glVertex3f(topLeft + 15 * unit, topLeft + 15 * unit, 0.0001f);
		GL11.glTexCoord2f(1,1);																	// corner
		GL11.glVertex3f(topLeft + 14 * unit, topLeft + 15 * unit, 0.0001f);
		GL11.glTexCoord2f(1,0);																	// corner
		GL11.glVertex3f(topLeft + 14 * unit, topLeft + 14 * unit, 0.0001f); 
		GL11.glEnd();
		
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glPopMatrix();
	}

	
}
