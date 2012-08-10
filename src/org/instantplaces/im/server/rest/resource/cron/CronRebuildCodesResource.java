/**
 * 
 */
package org.instantplaces.im.server.rest.resource.cron;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDaot;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetOptionDaot;
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

		
		List<Key<PlaceDaot>> placeKeys = Dao.getPlaceKeys();

		for (Key<PlaceDaot> placeKey : placeKeys) {
			Dao.beginTransaction();
			
			ReferenceCodeGeneratorDAO rcg = Dao.getReferenceCodeGenerator(placeKey);

			rcg.rebuild();
			List<WidgetOptionDaot> options = Dao.getWidgetOptions(placeKey);

			for (WidgetOptionDaot option : options) {
				rcg.remove(option.getReferenceCode());
				Log.get().debug("Removing used code: " + option.getReferenceCode());
			}
			
			Dao.commitOrRollbackTransaction();
		}

		

		return null;
	}

}
