package org.instantplaces.im.server.dso;

import java.util.ArrayList;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable="true")
public class ApplicationDSO  {
	
	public static final int MAXIMUM_ACTIVITY_INTERVAL = 30*1000; // milliseconds
	
	@Persistent
	private String applicationId;
	
	@PrimaryKey
    @Persistent //(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@Persistent 
	private long lastRequestTimestamp;
	
	@NotPersistent
	private PlaceDSO place;
	
	@Persistent 
	private String placeId;
	
	@NotPersistent
	private ArrayList<WidgetDSO> widgets;
	
	public ApplicationDSO() {
		this(null, null);
	}
	
	public ApplicationDSO(PlaceDSO place, String applicationId) {
		this.applicationId = applicationId;
		
		this.lastRequestTimestamp = System.currentTimeMillis();
		this.setPlace(place);
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof ApplicationDSO) ) {
			return false;
		}
		return ((ApplicationDSO) app).getKey().equals(this.key);
	}
	
	
	public String getApplicationId() {
		return this.applicationId;
	}

	public Key getKey() {
		return key;
	}


	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}

	/**
	 * @return the place
	 */
	public PlaceDSO getPlace() {
		return place;
	}
	
	/**
	 * @return the placeId
	 */
	public String getPlaceId() {
		return placeId;
	}
	
	/**
	 * @return the widgets
	 */
	public ArrayList<WidgetDSO> getWidgets() {
		return widgets;
	} 
	
	public boolean isActive() {
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < MAXIMUM_ACTIVITY_INTERVAL;
	}

	public void setApplicationId(String appID) {
		this.applicationId = appID;
	}	

	public void setKey(Key key) {
		this.key = key;
	}

	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}

	public void setPlace(PlaceDSO place) {
		if ( null != place ) {
			this.place = place;
			this.placeId = place.getPlaceId();
			this.key = KeyFactory.createKey(place.getKey(), ApplicationDSO.class.getSimpleName(),  this.applicationId);
			
		}
	}

	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	/**
	 * @param widgets the widgets to set
	 */
	public void setWidgets(ArrayList<WidgetDSO> widgets) {
		this.widgets = widgets;
	}

	public String toDebugString() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		return "application( " + this.applicationId + " )";
	}
}
