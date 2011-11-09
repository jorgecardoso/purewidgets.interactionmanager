/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.DsoFetcher;
import org.instantplaces.im.server.dao.PlaceDAO;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

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
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try
		{
		    tx.begin();
		    
		    ArrayList<PlaceDAO> places = DsoFetcher.getPlacesFromDSO(pm);
//		    for ( PlaceDAO place : places ) {
//		    	ReferenceCodeGeneratorDAO rcg = place.getCodeGenerator();
//		    	rcg.rebuild();
//		    	ArrayList<WidgetOptionDAO> options = DsoFetcher.getWidgetOptionFromDSO(pm, place.getPlaceId());
//		    	for ( WidgetOptionDAO option : options ) {
//		    		rcg.remove(option.getReferenceCode());
//					Log.get().debug("Removing used code: " + option.getReferenceCode());
//		    	}
//		    }
//		   
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		    	Log.get().error("Cron: Could not finish transaction. Rolling back.");
		        tx.rollback();
		    }
		}
		pm.close();
		
		
		
		return null;
	}
	
}
