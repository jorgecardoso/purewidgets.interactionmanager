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
	private static final long OLD = 1*24*60*60*1000; // milliseconds, 1 day
	
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
		    
//		    DsoFetcher.deleteApplicationsDSO(pm, System.currentTimeMillis()-OLD);
		    
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
