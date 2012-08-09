/**
 * 
 */
package org.instantplaces.im.server.rest.resource.cron;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDaoTmp;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;

/**
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class CronDeleteOldInput extends ServerResource {
	/*
	 * Applications that don't communicate over OLD milliseconds will be deleted
	 */
	private static final long OLD = 3 * 24 * 60 * 60 * 1000; // milliseconds, 3
																// days

	@Override
	public void doInit() {
		Log.get().debug("Cron: deleting old input.");
	}

	@Get
	public Representation runCron() {
		long current = System.currentTimeMillis();

		

		List<Key<ApplicationDao>> applicationKeys = Dao.getApplicationKeys();

		for (Key<ApplicationDao> applicationKey : applicationKeys) {
			
			Dao.beginTransaction();
			List<WidgetInputDao> widgetInputList = Dao.getWidgetInputs(applicationKey);
			
			for ( WidgetInputDao widgetInput : widgetInputList ) {
				if ( (current - widgetInput.getTimeStamp()) > OLD ) {
					Log.get().info("Deleting old input: " + widgetInput.getTimeStamp() );
					Dao.delete(widgetInput);
				}
			}
			
			Dao.commitOrRollbackTransaction();
		}
		

		return null;
	}

}
