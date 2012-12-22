package de.squig.plc.client.gui.extender;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import de.squig.plc.client.gui.SubGui;
import de.squig.plc.client.gui.controlls.TextButton;
import de.squig.plc.client.gui.controlls.TouchButton;
import de.squig.plc.logic.extender.ExtenderChannel;
import de.squig.plc.logic.extender.function.ExtenderTrigger;

public class SubGuiRedstone implements SubGui {

	protected ExtenderChannel channel;
	protected GuiExtender guiExtender;

	protected List<GuiButton> btnsFunct = new ArrayList<GuiButton>();
	protected static String dirs[] = new String[] {"D","U","N","S","W","E","A"};
	protected static String dirsN[] = new String[] {"Down","Up","North","South","West","East","All"};
	protected boolean output = false;

	public SubGuiRedstone(ExtenderChannel channel, GuiExtender guiExtender) {
		super();
		this.channel = channel;
		this.guiExtender = guiExtender;
		if (channel.getType().equals(ExtenderChannel.TYPES.OUTPUT))
			output = true;
	}

	@Override
	public void onClose() {
		
		for (GuiButton btn : btnsFunct) {
			guiExtender.removeControl(btn);
		}
		btnsFunct.clear();
	}

	@Override
	public void onOpen() {
		int x = 15;
		int y = 30;
		int xInc = 16;

		for (ExtenderTrigger trg : channel.getFunction().getTriggers()) {
			if (trg.getChannelType() == null
					|| trg.getChannelType().equals(channel.getType())) {
				int txtid = trg.getTriggerId();
				if (txtid > 100)
					txtid = txtid - 128 + 8;
				btnsFunct.add(guiExtender.subTouchButton(1, x, y,
						TouchButton.TYPES.TRIGGER, txtid));
				x += xInc;
				
			}
		}
		x = 15;
		y = 65;
		if (channel.getType().equals(ExtenderChannel.TYPES.OUTPUT)) {
			for (int i = 0; i < 7; i++) {
				btnsFunct.add(guiExtender.subTextButton(1, x, y,
					dirs[i],i,false));
				x += xInc;
			}
		}
		

		for (GuiButton btn : btnsFunct)
			guiExtender.addControl(btn);
		update();
	}

	@Override
	public void renderBackground() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderForeground() {

		guiExtender.subDrawText(channel.getTrigger().getName(), 15, 50,
				0x000000);
	}

	protected void update() {
		int activeTrigger = 0;
		if (channel.getTrigger() != null)
			activeTrigger = channel.getTrigger().getTriggerId();
		for (GuiButton btn : btnsFunct) {
			if (btn instanceof TouchButton) {
				TouchButton tbtn = (TouchButton) btn;
				tbtn.setActive(tbtn.getTriggerID() == activeTrigger);
			}
			if (btn instanceof TextButton) {
				TextButton tbtn = (TextButton) btn;
				tbtn.setActive(tbtn.getData() == channel.getSide());
			}
		}

	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button instanceof TouchButton && btnsFunct.contains(button)) {
			int trgID = ((TouchButton) button).getTriggerID();
			for (ExtenderTrigger trg : channel.getFunction().getTriggers()) {
				if (trg.getTriggerId() == trgID) {
					if (!trg.equals(channel.getTrigger())) {
						channel.setTrigger(trg);
						guiExtender.invokeServerUpdate();
					}
				}
			}
		}
		if (button instanceof TextButton && btnsFunct.contains(button)) {
			TextButton tButton = (TextButton) button;
			channel.setSide(tButton.getData());
			guiExtender.invokeServerUpdate();
		}
		update();
	}

}
