/**
 * 
 */
package org.instantplaces.im.server.rest.resource.cron;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.DaoTmp;
import org.instantplaces.im.server.dao.PlaceDao;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;

/**
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class CronDeleteOldAppsResource extends ServerResource {
	/*
	 * Applications that don't communicate over OLD milliseconds will be deleted
	 */
	private static final long OLD = 1 * 24 * 60 * 60 * 1000; // milliseconds, 1
																// day

	@Override
	public void doInit() {
		Log.get().debug("Cron: deleting inactive applications.");
	}

	@Get
	public Representation runCron() {
		long current = System.currentTimeMillis();

		

		List<Key<PlaceDao>> placeKeys = DaoTmp.getPlaceKeys();

		for (Key<PlaceDao> placeKey : placeKeys) {
			DaoTmp.beginTransaction();
			List<ApplicationDao> applications = DaoTmp.getApplications(placeKey);

			for (ApplicationDao app : applications) {
				Log.get().debug("Application " + app.getApplicationId() + " is " + ((current - app.getLastRequestTimestamp())/(1000*60*60)) + " hours old.");
				if ((current - app.getLastRequestTimestamp()) > OLD) {
					Log.get().info("Deleting old application: " + app.getApplicationId());
					DaoTmp.delete(app);
					DaoTmp.delete(DaoTmp.getWidgetsKeys(app.getKey()));
					DaoTmp.delete(DaoTmp.getWidgetOptionsKeys(app.getKey()));
					DaoTmp.delete(DaoTmp.getWidgetInputsKeys(app.getKey()));
				}
			}
			DaoTmp.commitOrRollbackTransaction();
		}
		

		return null;
	}

}
