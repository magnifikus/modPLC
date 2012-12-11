package de.squig.plc.client.gui.extender;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.GuiButton;
import de.squig.plc.client.gui.SubGui;
import de.squig.plc.client.gui.controlls.TextButton;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.function.ExtenderFunction;
import de.squig.plc.logic.extender.function.RedstoneFunction;
import de.squig.plc.logic.helper.LogHelper;

public class SubGuiChannel implements SubGui {
	
	protected ExtenderChannel channel;
	protected GuiExtender guiExtender;
	
	protected List<TextButton> btnsFunct = new ArrayList<TextButton>();
	
	
	protected TextButton btnBack = null;
	
	protected SubGui subGui = null;
	
	
	protected boolean output = false;
	
	public SubGuiChannel(ExtenderChannel channel, GuiExtender guiExtender) {
		super();
		this.channel = channel;
		this.guiExtender = guiExtender;
		if (channel.getType().equals(ExtenderChannel.TYPES.OUTPUT))
			output = true;
	}

	
	
	@Override
	public void onClose() {
		if (subGui != null)
			subGui.onClose();
		
		for (TextButton btn : btnsFunct)
			guiExtender.removeControl(btn);
		btnsFunct.clear();
		guiExtender.removeControl(btnBack);
		btnBack = null;
	}

	@Override
	public void onOpen() {
		int x = 15;
		int inc = 23;
		
		
		for (ExtenderFunction fnct : ExtenderFunction.getAviavableFunction(channel)) {
			TextButton btn = guiExtender.subTextButton(1,x,15,fnct.getDisplayName(),false); x += inc;
			btnsFunct.add(btn);
			guiExtender.addControl(btn);
			
		}
		
		
		btnBack = guiExtender.subTextButton(1,110,75,"Back",false); x += inc;
		guiExtender.addControl(btnBack);
		
		updateBtns();
		updateSub();
		

	}
	
	public void updateBtns () {
		for (TextButton btn : btnsFunct) {
			if (btn.getText().equals(channel.getFunction().getDisplayName()))
				btn.setActive(true);
			else btn.setActive(false);
		}
		
	}
	public void updateSub() {
		if (channel.getFunction() instanceof RedstoneFunction) {
			if (!(subGui instanceof SubGuiRedstone))
				setSubGui(new SubGuiRedstone( channel,  guiExtender));
		} else
			setSubGui(null);
	}
	
	private void setSubGui(SubGui subGui) {
		if (subGui != this.subGui) {
			if (this.subGui != null)
				this.subGui.onClose();
			this.subGui = subGui;
			if (this.subGui != null)
				this.subGui.onOpen();
		}
	}
	
	@Override
	public void renderBackground() {
		if (subGui != null)
			subGui.renderBackground();
	}

	@Override
	public void renderForeground() {
		String typ = "Input";
		if (output)
			typ = "Output";
		
		guiExtender.subDrawText(typ+" "+channel.getNumber(), 15, 0, 0x000000);
		
		if (subGui != null)
			subGui.renderForeground();
	}



	@Override
	public void actionPerformed(GuiButton button) {
		if (button == btnBack) {
			guiExtender.guiBack();
		} else {
			if (button instanceof TextButton && btnsFunct.contains(button)) {
				String btntext = ((TextButton) button).getText();
				if (!channel.getFunction().getDisplayName().equals(btntext)) {
					for (ExtenderFunction fnct : ExtenderFunction.getAviavableFunction(channel)) {
						if (fnct.getDisplayName().equals(btntext)) {
							channel.setFunction(fnct.getId());
							updateBtns();
							updateSub();
							guiExtender.refreshChannelButtons();
							guiExtender.invokeServerUpdate();
							break;
						}
					}
				}
			}	
		}
		if (subGui != null)
			subGui.actionPerformed(button);

	}

}
