package de.squig.plc.client.gui.controller;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import cpw.mods.fml.client.FMLClientHandler;
import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.CircuitMap;
import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.elements.Counter;
import de.squig.plc.logic.elements.Delay;
import de.squig.plc.logic.elements.Deleted;
import de.squig.plc.logic.elements.High;
import de.squig.plc.logic.elements.Input;
import de.squig.plc.logic.elements.Line;
import de.squig.plc.logic.elements.Not;
import de.squig.plc.logic.elements.Output;
import de.squig.plc.logic.elements.Pulse;
import de.squig.plc.logic.elements.Timer;
import de.squig.plc.network.PacketControllerData;
import de.squig.plc.tile.TileController;

public class GuiController extends GuiScreen {

	protected TileController controller;

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
	
	private Class[] elements = CircuitElement.getElements();
	private CircuitElement lastElement = null;
	
	private GuiInfoscreen infoScreen = null;
	protected int infoX = 0;
	protected int infoY = 0;
	protected int infoHeight = 160;
	protected int infoWidth =  160;
	
	private boolean redoInfo = true;
			
	
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
		
		txtName = new GuiTextField(fontRenderer, screenX+169, screenY+20, 100, 16);
		loadedName = controller.getControllerName();
		txtName.setText(loadedName);
		txtName.setMaxStringLength(14);
		
		infoX = screenX+(screenCols+2)*16;
		infoY = screenY+32;
		infoScreen = new GuiInfoscreen(this,getSelectedElement());
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
		
		if (redoInfo) {
			redoInfo = false;
			infoScreen.onClose();
			infoScreen = new GuiInfoscreen(this, getSelectedElement());
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
		infoScreen.drawBackground();
		
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
		
		//updateInfoPanel();
		infoScreen.drawForeground(i,j);
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
			this.drawTexturedModalRect(sx, sy, 16*(tile.txtId % 16), 16*(tile.txtId/16), 16, 16);
		} else {
			this.drawTexturedModalRect(sx, sy, 16*(tile.txtIdOn % 16), 16*(tile.txtIdOn/16), 16, 16);
		}
		
		
	}
	
	
	private void drawControls() {
		int screenwidth = (screenCols + 2) * 16;
		int xStart = screenX + screenwidth;
		int xEnd = screenX + xSize;
		/*
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
		
		// state buttons
		displayTiles.add(new DisplayTile(screenCols, 0, xStart, screenY, 32, 16));
		displayTiles.add(new DisplayTile(screenCols+1, 0, xStart+32, screenY, 32, 16));
		displayTiles.add(new DisplayTile(screenCols+2, 0, xStart+64, screenY, 32, 16));
		
		
		
		
		if (controller.getState().equals(TileController.STATES.ERROR))
			this.fontRenderer.drawString("ERROR", xStart+110, screenY + 4, 0xff0000);
		else if (controller.getState().equals(TileController.STATES.RUN))
			this.fontRenderer.drawString("RUN", xStart+110, screenY + 4, 0x00ff00);
		else if (controller.getState().equals(TileController.STATES.EDIT))
			this.fontRenderer.drawString("EDIT", xStart+110, screenY + 4, 0x000000);
		else if (controller.getState().equals(TileController.STATES.STOP))
			this.fontRenderer.drawString("STOP", xStart+110, screenY + 4, 0xFF4500);
		*/
		this.fontRenderer.drawString("PLC Controller Basic", xStart+40, screenY + 4, 0x000000);
	}

	private void drawControllerScreen() {

		int var4 = this.mc.renderEngine
				.getTexture("/ressources/art/gui/controller.png");
		this.mc.renderEngine.bindTexture(var4);
		// GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// Draw Screen
		for (int x = 0; x < 20; x++) {
			this.drawTexturedModalRect(screenX+x*16, screenY, 240, 0, 16, 192);
		}
		
		
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
		
		
	}
	
	

	public void actionPerformed(GuiButton button) {
		if (infoScreen != null)
			infoScreen.onActionPerformed(button);
	}
	
	protected void tileClicked (int x, int y, int button) {
		
		if (x < screenCols && y < screenRows) {
			cursorX = x;
			cursorY = y + yOffset;
			infoScreen.onClose();
			infoScreen = new GuiInfoscreen(this, getSelectedElement());
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
				refreshInfoScreen();
				break;
			case 208:
				if (cursorY < circuit.getMap().getHeight()-1)
					cursorY++;
				checkScroll();
				refreshInfoScreen();

				break;
			case 203:
				if (cursorX > 0)
					cursorX--;
				refreshInfoScreen();

				break;
			case 205:
				if (cursorX < circuit.getMap().getWidth()-1)
					cursorX++;
				refreshInfoScreen();
				break;
			case 57:
				keySpaceEvent();
				refreshInfoScreen();

				break;
			case 14:
				keyBackspaceEvent();
				refreshInfoScreen();
				break;
			case 42:
				keyShiftEvent();
				refreshInfoScreen();

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
			refreshInfoScreen();
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
			case 'D':
				tryConvert(Delay.class, element);
				break;
			case 'O':
				tryConvert(Output.class, element);
				break;
			default:
				return false;
		}
		
		refreshInfoScreen();

		return true;
	}
	
	protected CircuitElement getSelectedElement() {
		return circuit.getMap().getElementAt(cursorX, cursorY);
	}
	 
	protected void tryConvert(Class target, CircuitElement element) {
		
		int x = cursorX;
		int y = cursorY;
		
		if (target.equals(Input.class)) {
			element = new Input(circuit,x,y);	
		}
		else if (target.equals(Line.class)) {
			element = new Line(circuit,x,y);	
		}
		else if (target.equals(Output.class)) {
			element = new Output(circuit,x,y);	
		}
		else if (target.equals(Timer.class)) {
			element = new Timer(circuit,x,y);	
		}
		else if (target.equals(Pulse.class)) {
			element = new Pulse(circuit,x,y);	
		}
		else if (target.equals(Not.class)) {
			element = new Not(circuit,x,y);	
		}
		else if (target.equals(Counter.class)) {
			element = new Counter(circuit,x,y);	
		} else if (target.equals(Delay.class)) {
			element = new Delay(circuit,x,y);	
		}
		else if (target.equals(High.class)) {
			element = new High(circuit,x,y);	
		}
		else if (target.equals(Delay.class)) {
			element = new Delay(circuit,x,y);	
		}
		else if (target.equals(Deleted.class)) {
			element = new Deleted(circuit,x,y);	
		}
		if (element != null) {
			if (circuit.getMap().getElementAt(x, y) != null)
				circuit.getMap().removeElement(circuit.getMap().getElementAt(x, y));
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
		refreshInfoScreen();
	}
	

	private void keyShiftEvent() {
		CircuitElement element = circuit.getMap().getElementAt(cursorX, cursorY);
		if (element != null) {
			element.tryInvert();
		}
		sendUpdate(false);
		refreshInfoScreen();
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
		refreshInfoScreen();
	}
	
	protected void sendUpdate(boolean all) {
		PacketControllerData.sendElements(controller,FMLClientHandler.instance().getClient().thePlayer, all);
	}
	
	protected void refreshInfoScreen() {
		redoInfo = true;
	}
	private void checkScroll() {
		
		if (cursorY < yOffset+1)
			yOffset = cursorY;
		if (cursorY+1 > yOffset+screenRows-1)
			yOffset = cursorY-screenRows+1;
	}
	protected List<GuiButton> getControllList() {
		return controlList;
	}
	protected FontRenderer getFontRenderer() {
		return fontRenderer;
	}
	protected void drawGradientRect(int par1,int  par2,int  par3, int par4,int  par5,int  par6) {
		super.drawGradientRect(par1, par2, par3, par4, par5, par6);
	}
	

}