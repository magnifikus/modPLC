package de.squig.plc.client.gui.controlls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class TouchButton extends GuiButton {
	public enum TYPES {UP, DOWN, LEFT, RIGHT, TRIGGER};
	private TYPES type;
	private int triggerID = 0;
	private boolean active = false;
	
	public TouchButton(int par1, int par2, int par3, TYPES type) {
		super(par1, par2, par3,8,8, null);
		this.type = type;
		
	}
	
	public TouchButton(int par1, int par2, int par3, TYPES type, int triggerID) {
		super(par1, par2, par3,16,16, null);
		
		this.type = type;
		this.triggerID = triggerID;
	}
	
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/ressources/art/gui/touch.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
            int textureposx = 0;
            int textureposy = 0;
            
            if (type == TYPES.UP)
            	textureposx = 0;
            if (type == TYPES.DOWN)
            	textureposx = 8;
            if (type == TYPES.LEFT)
            	textureposx = 16;
            if (type == TYPES.RIGHT)
            	textureposx = 24;
            if (type == TYPES.TRIGGER) {
            	textureposx = 0 + triggerID*16;
            	textureposy = 32;
            	if (active)
            		textureposy += 16;
            }
            
            
            this.drawTexturedModalRect(this.xPosition, this.yPosition,  textureposx,textureposy, this.width, this.height);
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

	public TYPES getType() {
		return type;
	}

	public int getTriggerID() {
		return triggerID;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
