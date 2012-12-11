package de.squig.plc.client.gui.controller;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.Player;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.CircuitMap;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.Counter;
import de.squig.plc.logic.elements.Deleted;
import de.squig.plc.logic.elements.High;
import de.squig.plc.logic.elements.Input;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.elements.Not;
import de.squig.plc.logic.elements.Output;
import de.squig.plc.logic.elements.Pulse;
import de.squig.plc.logic.elements.Timer;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.network.PacketControllerData;
import de.squig.plc.tile.TileController;

public class GuiController extends GuiScreen {

	private TileController controller;

	public static String tutorialString = "Hello World";

	private int xSize = 312;
	private int ySize = 192;
	private int screenX = 0;
	private int screenY = 0;
	private int screenRows = 10;
	private int screenCols = 8;
	
	private int cursorX = 0;
	private int cursorY = 0;
	
	private int yOffset = 0;
	
	
	private List<DisplayTile> displayTiles = new ArrayList<DisplayTile>();
	private Circuit circuit;
	
	private GuiTextField txtName = null;
	private String loadedName = null;
	
	public GuiController(TileController controller) {
		super();
		// super(new ContainerController(player, controller));
		// this.ySize = 176;
		this.controller = controller;
		this.circuit = controller.getCircuit();
	}

	public void initGui() {
		controlList.clear();
		screenX = (this.width - xSize) / 2;
		screenY = (this.height - ySize) / 2;
		
		txtName = new GuiTextField(fontRenderer, screenX+163, screenY+20, 100, 16);
		loadedName = controller.getControllerName();
		txtName.setText(loadedName);
		txtName.setMaxStringLength(14);
		
	}
	
	public boolean doesGuiPauseGame()
	{
			return false;
	}

	public void drawBackground(int i) {

	}

	public void drawScreen(int i, int j, float f) {
		if (!controller.getControllerName().equals(loadedName)) {
			loadedName = controller.getControllerName();
			txtName.setText(loadedName);
		}
			
		displayTiles.clear();
		
		
		drawControllerScreen();

		CircuitMap map = circuit.getMap();
		int rows = map.getHeight();
		if (rows > screenRows)
			rows = screenRows;
		
		// First run draw textures
		for (int x = 0; x < map.getWidth(); x++)
			for (int y = 0; y < rows; y++) {
				CircuitElement element = map.getElementAt(x, y+yOffset);
				if (element != null) {
					drawLogicTile(element.getTexture(), x, y,
							element.isPowered(), null,(element.getInputPin() != null), element.isInpowered());
					for (LogicTextureTile tile : element.getTags())
						drawLogicTile(tile, x, y, element.isPowered(), null, false,false);
				}
			}
		// print the cursor
		drawLogicTile(LogicTextureTile.CURSOR, cursorX, cursorY-yOffset, false, null,false,false);
		
		// paint the rest background
		
		drawControls();
		
		

		// Second run draw Text
		txtName.drawTextBox();
		
		for (int x = 0; x < map.getWidth(); x++)
			for (int y = 0; y < rows; y++) {
				CircuitElement element = map.getElementAt(x, y+yOffset);
				if (element != null && element.getLinkNumber() != null && element.isDisplayLink()) {
					drawLogicTile(element.getTexture(), x, y,
							element.isPowered(), element.getLinkNumber(), (element.getInputPin() != null), element.isInpowered());
				}
			}
		this.fontRenderer.drawString("R"+(cursorY+1)+" C"+(cursorX+1), screenX+8, screenY + 8, 0x000000);
		
		updateInfoPanel();
		super.drawScreen(i, j, f);
	}

	private void drawLogicTile(LogicTextureTile tile, int x, int y, boolean on,
			String text, boolean hasInput, boolean inputPowered) {
		int sx = screenX + 16 * (x + 1);
		int sy = screenY + 16 * (y + 1);

		if (text != null) {
			if (!on)
				this.fontRenderer.drawString(text, sx + 2, sy + 8, 0x000000);
			else this.fontRenderer.drawString(text, sx + 2, sy + 8, 0x990000);
			return;
		}

		int var4 = this.mc.renderEngine
				.getTexture("/ressources/art/gui/controller.png");
		this.mc.renderEngine.bindTexture(var4);
		
		if (tile == null)
			return;
		
		if (!on) {
			this.drawTexturedModalRect(sx, sy, tile.x, tile.y, 16, 16);
		} else {
			this.drawTexturedModalRect(sx, sy, tile.xon, tile.yon, 16, 16);
		}
		
		
	}
	
	
	private void drawControls() {
		int screenwidth = (screenCols + 2) * 16;
		int xStart = screenX + screenwidth;
		int xEnd = screenX + xSize;
		
		int colorRun = 0xdddddd;
		int colorEdit = 0xdddddd;
		int colorStop = 0xdddddd;
		if (controller.getState().equals(TileController.STATES.EDIT))
			colorEdit = 0x777777;
		
		if (controller.getState().equals(TileController.STATES.RUN))
			colorRun = 0x777777;
		
		if (controller.getState().equals(TileController.STATES.STOP) || controller.getState().equals(TileController.STATES.ERROR))
			colorStop = 0x777777;
		
		this.fontRenderer.drawString("EDIT", xStart+6, screenY + 5, colorEdit);
		this.fontRenderer.drawString("RUN", xStart+6+32 + 2, screenY + 5, colorRun);
		this.fontRenderer.drawString("STOP", xStart+4+64 + 2, screenY + 5, colorStop);
		//this.fontRenderer.drawString("Name", xStart+2+112 + 2, screenY + 9 + 16, 0xdddddd);
		
		// state buttons
		displayTiles.add(new DisplayTile(screenCols, 0, xStart, screenY, 32, 16));
		displayTiles.add(new DisplayTile(screenCols+1, 0, xStart+32, screenY, 32, 16));
		displayTiles.add(new DisplayTile(screenCols+2, 0, xStart+64, screenY, 32, 16));
		// rename button
		//displayTiles.add(new DisplayTile(screenCols+1, 1, xStart+112, screenY+16+4, 32, 16));
		
		
		if (controller.getState().equals(TileController.STATES.ERROR))
			this.fontRenderer.drawString("ERROR", xStart+110, screenY + 4, 0xff0000);
		else if (controller.getState().equals(TileController.STATES.RUN))
			this.fontRenderer.drawString("RUN", xStart+110, screenY + 4, 0x00ff00);
		else if (controller.getState().equals(TileController.STATES.EDIT))
			this.fontRenderer.drawString("EDIT", xStart+110, screenY + 4, 0x000000);
		else if (controller.getState().equals(TileController.STATES.STOP))
			this.fontRenderer.drawString("STOP", xStart+110, screenY + 4, 0xFF4500);
		
	}

	private void drawControllerScreen() {

		int var4 = this.mc.renderEngine
				.getTexture("/ressources/art/gui/controller.png");
		this.mc.renderEngine.bindTexture(var4);
		// GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// Draw Screen
		for (int x = 0; x < screenCols + 2; x++) {
			for (int y = 0; y < screenRows + 2; y++) {
				
				int tx = 16;
				int ty = 16;
				
				if (x == 0)
					tx = 0;
				if (y == 0)
					ty = 0;
				if (x == screenCols + 1)
					tx = 32;
				if (y == screenRows + 1)
					ty = 32;
				int sx = screenX + 16 * x;
				int sy = screenY + 16 * y;
				if (ty == 16 && tx == 16) {
					displayTiles.add(new DisplayTile(x-1, y-1, sx, sy, 16, 16));
				}
				this.drawTexturedModalRect(sx, sy,
						tx, ty, 16, 16);
			}
		}
		
		
		int screenwidth = (screenCols + 2) * 16;
		int xStart = screenX + screenwidth;
		int xEnd = screenX + xSize;
		
		
		// Draw rightpane
		for (int x = xStart; x < xEnd; x += 16) {
			for (int y = screenY; y < screenY + ySize; y += 16) {
				this.drawTexturedModalRect(x, y, 48, 0, 16, 16);
			}
		}
		
		// draw helpdisplay
		for (int x = screenCols+2; x < screenCols + 12; x++) {
			for (int y = 2; y < screenRows + 2; y++) {
				int tx = 16;
				int ty = 16;
				if (x == screenCols+2)
					tx = 0;
				if (y == 2)
					ty = 0;
				if (x == screenCols + 11)
					tx = 32;
				if (y == screenRows + 1)
					ty = 32;
				int sx = screenX + 16 * x;
				int sy = screenY + 16 * y;
				
				if (tx == 16 && ty >= 16)
					tx = tx+48;
				if (tx == 32 && ty >= 16)
					tx = tx+48;
					
				
				this.drawTexturedModalRect(sx, sy,
						tx, ty, 16, 16);
			}
		}

		
		// draw buttons
		this.drawTexturedModalRect(xStart, screenY, 64, 0, 32, 16);
		this.drawTexturedModalRect(xStart+32, screenY, 64, 0, 32, 16);
		this.drawTexturedModalRect(xStart+64, screenY, 64, 0, 32, 16);
		
		//this.drawTexturedModalRect(xStart+112, screenY+16+4, 64, 0, 32, 16);
		
		// draw lamp
		if (controller.getState().equals(TileController.STATES.ERROR))
			this.drawTexturedModalRect(xStart+102, screenY+6, 128, 0, 5, 5);	
		else if (controller.getState().equals(TileController.STATES.RUN))
			this.drawTexturedModalRect(xStart+102, screenY+6, 128, 6, 5, 5);
		else if (controller.getState().equals(TileController.STATES.EDIT))
			this.drawTexturedModalRect(xStart+102, screenY+6, 133, 0, 5, 5);
		else if (controller.getState().equals(TileController.STATES.STOP))
			this.drawTexturedModalRect(xStart+102, screenY+6, 133, 0, 5, 5);
		
	}
	
	
	public void updateInfoPanel() {
		int startX = screenX + (screenCols+2)*16+4;
		int startY = screenY + 34;
		CircuitElement element = getSelectedElement();
		
		String name = "";
		int y = startY+4;
		if (element != null) {
			this.fontRenderer.drawString("Type: "+element.getName(), startX+8, y += 10 , 0x000000);
			if (element.getLinkedObject() != null) {
				this.fontRenderer.drawString("Link to: "+element.getLinkedObject().getName(), startX+8, y += 10 , 0x000000);
				this.fontRenderer.drawString("Link to Nr: "+element.getLinkedObject().getLinkNumber(), startX+8, y += 10 , 0x000000);
			}
			
			if (element.getInputPin() != null)
				this.fontRenderer.drawString("Input is: "+element.getInputPin().getName(), startX+8, y += 10 , 0x000000);
			if (element.getOutputPin() != null)
				this.fontRenderer.drawString("Output is: "+element.getOutputPin().getName(), startX+8, y += 10 , 0x000000);
			
			
			
		}
		
	}

	public void actionPerformed(GuiButton button) {
		

	}
	
	protected void tileClicked (int x, int y, int button) {
		
		if (x < screenCols && y < screenRows) {
			cursorX = x;
			cursorY = y + yOffset;
		} else {
			if (y == 0) {
				if (x == screenCols) {
					// Edit button
					controller.setState(TileController.STATES.EDIT);
				}
				else if (x == screenCols +1) {
					// Run button
					controller.setState(TileController.STATES.RUN);
					
				}
				else if (x == screenCols +2) {
					// Stop button
					controller.setState(TileController.STATES.STOP);
				}
			}
		
		}
	}
	
	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
			txtName.mouseClicked(x, y, mouseButton);
			boolean found = false;
			for (DisplayTile dt : displayTiles) {
				if (dt.checkClicked(x, y)) {
					tileClicked(dt.getGridx(), dt.getGridy(), mouseButton);
				}
			}
			if (!found)
				super.mouseClicked(x, y, mouseButton);
			
	}		
	
	
	
	@Override
	 protected void keyTyped(char par1, int par2)
    {
		if (par1 == 27)
			super.keyTyped(par1,par2);
		
		if (txtName.isFocused()) {
				txtName.textboxKeyTyped(par1, par2);
				controller.setControllerName(txtName.getText());
				sendUpdate(false);
			return;
		}
		boolean handled = false;
		
		switch (par2) {
			case 200:
				if (cursorY > 0)
					cursorY--;
				checkScroll();
				break;
			case 208:
				if (cursorY < circuit.getMap().getHeight()-1)
					cursorY++;
				checkScroll();
				break;
			case 203:
				if (cursorX > 0)
					cursorX--;
				break;
			case 205:
				if (cursorX < circuit.getMap().getWidth()-1)
					cursorX++;
				break;
			case 57:
				keySpaceEvent();
				break;
			case 14:
				keyBackspaceEvent();
				break;
			case 42:
				keyShiftEvent();
				break;
			default:
				if (!keyEvent(par1))
					super.keyTyped(par1, par2);
		}
    }
	
	private boolean keyEvent(char cin) {
		char c = Character.toUpperCase(cin);
		CircuitElement element = getSelectedElement();
		
		if (c >= '0' &&  c <= '9' && element != null) {
			element.tryIdInput(cin);
			sendUpdate(false);
			return true;
		}
		
		switch (c) {
			case 'I':
				tryConvert(Input.class, element);
				break;
			case 'T':
				tryConvert(Timer.class, element);
				break;
			case 'H':
				tryConvert(High.class, element);
				break;
			case 'N':
				tryConvert(Not.class, element);
				break;
			case 'C':
				tryConvert(Counter.class, element);
				break;
			case 'P':
				tryConvert(Pulse.class, element);
				break;
			case 'O':
				tryConvert(Output.class, element);
				break;
			default:
				return false;
		}
		
		
		return true;
	}
	
	private CircuitElement getSelectedElement() {
		return circuit.getMap().getElementAt(cursorX, cursorY);
	}
	 
	private void tryConvert(Class target, CircuitElement element) {
		
		int x = cursorX;
		int y = cursorY;
		
		if (target.equals(Input.class)) {
			element = new Input(circuit,x,y);	
		}
		if (target.equals(Output.class)) {
			element = new Output(circuit,x,y);	
		}
		if (target.equals(Timer.class)) {
			element = new Timer(circuit,x,y);	
		}
		if (target.equals(Pulse.class)) {
			element = new Pulse(circuit,x,y);	
		}
		if (target.equals(Not.class)) {
			element = new Not(circuit,x,y);	
		}
		if (target.equals(Counter.class)) {
			element = new Counter(circuit,x,y);	
		}
		if (target.equals(High.class)) {
			element = new High(circuit,x,y);	
		}
		
		if (element != null) {
			circuit.getMap().addElement(element, -1, -1);
			element.tryIdInput('0');
			sendUpdate(false);
		}
	}
	
	
	private void keySpaceEvent() {
		CircuitElement element = circuit.getMap().getElementAt(cursorX, cursorY);
		if (element == null) {
			element = new Line(circuit, cursorX,cursorY);
			circuit.getMap().addElement(element, -1, -1);
		} else {
			element.functionCycle();
		}
		sendUpdate(false);
		
	}
	

	private void keyShiftEvent() {
		CircuitElement element = circuit.getMap().getElementAt(cursorX, cursorY);
		if (element != null) {
			element.tryInvert();
		}
		sendUpdate(false);
	}
	
	private void keyBackspaceEvent() {
		CircuitElement element = circuit.getMap().getElementAt(cursorX, cursorY);
		if (element != null) {
			circuit.getMap().removeElement(element);
			CircuitElement del = new Deleted(circuit, cursorX, cursorY);
			circuit.getMap().addElement(del,-1,-1);
			sendUpdate(false);
			circuit.getMap().removeElement(del);
		}
	}
	
	private void sendUpdate(boolean all) {
		PacketControllerData.sendElements(controller,  FMLClientHandler.instance().getClient().thePlayer, all);
	}
	private void checkScroll() {
		
		if (cursorY < yOffset+1)
			yOffset = cursorY;
		if (cursorY+1 > yOffset+screenRows-1)
			yOffset = cursorY-screenRows+1;
	}
	
	/*
	 * protected void drawGuiContainerForegroundLayer() {
	 * this.fontRenderer.drawString
	 * (LanguageRegistry.instance().getStringLocalization("Controller"), 60, 6,
	 * 4210752); this.fontRenderer.drawString(StatCollector.translateToLocal(
	 * "container.inventory"), 8, this.height - 96 + 2, 4210752); }
	 * 
	 * protected void drawGuiContainerBackgroundLayer(float par1, int par2, int
	 * par3) {
	 * 
	 * 
	 * int var4 =
	 * this.mc.renderEngine.getTexture("/ressources/art/gui/controller.png");
	 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	 * this.mc.renderEngine.bindTexture(var4); int var5 = (this.width -
	 * this.width) / 2; int var6 = (this.height - this.height) / 2;
	 * this.drawTexturedModalRect(var5, var6, 0, 0, this.width, this.height);
	 * int var7;
	 * 
	 * /* This bit shows the "fire" effect in the GUI if
	 * (this.furnaceInventory.isBurning()) { var7 =
	 * this.furnaceInventory.getBurnTimeRemainingScaled(12);
	 * this.drawTexturedModalRect(var5 + 56, var6 + 36 + 12 - var7, 176, 12 -
	 * var7, 14, var7 + 2); }
	 * 
	 * This bit shows the progress bar in the GUI var7 =
	 * this.furnaceInventory.getCookProgressScaled(24);
	 * this.drawTexturedModalRect(var5 + 79, var6 + 34, 176, 14, var7 + 1, 16);
	 */
	// }

}