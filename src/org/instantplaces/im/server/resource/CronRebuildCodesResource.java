/**
 * 
 */
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
		
//		try {
//			
//			Extent<PlaceDSO> extent = pm.getExtent(PlaceDSO.class);
//		
//			Iterator<PlaceDSO> it =  extent.iterator();
//		
//			while (it.hasNext()) {
//				PlaceDSO place = it.next();
//				
//				
//				/*
//				 * sometimes codes are not recycled correctly, so rebuild the code generator from the currently used codes.
//				 */
//				
//				ReferenceCodeGenerator rcg = place.getCodeGenerator();
//				rcg.rebuild();
//				for (ApplicationDSO app : place.getApplications()) {
//					if ( null != app ) {
//						for ( WidgetDSO widget : app.getWidgets() ) {
//							
//							for ( WidgetOptionDSO option : widget.getWidgetOptions() ) {
//								rcg.remove(option.getReferenceCode());
//								Log.get().debug("Removing used code: " + option.getReferenceCode());
//							}
//						}
//					}
//				}
//				
//				
//			}
//	    } catch (Exception e) {
//	    	Log.get().error("Error rebuilding reference codes: " + e.getMessage());
//	    	for ( StackTraceElement ste : e.getStackTrace() ) {
//	    		Log.get().error(ste.getClassName() + " "+ ste.getMethodName() + " " + ste.getLineNumber());
//	    	}
//	    } 
	}
}
