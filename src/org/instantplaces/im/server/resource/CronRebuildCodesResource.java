/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.PlaceDAO;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;

/**
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class CronRebuildCodesResource extends ServerResource {

	@Override
	public void doInit() {
		Log.get().debug("Cron: rebuilding reference codes.");
	}

	@Get
	public Representation runCron() {

		DAO.beginTransaction();
		List<Key<PlaceDAO>> placeKeys = DAO.getPlaceKeys();

		for (Key<PlaceDAO> placeKey : placeKeys) {

			ReferenceCodeGeneratorDAO rcg = DAO.getReferenceCodeGenerator(placeKey);

			rcg.rebuild();
			List<WidgetOptionDAO> options = DAO.getWidgetOptions(placeKey);

			for (WidgetOptionDAO option : options) {
				rcg.remove(option.getReferenceCode());
				Log.get().debug("Removing used code: " + option.getReferenceCode());
			}
		}

		DAO.commitOrRollbackTransaction();

		return null;
	}

}
