package org.instantplaces.interactionmanager.server;

import org.apache.log4j.Logger;

public class Log {
	private static Logger logger;
	

	public static Logger get() {
		return Logger.getLogger("IM");
	}
	
	public static Logger getDSO() {
		return Logger.getLogger("IM.DSO");
	}
	public static Logger getREST() {
		return Logger.getLogger("IM.REST");
	}
}
