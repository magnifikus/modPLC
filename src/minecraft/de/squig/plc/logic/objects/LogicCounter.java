package de.squig.plc.logic.objects;

import java.util.ArrayList;
import java.util.List;

import de.squig.plc.logic.Circuit;
import de.squig.plc.logic.Signal;
import de.squig.plc.logic.elements.functions.ElementFunction;
import de.squig.plc.logic.helper.LogHelper;
import de.squig.plc.logic.objects.guiFunctions.GuiFunction;
import de.squig.plc.logic.objects.guiFunctions.GuiFunctionIntDisplay;
import de.squig.plc.logic.objects.guiFunctions.GuiFunctionIntValue;

public class LogicCounter extends CircuitObject implements
		ICircuitObjectInputPinListener {
	public static List<Class> dataTypes = new ArrayList<Class>() {
		{
			add(Short.class); // value
			add(Short.class); // low
			add(Short.class); // mid
			add(Short.class); // high
			add(Short.class); // incV
			add(Short.class); // decV
		}
	};

	private static List<GuiFunction> guiFunctions = new ArrayList<GuiFunction>() {
		{
			add(new GuiFunctionIntDisplay((short) dataMap.VALUE.ordinal(), "Counter Value"));
			add(new GuiFunctionIntValue((short) dataMap.LOW.ordinal(),
					"Low Value", -32768, 32767));
			//add(new GuiFunctionIntValue((short) dataMap.MID.ordinal(),
			//		"Mid Value", -32768, 32768));
			add(new GuiFunctionIntValue((short) dataMap.HIGH.ordinal(),
					"High Value", -32768, 32767));
			add(new GuiFunctionIntValue((short) dataMap.INC.ordinal(),
					"Increment", 0, 32767));
			add(new GuiFunctionIntValue((short) dataMap.DEC.ordinal(),
					"Decrement", 0, 32767));
		}
	};

	private enum dataMap {
		VALUE, LOW, MID, HIGH, INC, DEC
	};

	protected CircuitObjectOutputPin outTop = new CircuitObjectOutputPin(this,
			"Maximum reached");
	protected CircuitObjectOutputPin outBottom = new CircuitObjectOutputPin(
			this, "Minumum reached");
	protected CircuitObjectOutputPin outMid = new CircuitObjectOutputPin(this,
			"Median reached");
	protected CircuitObjectInputPin inCountUp = new CircuitObjectInputPin(this,
			"Count up");
	protected CircuitObjectInputPin inCountDown = new CircuitObjectInputPin(
			this, "Count down");
	protected CircuitObjectInputPin inReset = new CircuitObjectInputPin(this,
			"Reset Counter");
	protected CircuitObjectInputPin inStop = new CircuitObjectInputPin(this,
			"Stop Counting");

	public LogicCounter(Circuit circuit, short linkNumber) {
		super(circuit, dataTypes);
		addOutputPin(outTop);
		addOutputPin(outMid);
		addOutputPin(outBottom);
		addInputPin(inCountUp);
		addInputPin(inCountDown);
		addInputPin(inReset);
		addInputPin(inStop);
		inCountUp.setListener(this);
		inCountDown.setListener(this);
		inReset.setListener(this);
		
		setGuiFunctions(guiFunctions);
		setLinkNumber(linkNumber);
		name = "Internal Counter";

		setValue((short) 0);
		setLow((short) 0);
		setMid((short) 5);
		setHigh((short) 10);
		setIncrement((short) 1);
		setDecrement((short) 1);
	}

	

	public CircuitObjectInputPin getInputPin(ElementFunction funct) {
		if (ElementFunction.COUNTERCOUNTUP == funct)
			return inCountUp;
		if (ElementFunction.COUNTERCOUNTDOWN == funct)
			return inCountDown;
		if (ElementFunction.COUNTERRESET == funct)
			return inReset;
		if (ElementFunction.COUNTERSTOP == funct)
			return inStop;
		return null;
	}

	public CircuitObjectOutputPin getOutputPin(ElementFunction funct) {
		if (ElementFunction.COUNTERTOP == funct)
			return outTop;
		if (ElementFunction.COUNTERBOTTOM == funct)
			return outBottom;
		return null;
	}

	@Override
	public void onSignal(CircuitObjectInputPin pin, Signal signal) {
		boolean lastUp = (getFlags() & 16384) == 16384;
		boolean lastDown = (getFlags() & 8192) == 8192;
		boolean stop = inStop.getSignal().equals(Signal.ON);
		boolean reset = inReset.getSignal().equals(Signal.ON);
		boolean valueupdate = false;
		



		if (reset) {
			if (getValue() != 0) {
				setValue((short) 0);
				valueupdate = true;
			}
		}
		if (pin == inCountUp) {
			if ((signal.equals(Signal.OFF)  && lastUp ) || signal
					.equals(Signal.NEGATIVEPULSE)) {
				setFlags((short) (getFlags() - 16384));
				return;
			}
			if ((signal.equals(Signal.ON)  && !lastUp ) || signal.equals(Signal.PULSE)) {
				if (!signal.equals(Signal.PULSE))
					setFlags((short) (getFlags() | 16384));

				if (!reset && !stop) {
					short newValue = (short) (getValue() + getIncrement());
					if (newValue < getValue())
						setValue((short) 32767);
					else if (newValue > getHigh())
						setValue(getHigh());
					else
						setValue(newValue);
					valueupdate = true;
				}
			}
		} else if (pin == inCountDown) {
			if ((signal.equals(Signal.OFF)  && lastDown) || signal
					.equals(Signal.NEGATIVEPULSE)) {
				setFlags((short) (getFlags() - 8192));
				return;
			}
			if ((signal.equals(Signal.ON) && !lastDown) || signal.equals(Signal.PULSE)) {
				if (!signal.equals(Signal.PULSE))
					setFlags((short) (getFlags() | 8192));

				if (!reset && !stop) {
					short newValue = (short) (getValue() - getDecrement());
					if (newValue > getValue())
						setValue((short) -32768);
					else if (newValue < getLow())
						setValue(getLow());
					else
						setValue(newValue);
					valueupdate = true;
				}
			}
		}
		
		if (valueupdate) {
			short value = getValue();
			if (value <= getLow())
				outBottom.onSignal(Signal.ON);
			else outBottom.onSignal(Signal.OFF);
			if (value >= getHigh())
				outTop.onSignal(Signal.ON);
			else outTop.onSignal(Signal.OFF);
			setChanged(true);
		}
	}

	public short getValue() {
		return (Short) objData.get(dataMap.VALUE.ordinal());
	}

	public short getLow() {
		return (Short) objData.get(dataMap.LOW.ordinal());
	}

	public short getMid() {
		return (Short) objData.get(dataMap.MID.ordinal());
	}

	public short getHigh() {
		return (Short) objData.get(dataMap.HIGH.ordinal());
	}

	public short getIncrement() {
		return (Short) objData.get(dataMap.INC.ordinal());
	}

	public short getDecrement() {
		return (Short) objData.get(dataMap.DEC.ordinal());
	}

	public void setValue(short value) {
		objData.set(dataMap.VALUE.ordinal(), value);
	}

	public void setLow(short value) {
		objData.set(dataMap.LOW.ordinal(), value);
	}

	public void setMid(short value) {
		objData.set(dataMap.MID.ordinal(), value);
	}

	public void setHigh(short value) {
		objData.set(dataMap.HIGH.ordinal(), value);
	}

	public void setIncrement(short value) {
		objData.set(dataMap.INC.ordinal(), value);
	}

	public void setDecrement(short value) {
		objData.set(dataMap.DEC.ordinal(), value);
	}

}
