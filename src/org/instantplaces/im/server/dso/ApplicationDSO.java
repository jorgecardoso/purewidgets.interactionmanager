package org.instantplaces.im.server.dso;

import java.util.ArrayList;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ApplicationDSO  {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	@Persistent
	private PlaceDSO place;
	
	@Persistent(mappedBy = "application")
	private ArrayList<WidgetDSO> widgets;
	
	public ApplicationDSO() {
		this(null, null, null);
	}
	
	public ApplicationDSO(String id, PlaceDSO place, ArrayList<WidgetDSO> widgets) {
		this.id = id;
		this.place = place;
		
		if (widgets != null) {
			this.widgets = widgets;
		} else {
			this.widgets = new ArrayList<WidgetDSO>();
		}
	}
	
	
	public void setId(String appID) {
		this.id = appID;
	}

	public String getId() {
		return this.id;
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

	public WidgetDSO[] getWidgets() {
		return this.widgets.toArray(new WidgetDSO[0]);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	public String toString() {
		return "application(id: " + this.id + ")";
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof ApplicationDSO) ) {
			return false;
		}
		return ((ApplicationDSO) app).getKey().equals(this.key);
	} 
	
	public static ApplicationDSO[] getApplicationsDSO( PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching applications for Place(" + placeId + ") from Data Store.");
		
		PlaceDSO place = PlaceDSO.getPlaceDSO(pm, placeId);
		if ( place == null ) {
			Log.get().debug("Place not found.");
			return null;
		}
		if (place.getApplications() == null) {
			Log.get().debug("Found 0 applications.");
		} else {
			Log.get().debug("Found " + place.getApplications().length + " applications.");
		}
		return place.getApplications();
	}	
	
	public static ApplicationDSO getApplicationDSO( PersistenceManager pm, String placeId, String applicationId ) {
		Log.get().debug("Fetching application Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
		ApplicationDSO[] applications = getApplicationsDSO(pm, placeId);
		if ( applications == null ) {
			Log.get().debug("No applications found.");
			return null;
		}
		
		for ( ApplicationDSO app : applications ) {
			if (app.getId().equals(applicationId)) {
				Log.get().debug("Returning application.");
				return app;
			}
		}
		Log.get().debug("Application not found.");
		return null;
	}	

}
