/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.PlaceDAO;
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

		DAO.beginTransaction();

		List<Key<PlaceDAO>> placeKeys = DAO.getPlaceKeys();

		for (Key<PlaceDAO> placeKey : placeKeys) {
			List<ApplicationDAO> applications = DAO.getApplications(placeKey);

			for (ApplicationDAO app : applications) {
				if ((current - app.getLastRequestTimestamp()) > OLD) {
					DAO.delete(app);
					DAO.delete(DAO.getWidgetsKeys(app.getKey()));
					DAO.delete(DAO.getWidgetOptionsKeys(app.getKey()));
					DAO.delete(DAO.getWidgetInputsKeys(app.getKey()));
				}
			}
		}

		return null;
	}

}
