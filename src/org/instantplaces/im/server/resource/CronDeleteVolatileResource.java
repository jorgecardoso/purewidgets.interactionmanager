package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.PlaceDAO;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetInputDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;
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
		Log.get().debug("Cron: deleting widgets from inactive applications.");
	}

	@Get
	public Representation runCron() {
		long current = System.currentTimeMillis();

		DAO.beginTransaction();

		List<Key<PlaceDAO>> placeKeys = DAO.getPlaceKeys();

		ArrayList<WidgetDAO> widgetsToDelete = new ArrayList<WidgetDAO>();
		ArrayList<Key<WidgetOptionDAO>> widgetOptionsToDelete = new ArrayList<Key<WidgetOptionDAO>>();
		ArrayList<Key<WidgetInputDAO>> widgetInputsToDelete = new ArrayList<Key<WidgetInputDAO>>();

		for (Key<PlaceDAO> placeKey : placeKeys) {
			List<ApplicationDAO> applications = DAO.getApplications(placeKey);

			for (ApplicationDAO app : applications) {
				Log.get().debug("Deleting widgets from application: " + app.getApplicationId());
				if ((current - app.getLastRequestTimestamp()) > INACTIVE) {

					List<WidgetDAO> widgets = DAO.getWidgets(placeKey, app.getApplicationId());

					for (WidgetDAO widget : widgets) {
						if (widget.isVolatileWidget()) {
							widgetsToDelete.add(widget);

							widgetOptionsToDelete.addAll(DAO.getWidgetOptionsKeys(widget.getKey()));

							widgetInputsToDelete.addAll(DAO.getWidgetInputsKeys(widget.getKey()));
						}
					}

				}
			}
		}
		DAO.delete(widgetsToDelete);
		DAO.delete(widgetOptionsToDelete);
		DAO.delete(widgetInputsToDelete);

		DAO.commitOrRollbackTransaction();
		return null;

	}

}
