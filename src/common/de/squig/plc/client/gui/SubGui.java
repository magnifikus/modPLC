package de.squig.plc.client.gui;

import net.minecraft.src.GuiButton;

public interface SubGui {
	public void onClose();
	public void onOpen();
	public void renderBackground();
	public void renderForeground();
	public void actionPerformed (GuiButton button);
}
