package de.squig.plc.logic.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;

public class LogHelper {

	private static Logger plcLoggerServer = null;
	private static Logger plcLoggerClient = null;

	public static void init() {
		plcLoggerClient = Logger.getLogger("plcClient");
		plcLoggerClient.setParent(FMLLog.getLogger());
		plcLoggerServer = Logger.getLogger("plcServer");
		plcLoggerServer.setParent(FMLLog.getLogger());

	}

	public static void log(Level logLevel, String message) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT)
			plcLoggerClient.log(logLevel, message);
		else
			plcLoggerServer.log(logLevel, message);

	}

	public static void info(String message) {
		log(Level.INFO, message);
	}

	public static void warn(String message) {
		log(Level.WARNING, message);
	}

	public static void error(String message) {
		log(Level.SEVERE, message);
	}

}
