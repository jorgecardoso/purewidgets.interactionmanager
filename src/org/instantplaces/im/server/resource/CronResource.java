package org.instantplaces.im.server.resource;

import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.comm.InputRequest;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.PlaceDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class CronResource extends ServerResource {
	/*
	 * Applications that don't communicate over OLD minutes
	 * will have all their widgets deleted.
	 */
	private static final long OLD = 10; // minutes
	
	@Override
	public void doInit() {
		Log.get().debug("Running Cron");
	}
	
	@Get
	public Representation runCron() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try
		{
		    tx.begin();
		    
		    deleteOldWidgets(pm);
		    
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		    	Log.get().error("Could not finish transaction. Rollong back.");
		        tx.rollback();
		    }
		}
		pm.close();
		
		
		
		return null;
	}
	
	private void deleteOldWidgets(PersistenceManager pm) {
		Log.get().debug("Deleting widgets from inactive applications.");
		try {
			
			Extent<PlaceDSO> extent = pm.getExtent(PlaceDSO.class);
		
			Iterator<PlaceDSO> it =  extent.iterator();
		
			long current = System.currentTimeMillis();
			while (it.hasNext()) {
				PlaceDSO place = it.next();
				
				for (ApplicationDSO app : place.getApplications()) {
					if ( current-app.getLastRequestTimestamp() > OLD*60*1000 ) {
						//
						Log.get().debug("Will delete all widgets from application: " + app.toString());
						
						app.removeAllWidgets();
					}
				}
			}
	    } catch (Exception e) {
	    	Log.get().error("Error deleting widgets: " + e.getMessage());
	    } 
	}
}
