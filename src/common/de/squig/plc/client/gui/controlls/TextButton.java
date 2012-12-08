package de.squig.plc.client.gui.controlls;

import org.lwjgl.opengl.GL11;

import de.squig.plc.client.gui.extender.GuiExtender;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;

public class TextButton extends GuiButton {
	private String text;
	private boolean active;
	private int data = -1;
	public TextButton(int par1, int x, int y, String text, boolean active) {
		super(par1, x, y, 6*text.length(), 10, null);
		this.text = text;
		this.active = active;
		
	}
	public TextButton(int par1, int x, int y, String text, int data, boolean active) {
		super(par1, x, y, 6*text.length(), 10, null);
		this.text = text;
		this.active = active;
		this.data = data;
		
	}
	
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            
            
            
           this.mouseDragged(par1Minecraft, par2, par3);
            int var6 = 0xFF0000;

            if (!active)
            {
            	var6 = 0x000000;
               // var6 = -6250336;
            }
            else if (this.field_82253_i)
            {
            	var6 = 0xFF00FF;
                //var6 = 16777120;
            }

            //this.drawCenteredString(var4, this.text, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var6);
           var4.drawString(text,  this.xPosition,  this.yPosition, var6);
          
        }
    }



	public String getText() {
		return text;
	}



	public void setText(String text) {
		this.text = text;
	}



	public boolean isActive() {
		return active;
	}



	public void setActive(boolean active) {
		this.active = active;
	}
	public int getData() {
		return data;
	}
	public void setData(int data) {
		this.data = data;
	}
	
	
}
