package de.squig.plc.logic;

public enum Signal {
	ON, OFF, PULSE, NEGATIVEPULSE;

	public Signal getHigherSignal(Signal signal) {
		if (signal == null)
			return OFF;
		if (this.equals(signal))
			return this;
		if (signal.equals(ON) || this.equals(ON))
			return ON;
		if (this.equals(OFF))
			return signal;
		if (signal.equals(OFF))
			return this;
		if (this.equals(PULSE) && signal.equals(NEGATIVEPULSE))
			return ON;
		if (this.equals(NEGATIVEPULSE) && signal.equals(PULSE))
			return ON;
		return OFF;
	}

	public Signal getLowerSignal(Signal signal) {
		if (signal == null)
			return OFF;
		if (this.equals(OFF) || signal.equals(OFF))
			return OFF;
		if (this.equals(ON) && signal.equals(ON))
			return Signal.ON;
		if (this.equals(ON))
			return signal;
		if (signal.equals(ON))
			return this;
		if (this.equals(PULSE) && signal.equals(NEGATIVEPULSE))
			return OFF;
		if (this.equals(NEGATIVEPULSE) && signal.equals(PULSE))
			return OFF;
		return OFF;
	}

	public Signal invert() {
		if (this.equals(ON))
			return OFF;
		if (this.equals(OFF))
			return ON;
		if (this.equals(PULSE))
			return NEGATIVEPULSE;
		if (this.equals(NEGATIVEPULSE))
			return PULSE;
		return OFF;
	}

	public static Signal fromOrdinal(int ordinal) {
		switch (ordinal) {
		case 0:
			return ON;
		case 1:
			return OFF;
		case 2:
			return PULSE;
		case 3:
			return NEGATIVEPULSE;
		}
		return null;
	}

}
