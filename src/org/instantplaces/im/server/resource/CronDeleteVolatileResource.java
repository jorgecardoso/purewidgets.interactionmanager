package org.instantplaces.im.server.resource;

import java.util.Iterator;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.PlaceDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;
import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class CronDeleteVolatileResource extends ServerResource {
	/*
	 * Applications that don't communicate over OLD minutes
	 * will have all their volatile widgets deleted.
	 */
	private static final long OLD = 5; // minutes
	
	@Override
	public void doInit() {
		Log.get().debug("Cron: deleting widgets from inactive applications.");
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
		    	Log.get().error("Cron: Could not finish transaction. Rolling back.");
		        tx.rollback();
		    }
		}
		pm.close();
		
		
		
		return null;
	}
	
	private void deleteOldWidgets(PersistenceManager pm) {
		
		try {
			
			Extent<PlaceDSO> extent = pm.getExtent(PlaceDSO.class);
		
			Iterator<PlaceDSO> it =  extent.iterator();
		
			long current = System.currentTimeMillis();
			while (it.hasNext()) {
				PlaceDSO place = it.next();
				
				for (ApplicationDSO app : place.getApplications()) {
					if ( current-app.getLastRequestTimestamp() > OLD*60*1000 ) {
						
						if (  app.removeVolatileWidgets() ) {
							Log.get().info("Cron: deleted volatile widgets from application " + app.getApplicationId());
						
						}
					}
				}
			}
	    } catch (Exception e) {
	    	Log.get().error("Error deleting widgets: " + e.getMessage());
	    	for ( StackTraceElement ste : e.getStackTrace() ) {
	    		Log.get().error(ste.getClassName() + " "+ ste.getMethodName() + " " + ste.getLineNumber());
	    	}
	    } 
	}
}
