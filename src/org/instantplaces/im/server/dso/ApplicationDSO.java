package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ApplicationDSO  {
	
	private static final int MAXIMUM_ACTIVITY_INTERVAL = 30*1000; // milliseconds
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String applicationId;
	
	@Persistent
	private PlaceDSO place;
	
	@Persistent(mappedBy = "application")
	private ArrayList<WidgetDSO> widgets;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@Persistent 
	private long lastRequestTimestamp;
	
	public ApplicationDSO() {
		this(null, null, null);
	}
	
	public ApplicationDSO(String id, PlaceDSO place, ArrayList<WidgetDSO> widgets) {
		this.applicationId = id;
		this.place = place;
		
		if (widgets != null) {
			this.widgets = widgets;
		} else {
			this.widgets = new ArrayList<WidgetDSO>();
		}
		this.lastRequestTimestamp = System.currentTimeMillis();
	}
	
	
	public void setApplicationId(String appID) {
		this.applicationId = appID;
	}

	public String getApplicationId() {
		return this.applicationId;
	}

	public void setPlace(PlaceDSO place) {
		this.place = place;
		
	}

	public PlaceDSO getPlace() {
		return this.place;
	}

	public void addWidget(WidgetDSO widget) {
		this.widgets.add(widget);
		
	}
	
	public void removeWidget(WidgetDSO widget) {
		Log.get().debug("Deleting widget: " + widget.toString());
		if ( null != this.widgets ) {
			widget.recycleReferenceCodes();
			this.widgets.remove(widget);
			PersistenceManager pm = JDOHelper.getPersistenceManager(widget);
			pm.deletePersistent(widget);
		}
	}
	
	public void removeVolatileWidgets() {
		Iterator<WidgetDSO> it = this.widgets.iterator();
		ArrayList<WidgetDSO> toDelete = new ArrayList<WidgetDSO>();
		
		while ( it.hasNext() ) {
			WidgetDSO widget = it.next();
			if (widget.isVolatileWidget()) {
				Log.get().debug("Deleting widget: " + widget.toString());
				widget.recycleReferenceCodes();
				
				PersistenceManager pm = JDOHelper.getPersistenceManager(widget);
				pm.deletePersistent(widget);
				//it.remove();
				toDelete.add(widget);
			}
		}
		
		for ( WidgetDSO widget : toDelete ) {
			this.widgets.remove(widget);
		}
	}
	
	public void removeAllWidgets() {
		
		Iterator<WidgetDSO> it = this.widgets.iterator();
		ArrayList<WidgetDSO> toDelete = new ArrayList<WidgetDSO>();
		while ( it.hasNext() ) {
			WidgetDSO widget = it.next();
			Log.get().debug("Deleting widget: " + widget.toString());
			widget.recycleReferenceCodes();
			
			PersistenceManager pm = JDOHelper.getPersistenceManager(widget);
			pm.deletePersistent(widget);
			
			//it.remove();
			toDelete.add(widget);
			
		}
		for ( WidgetDSO widget : toDelete ) {
			this.widgets.remove(widget);
		}
	}
	
	public ArrayList<WidgetDSO> getWidgets() {
		return this.widgets;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	public String toString() {
		return "application(id: " + this.applicationId + ")";
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof ApplicationDSO) ) {
			return false;
		}
		return ((ApplicationDSO) app).getKey().equals(this.key);
	} 
	
	public static ArrayList<ApplicationDSO> getApplicationsDSO( PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching applications for Place(" + placeId + ") from Data Store.");
		
		PlaceDSO place = PlaceDSO.getPlaceDSO(pm, placeId);
		if ( place == null ) {
			Log.get().debug("Place not found.");
			return null;
		}
		if (place.getApplications() == null) {
			Log.get().debug("Found 0 applications.");
		} else {
			Log.get().debug("Found " + place.getApplications().size() + " applications.");
		}
		return place.getApplications();
	}	
	
	public static ApplicationDSO getApplicationDSO( PersistenceManager pm, String placeId, String applicationId ) {
		Log.get().debug("Fetching application Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
		ArrayList<ApplicationDSO> applications = getApplicationsDSO(pm, placeId);
		if ( applications == null ) {
			Log.get().debug("No applications found.");
			return null;
		}
		
		for ( ApplicationDSO app : applications ) {
			if (app.getApplicationId().equals(applicationId)) {
				Log.get().debug("Returning application.");
				return app;
			}
		}
		Log.get().debug("Application not found.");
		return null;
	}

	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}

	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}	

	public boolean isActive() {
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < MAXIMUM_ACTIVITY_INTERVAL;
	}
}
