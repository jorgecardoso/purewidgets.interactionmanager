package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Iterator;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.DsoFetcher;
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
	private static final long INACTIVE = 5*60*1000; // milliseconds, 5 minutes
	
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
		    
		    ArrayList<ApplicationDSO> applications = DsoFetcher.getApplicationsDSO(pm, System.currentTimeMillis()-INACTIVE);
		    
		    for ( ApplicationDSO app : applications ) {
		    	Log.get().debug("Deleting widgets from application: " + app.getApplicationId());
		    	ArrayList<WidgetDSO> widgets = DsoFetcher.getVolatileWidgetsFromDSO(pm, app.getPlaceId(), app.getApplicationId());
		    	for ( WidgetDSO widget : widgets ) {
		    		Log.get().debug("Deleting widget: " + widget.getWidgetId());
		    		DsoFetcher.deleteWidgetFromDSO(pm, widget.getPlaceId(), widget.getApplicationId(), widget.getWidgetId());
		    	}
		    }
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
