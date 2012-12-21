package de.squig.plc.client.gui.controlls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class ChannelButton extends GuiButton {
	public enum TYPES {INACTIVE, ON, OFF, EDIT};
	private boolean output;
	private TYPES type;
	private int idx;
	
	public ChannelButton(int par1, int par2, int par3, TYPES type, int idx, boolean output) {
		super(par1, par2, par3,8,8, null);
		this.type = type;
		this.idx = idx;
		this.output = output;
		this.width = 5;
		this.height = 3;
	}
	
	
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/ressources/art/gui/extender.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
            
            int textureposx = 176;
            int textureposy = 4;
            
            if (idx % 5 == 0)
            	textureposy = 2;
            
            if (idx % 10 == 0)
            	textureposy = 0;
            
            
            textureposx += type.ordinal()*4;
            if (output)
            	textureposx += 16;
            
            this.drawTexturedModalRect(this.xPosition, this.yPosition,  textureposx,textureposy, 4, 2);
            this.mouseDragged(par1Minecraft, par2, par3);
           
        }
    }



	public TYPES getType() {
		return type;
	}



	public void setType(TYPES type) {
		this.type = type;
	}



	public int getIdx() {
		return idx;
	}



	public void setIdx(int idx) {
		this.idx = idx;
	}



	public boolean isOutput() {
		return output;
	}



	
}
