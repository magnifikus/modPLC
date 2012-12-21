package de.squig.plc.client.gui.extender;

import net.minecraft.client.gui.GuiButton;
import de.squig.plc.client.gui.SubGui;
import de.squig.plc.client.gui.controlls.TouchButton;
import de.squig.plc.event.SearchResponseEvent;
import de.squig.plc.tile.TileController;

public class SubGuiFront implements SubGui {
	private enum State {connected, search};
	private State state;
	private State nextState = null;
	private GuiExtender guiExtender;
	
	private TileController searchRes = null;
	private int searchidx = 0;
	
	GuiButton btnNext = null;
	GuiButton btnBack = null;
	GuiButton btnConnect = null;
	GuiButton btnDisconnect = null;
	
	public SubGuiFront( GuiExtender guiExtender) {
		super();
		this.guiExtender = guiExtender;
		
		btnNext = guiExtender.subTouchButton(1, 120, 35, TouchButton.TYPES.RIGHT);
		btnBack = guiExtender.subTouchButton(1, 10, 35, TouchButton.TYPES.LEFT);
		btnConnect = guiExtender.subTextButton(1, 80, 70, "Connect", false);
		btnDisconnect = guiExtender.subTextButton(1, 80, 70, "Disconnect", false);
		
	}

	
	
	@Override
	public void onClose() {
		onState(null);
	}

	@Override
	public void onOpen() {
		if (guiExtender.extender.getConnectedController() != null)
			onState(State.connected);
		else onState(State.search);
	}

	@Override
	public void renderBackground() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderForeground() {
		if (nextState != null) {
			onState(nextState);
			nextState = null;
		}
		if (state.equals(State.connected)) {
		
			guiExtender.subDrawText("Link: "+guiExtender.extender.getConnectedControllerName(), 15, 0, 0x000000);
		
			//guiExtender.subDrawText("State:  RUN ", 15, 15, 0x000000);
			//guiExtender.subDrawText("STOP", 80, 15, 0xFF0000);
			
			guiExtender.subDrawText("< Inputs          Outputs >", 5, 35, 0x000000);
			guiExtender.subDrawText("click one to configure", 15, 50, 0x000000);
			
			
		} else {
			guiExtender.subDrawText("Not connected", 15, 0, 0x000000);
			guiExtender.subDrawText("Controllers in range:", 15, 20, 0x000000);
			
			if (guiExtender.extender.getControllerInRange().size() > searchidx) {
				SearchResponseEvent resp = guiExtender.extender.getControllerInRange().get(searchidx);
				guiExtender.subDrawText(resp.getName(), 30, 35, 0x000000);
				guiExtender.subDrawText("X: "+resp.getSource().xCoord, 5, 50, 0x000000);
				guiExtender.subDrawText("Y: "+resp.getSource().yCoord, 5, 60, 0x000000);
				guiExtender.subDrawText("Z: "+resp.getSource().zCoord, 5, 70, 0x000000);
			} 	
			
		}
	}
	
	private void onState(State state) {
		if (this.state != state) {
			if (State.search.equals(this.state)) {
				guiExtender.removeControl(btnNext);
				guiExtender.removeControl(btnBack);
				guiExtender.removeControl(btnConnect);
			}
			if (State.search.equals(state)) {
				guiExtender.addControl(btnNext);
				guiExtender.addControl(btnBack);
				guiExtender.addControl(btnConnect);
			}
			
			if (State.connected.equals(this.state)) {
				guiExtender.removeControl(btnDisconnect);
			}
			if (State.connected.equals(state)) {
				guiExtender.addControl(btnDisconnect);
			}
			
			
			this.state = state;
		}
		
	}
	
	private void moveSearch(int mv) {
		searchidx += mv;
		if (searchidx < 0) {
			searchidx = guiExtender.extender.getControllerInRange().size()-1;
			if (searchidx < 0)
				searchidx = 0;
		}
		if (guiExtender.extender.getControllerInRange().size() <= searchidx)
			searchidx = 0;
		System.out.println(searchidx);
	}


	@Override
	public void actionPerformed(GuiButton button) {
		if (button == btnNext) 
			moveSearch(1);
		else if (button == btnBack)
			moveSearch(-1);
		
		if (button == btnConnect) {
			if (guiExtender.extender.getControllerInRange().size() > searchidx) {
				SearchResponseEvent resp = guiExtender.extender.getControllerInRange().get(searchidx);;
				guiExtender.extender.link(resp);
				nextState = State.connected;
				guiExtender.refreshChannelButtons();
				guiExtender.invokeServerUpdate();
				
			} 
		} else if (button == btnDisconnect) {
			guiExtender.extender.unlink();
			nextState = State.search;
			guiExtender.refreshChannelButtons();
			guiExtender.invokeServerUpdate();
		}
		
	}

}
