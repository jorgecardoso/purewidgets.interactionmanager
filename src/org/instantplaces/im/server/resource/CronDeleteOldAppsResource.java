/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.Dao;
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

		Dao.beginTransaction();

		List<Key<PlaceDao>> placeKeys = Dao.getPlaceKeys();

		for (Key<PlaceDao> placeKey : placeKeys) {
			List<ApplicationDao> applications = Dao.getApplications(placeKey);

			for (ApplicationDao app : applications) {
				if ((current - app.getLastRequestTimestamp()) > OLD) {
					Dao.delete(app);
					Dao.delete(Dao.getWidgetsKeys(app.getKey()));
					Dao.delete(Dao.getWidgetOptionsKeys(app.getKey()));
					Dao.delete(Dao.getWidgetInputsKeys(app.getKey()));
				}
			}
		}

		return null;
	}

}
