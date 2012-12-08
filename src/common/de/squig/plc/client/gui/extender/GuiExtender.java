package de.squig.plc.client.gui.extender;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import de.squig.plc.client.gui.controlls.ChannelButton;
import de.squig.plc.client.gui.controlls.TextButton;
import de.squig.plc.client.gui.controlls.TouchButton;
import de.squig.plc.client.gui.tiles.DisplayTile;
import de.squig.plc.container.ContainerExtender;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.function.ExtenderFunction;
import de.squig.plc.logic.objects.CircuitObject;
import de.squig.plc.network.PacketExtenderData;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TileExtender;

public class GuiExtender extends GuiContainer {

	private TileController controller;
	protected TileExtender extender;

	public int screenX = 28;
	public int screenY = 7;

	private static int selectedChannel = -1;

	private SubGui subGui = null;

	private ExtenderChannel.TYPES myChannelType;

	ArrayList<GuiButton> btnsChannels = new ArrayList<GuiButton>();

	protected InventoryPlayer iplayer;

	public GuiExtender(InventoryPlayer inventoryPlayer, TileExtender extender) {
		super(new ContainerExtender(inventoryPlayer, extender));
		this.extender = extender;
		this.iplayer = inventoryPlayer;

	}

	
	
	public void initGui() {
		super.initGui();
		selectedChannel = 0;
		
		extender.sendBroadcastSearch();
		
		controlList.clear();
		refreshChannelButtons();
		setSubGui(new SubGuiFront(this));
		
	}
	
	public void guiBack() {
		setSubGui(new SubGuiFront(this));
	}
	protected void refreshChannelButtons() {
		controlList.removeAll(btnsChannels);
		btnsChannels.clear();

		int in = 0, out = 0;
		for (ExtenderChannel chn : extender.getChannels()) {
			GuiButton btn = null;
			ChannelButton.TYPES typ = ChannelButton.TYPES.INACTIVE;
			if (chn.getFunction().getId() > 0)
				typ = ChannelButton.TYPES.OFF;
			if (selectedChannel == extender
					.getChannels().indexOf(chn))
				typ = ChannelButton.TYPES.EDIT;
			
			if (chn.getType().equals(ExtenderChannel.TYPES.INPUT)) {
				btn = new ChannelButton(1, guiLeft + screenX, guiTop + screenY
						+ 10 + in * 3, typ, extender
						.getChannels().indexOf(chn), false);
				in++;
			} else if (chn.getType().equals(ExtenderChannel.TYPES.OUTPUT)) {
				btn = new ChannelButton(1, guiLeft + screenX + 136, guiTop + screenY
						+ 10 + out * 3, typ, extender
						.getChannels().indexOf(chn), true);
				out++;
			}

			if (btn != null) {
				btnsChannels.add(btn);
				controlList.add(btn);
			}
		}
	}


	protected void subDrawText(String text, int x, int y, int color) {
		fontRenderer.drawString(text, screenX + x, screenY + y, color);
	}

	protected TextButton subTextButton(int p1, int x, int y, String text,
			boolean active) {
		return new TextButton(p1, guiLeft + screenX + x, guiTop+ screenY + y, text, active);
	}
	protected TextButton subTextButton(int p1, int x, int y, String text,
			int data, boolean active) {
		return new TextButton(p1, guiLeft + screenX + x, guiTop+ screenY + y, text, data, active);
	}
	protected TouchButton subTouchButton(int p1, int x, int y,
			TouchButton.TYPES type) {
		return new TouchButton(p1, guiLeft + screenX +x, guiTop+ screenY +y, type);
	}
	protected TouchButton subTouchButton(int p1, int x, int y,
			TouchButton.TYPES type, int triggerId) {
		return new TouchButton(p1, guiLeft + screenX +x, guiTop+ screenY +y, type, triggerId);
	}
	
	public void addControl(GuiButton button) {
		controlList.add(button);
	}
	public void removeControl(GuiButton button) {
		controlList.remove(button);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		int texture = mc.renderEngine
				.getTexture("/ressources/art/gui/extender.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, 176);

		// draw in/out symbol left and right
		this.drawTexturedModalRect(guiLeft + screenX - 1, guiTop + screenY,
				176, 16, 5, 8);
		this.drawTexturedModalRect(guiLeft + screenX + 135, guiTop + screenY,
				181, 16, 5, 8);

		if (subGui != null)
			subGui.renderBackground();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int a1, int a2) {
		if (subGui == null) {
			fontRenderer.drawString("ERR NO GUI", screenX + 15, screenY,
					0x000000);
		} else {
			subGui.renderForeground();
		}
		

	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void actionPerformed(GuiButton button) {
		if (button instanceof ChannelButton) {
			ChannelButton cbutton = (ChannelButton) button;
			int idx = cbutton.getIdx();
			selectedChannel = idx;
			ExtenderChannel chn = extender.getChannels().get(idx);
			setSubGui(new SubGuiChannel(chn, this));
			refreshChannelButtons();
			
		} else {
			
			
			
			if (subGui != null)
				subGui.actionPerformed(button);
		}
		
		
	}

	public SubGui getSubGui() {
		return subGui;
	}

	public void setSubGui(SubGui subGui) {
		if (subGui != this.subGui) {
			if (this.subGui != null)
				this.subGui.onClose();
			if (subGui != null)
				subGui.onOpen();
		}
		this.subGui = subGui;
	}
	
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}
	
	
	



	private void updateContent() {
	
		
	}



	public void invokeServerUpdate() {
		PacketExtenderData.sendElements(extender, iplayer.player);
		
		
	}

}