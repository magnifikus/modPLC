package de.squig.plc.client.gui.controlls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import de.squig.plc.CommonProxy;

public class TextureButton extends GuiButton {
	
	private int txtId = 0;
	private boolean active = false;
	private int id;
	private String hovertext;
	
	
	public TextureButton(int par1, int par2, int par3,  int txtId, boolean active, String hovertext, int id ) {
		super(par1, par2, par3,16,16, null);
		this.active = active;
		this.id = id;
		this.hovertext = hovertext;
		this.txtId = txtId;
	}
	
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture(CommonProxy.CONTROLLER_PNG));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
           
            int textureposx = (txtId % 16)*16;
            int textureposy = (txtId /16) *16;
            
            int ycorr = 0;
            if (txtId >= 176 && txtId < 192) {
            	ycorr = 3;
            	if (!active)
            		this.drawTexturedModalRect(this.xPosition, this.yPosition, 16,240,16,16);
            	else this.drawTexturedModalRect(this.xPosition, this.yPosition, 32,240,16,16);
            }

            
            this.drawTexturedModalRect(this.xPosition, this.yPosition+ycorr, textureposx,textureposy,16,16);
            
            
            if (par2 >= this.xPosition && par2 < this.xPosition+this.width)
            	if (par3 >= this.yPosition && par3 < this.yPosition+this.height) {
            		int width = var4.getStringWidth(hovertext);
            		int xx = this.xPosition +3;
            		int yy = this.yPosition -12;
            		int xcorr = 0;
            		if (xx+width > 450)
            			xx = xx+(450-(xx+width));
            		drawGradientRect(xx-2+xcorr,yy-2, xx+width+2+xcorr, yy+8+2, 0xc0000000, 0xf0000000);
            		var4.drawString(hovertext, xx+xcorr,yy, 0xffffff);
            	}
            
            
            
            //this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            int var6 = 14737632;

            if (!this.enabled)
            {
                var6 = -6250336;
            }
            else if (this.field_82253_i)
            {
                var6 = 16777120;
            }

            //this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
        }
    }



	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	public String getHovertext() {
		return hovertext;
	}


	public void setHovertext(String hovertext) {
		this.hovertext = hovertext;
	}


	public int getTxtId() {
		return txtId;
	}


	public int getId() {
		return id;
	}
	
	
}
