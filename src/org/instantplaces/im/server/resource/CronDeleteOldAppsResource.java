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
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.PlaceDSO;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class CronDeleteOldAppsResource extends ServerResource {
	/*
	 * Applications that don't communicate over OLD milliseconds will be deleted
	 */
	private static final long OLD = 1*24*60*60*1000; // milliseconds
	
	@Override
	public void doInit() {
		Log.get().debug("Cron: deleting inactive applications.");
	}
	
	@Get
	public Representation runCron() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try
		{
		    tx.begin();
		    
		    deleteOldApps(pm);
		    
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
	
	private void deleteOldApps(PersistenceManager pm) {
//		
//		try {
//			
//			Extent<PlaceDSO> extent = pm.getExtent(PlaceDSO.class);
//		
//			Iterator<PlaceDSO> it =  extent.iterator();
//		
//			long current = System.currentTimeMillis();
//			while (it.hasNext()) {
//				PlaceDSO place = it.next();
//			
//				ArrayList<ApplicationDSO> toDelete = new ArrayList<ApplicationDSO>();
//				
//				for ( ApplicationDSO app : place.getApplications() ) {
//					if ( current-app.getLastRequestTimestamp() > OLD ) {			
//						toDelete.add(app);
//					}
//				}
//				
//				for ( ApplicationDSO app : toDelete ) {
//					String id = app.getApplicationId();
//					Log.get().info("Cron: deleting application " + id);
//					
//					if ( place.deleteApplication(app) ) {
//						Log.get().info("Cron: deleted application " + id);
//					}
//					
//				}
//				
//			}
//	    } catch (Exception e) {
//	    	Log.get().error("Error deleting old apps " + e.getMessage());
//	    	for ( StackTraceElement ste : e.getStackTrace() ) {
//	    		Log.get().error(ste.getClassName() + " "+ ste.getMethodName() + " " + ste.getLineNumber());
//	    	}
//	    } 
	}
}
