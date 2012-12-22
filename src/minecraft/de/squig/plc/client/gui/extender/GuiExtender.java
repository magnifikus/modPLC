package de.squig.plc.client.gui.extender;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import de.squig.plc.client.gui.SubGui;
import de.squig.plc.client.gui.controlls.ChannelButton;
import de.squig.plc.client.gui.controlls.TextButton;
import de.squig.plc.client.gui.controlls.TouchButton;
import de.squig.plc.container.ContainerExtender;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.network.PacketExtenderData;
import de.squig.plc.tile.TileController;
import de.squig.plc.tile.TileExtender;

public class GuiExtender extends GuiContainer {

	private TileController controller;
	protected TileExtender extender;

	public int screenX = 28;
	public int screenY = 7;

	//private static int selectedChannel = -1;
	

	private SubGui subGui = null;
	private SubGui nextSubGui = null;

	private ExtenderChannel.TYPES myChannelType;
	
	private ExtenderChannel edit = null;
	
	

	ArrayList<GuiButton> btnsChannelsIn = new ArrayList<GuiButton>();
	ArrayList<GuiButton> btnsChannelsOut = new ArrayList<GuiButton>();

	protected InventoryPlayer iplayer;

	public GuiExtender(InventoryPlayer inventoryPlayer, TileExtender extender) {
		super(new ContainerExtender(inventoryPlayer, extender));
		this.extender = extender;
		this.iplayer = inventoryPlayer;
	}

	
	
	public void initGui() {
		super.initGui();
		//selectedChannel = 0;
		
		extender.sendBroadcastSearch();
		
		controlList.clear();
		refreshChannelButtons();
		setSubGui(new SubGuiFront(this));
		
	}
	
	public void guiBack() {
		setSubGui(new SubGuiFront(this));
	}
	protected void refreshChannelButtons() {
		controlList.removeAll(btnsChannelsIn);
		controlList.removeAll(btnsChannelsOut);
		btnsChannelsIn.clear();
		btnsChannelsOut.clear();

		int in = 0, out = 0;
		
		for (ExtenderChannel chn : extender.getChannelsIn()) {
			GuiButton btn = null;
			ChannelButton.TYPES typ = ChannelButton.TYPES.INACTIVE;
			if (chn.getFunction().getId() > 0)
				typ = ChannelButton.TYPES.OFF;
			if (chn.getSignal().equals(Signal.ON))
				typ = ChannelButton.TYPES.ON;
			if (chn == edit)
				typ = ChannelButton.TYPES.EDIT;
			btn = new ChannelButton(1, guiLeft + screenX, guiTop + screenY
					+ 10 + in * 3, typ, chn.getNumber() , false);
			btnsChannelsIn.add(btn);
			controlList.add(btn);
			in++;
		}
		for (ExtenderChannel chn : extender.getChannelsOut()) {
			GuiButton btn = null;
			ChannelButton.TYPES typ = ChannelButton.TYPES.INACTIVE;
			if (chn.getFunction().getId() > 0)
				typ = ChannelButton.TYPES.OFF;
			if (chn.getSignal().equals(Signal.ON))
				typ = ChannelButton.TYPES.ON;
			if (chn == edit)
				typ = ChannelButton.TYPES.EDIT;
			btn = new ChannelButton(1, guiLeft + screenX + 136, guiTop + screenY
						+ 10 + out * 3, typ, chn.getNumber(), true);
			
			btnsChannelsOut.add(btn);
			controlList.add(btn);
			out++;
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
		if (nextSubGui != null) {
			subGui = nextSubGui;
			if (subGui != null)
				subGui.onOpen();
			nextSubGui = null;
			
		}
		
		
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
			
			if (btnsChannelsIn.contains(button)) {
				edit = extender.getChannelsIn().get(idx);	
			} else {
				edit = extender.getChannelsOut().get(idx);
			}
			setSubGui(new SubGuiChannel(edit, this));
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
			
		}
		this.nextSubGui = subGui;
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