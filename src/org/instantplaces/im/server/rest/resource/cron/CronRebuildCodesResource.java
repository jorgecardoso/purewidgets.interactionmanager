/**
 * 
 */
package org.instantplaces.im.server.rest.resource.cron;

import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.DaoTmp;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetOptionDao;
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

		
		List<Key<PlaceDao>> placeKeys = DaoTmp.getPlaceKeys();

		for (Key<PlaceDao> placeKey : placeKeys) {
			DaoTmp.beginTransaction();
			
			ReferenceCodeGeneratorDAO rcg = DaoTmp.getReferenceCodeGenerator(placeKey);

			rcg.rebuild();
			List<WidgetOptionDao> options = DaoTmp.getWidgetOptions(placeKey);

			for (WidgetOptionDao option : options) {
				rcg.remove(option.getReferenceCode());
				Log.get().debug("Removing used code: " + option.getReferenceCode());
			}
			
			DaoTmp.commitOrRollbackTransaction();
		}

		

		return null;
	}

}
