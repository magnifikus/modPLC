package de.squig.plc.client.gui.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import de.squig.plc.client.gui.controlls.TextureButton;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.Deleted;

public class GuiInfoscreen {
	private CircuitElement element;
	private GuiController guiController;
	private List<GuiButton> controlls;

	private FontRenderer fontRenderer;
	private int x, y, width, height;

	public GuiInfoscreen(GuiController guiController, CircuitElement element) {
		this.guiController = guiController;
		this.element = element;
		controlls = new ArrayList<GuiButton>();
		fontRenderer = guiController.getFontRenderer();
		x = guiController.infoX;
		y = guiController.infoY;
		width = guiController.infoWidth;
		height = guiController.infoHeight;
		int xx = 0;
		
		if (element == null) {
		for (Class ele : CircuitElement.getElements()) {
			if (ele != null && ele != Deleted.class) {
				try {
					Method method = ele.getMethod("getDisplayName");
					String name = (String) method.invoke(null);
					 method = ele.getMethod("getDisplayTextureId");
					int txtId = (Integer)method.invoke(null);
					TextureButton btn = new TextureButton(0, x+10+(xx % 8)*16, y+30+(xx/8)*16, txtId, false, name, 0);
					controlls.add(btn);
					xx++;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}}
		guiController.getControllList().addAll(controlls);
	}

	public void drawForeground() {
		if (element != null)
			fontRenderer.drawString(element.getName(), x + 10, y + 10, 0x000000);
		else {
			fontRenderer.drawString("Insert an Element:", x + 10, y + 10, 0x000000);
		}

		

	}

	public void drawBackground() {

		for (int x = 0; x < width / 16; x++) {
			for (int y = 0; y < height / 16; y++) {
				int tx = 16;
				int ty = 16;
				if (x == 0)
					tx = 0;
				if (y == 0)
					ty = 0;
				if (x == (width - 1) / 16)
					tx = 32;
				if (y == (height - 1) / 16)
					ty = 32;
				int ssx = this.x + 16 * x;
				int ssy = this.y + 16 * y;

				if (tx == 16 && ty >= 16)
					tx = tx + 48;
				if (tx == 32 && ty >= 16)
					tx = tx + 48;

				guiController.drawTexturedModalRect(ssx, ssy, tx, ty, 16, 16);
			}
		}
	}

	public void onClose() {
		guiController.getControllList().removeAll(controlls);
	}

	public void drawCenteredString(FontRenderer par1FontRenderer,
			String par2Str, int par3, int par4, int par5) {
		guiController.drawCenteredString(par1FontRenderer, par2Str, par3, par4,
				par5);
	}

	public void drawString(FontRenderer par1FontRenderer, String par2Str,
			int par3, int par4, int par5) {
		guiController.drawString(par1FontRenderer, par2Str, par3, par4, par5);
	}

	public void drawTexturedModalRect(int par1, int par2, int par3, int par4,
			int par5, int par6) {
		guiController.drawTexturedModalRect(par1, par2, par3, par4, par5, par6);
	}

}
