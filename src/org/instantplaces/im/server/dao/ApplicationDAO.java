package org.instantplaces.im.server.dao;

import java.util.ArrayList;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;


public class ApplicationDAO  {
	
	@NotSaved //not needed but to make explicit
	public static final int MAXIMUM_ACTIVITY_INTERVAL = 30*1000; // milliseconds
	
	@Parent
	private Key<PlaceDAO> placeKey;

	@Id
	private String applicationId;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@Unindexed
	private long lastRequestTimestamp;
	
	
	
	@NotSaved
	private PlaceDAO place;
	
	@NotSaved
	private ArrayList<WidgetDAO> widgets;
	
	public ApplicationDAO() {
		this(null, null);
	}
	
	public ApplicationDAO(PlaceDAO place, String applicationId) {
		this.applicationId = applicationId;
		
		this.lastRequestTimestamp = System.currentTimeMillis();
		this.setPlace(place);
	}
	
//	@Override
//	public boolean equals(Object app) {
//		if ( !(app instanceof ApplicationDSO) ) {
//			return false;
//		}
//		return ((ApplicationDSO) app).getKey().equals(this.key);
//	}
	
	
	public String getApplicationId() {
		return this.applicationId;
	}

	


	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}

	/**
	 * @return the place
	 */
	public PlaceDAO getPlace() {
		return place;
	}
	
	
	
	/**
	 * @return the widgets
	 */
	public ArrayList<WidgetDAO> getWidgets() {
		return widgets;
	} 
	
	public boolean isActive() {
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < MAXIMUM_ACTIVITY_INTERVAL;
	}

	public void setApplicationId(String appID) {
		this.applicationId = appID;
	}	

	

	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}

	public void setPlace(PlaceDAO place) {
		if ( null != place ) {
			this.place = place;
			this.placeKey = new Key<PlaceDAO>(PlaceDAO.class, place.getPlaceId());
			//this.key = KeyFactory.createKey(place.getKey(), ApplicationDSO.class.getSimpleName(),  this.applicationId);	
		}
	}

	public Key<ApplicationDAO> getKey() {
		return new Key<ApplicationDAO>(this.placeKey, ApplicationDAO.class, this.applicationId);
	}
	

	/**
	 * @param widgets the widgets to set
	 */
	public void setWidgets(ArrayList<WidgetDAO> widgets) {
		this.widgets = widgets;
	}

	public String toDebugString() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		return "application( " + this.applicationId + " )";
	}

	/**
	 * @return the placeKey
	 */
	public Key<PlaceDAO> getPlaceKey() {
		return placeKey;
	}

	/**
	 * @param placeKey the placeKey to set
	 */
	public void setPlaceKey(Key<PlaceDAO> placeKey) {
		this.placeKey = placeKey;
	}
}
