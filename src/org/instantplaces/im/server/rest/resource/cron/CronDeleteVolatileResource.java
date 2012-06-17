package org.instantplaces.im.server.rest.resource.cron;

import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.WidgetDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;

public class CronDeleteVolatileResource extends ServerResource {
	/*
	 * Applications that don't communicate over OLD minutes will have all their
	 * volatile widgets deleted.
	 */
	private static final long INACTIVE = 5 * 60 * 1000; // milliseconds, 5
														// minutes

	@Override
	public void doInit() {
		Log.get().debug("Cron: deleting volatile widgets from inactive applications.");
	}

	@Get
	public Representation runCron() {
		long current = System.currentTimeMillis();

		

		List<Key<PlaceDao>> placeKeys = Dao.getPlaceKeys();

		

		for (Key<PlaceDao> placeKey : placeKeys) {
			Dao.beginTransaction();
			List<ApplicationDao> applications = Dao.getApplications(placeKey);

			ArrayList<WidgetDao> widgetsToDelete = new ArrayList<WidgetDao>();
			ArrayList<Key<WidgetOptionDao>> widgetOptionsToDelete = new ArrayList<Key<WidgetOptionDao>>();
			ArrayList<Key<WidgetInputDao>> widgetInputsToDelete = new ArrayList<Key<WidgetInputDao>>();
			
			for (ApplicationDao app : applications) {
				Log.get().debug("Deleting widgets from application: " + app.getApplicationId());
				if ( (current - app.getLastRequestTimestamp()) > INACTIVE ) {

					List<WidgetDao> widgets = Dao.getWidgets(placeKey, app.getApplicationId());

					for (WidgetDao widget : widgets) {
						if (widget.isVolatileWidget()) {
							widgetsToDelete.add(widget);

							widgetOptionsToDelete.addAll(Dao.getWidgetOptionsKeys(widget.getKey()));

							widgetInputsToDelete.addAll(Dao.getWidgetInputsKeys(widget.getKey()));
						}
					}

				}
			}
			Dao.delete(widgetsToDelete);
			Dao.delete(widgetOptionsToDelete);
			Dao.delete(widgetInputsToDelete);

			Dao.commitOrRollbackTransaction();
		}
		
		return null;

	}

}
