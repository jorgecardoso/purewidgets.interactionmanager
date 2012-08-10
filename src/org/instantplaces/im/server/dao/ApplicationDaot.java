package org.instantplaces.im.server.dao;

import java.util.ArrayList;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class ApplicationDaot  {
	
	@NotSaved //not needed but to make explicit
	public static final int MAXIMUM_ACTIVITY_INTERVAL = 30*1000; // milliseconds
	
	@Id
	private String applicationId;

	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@Unindexed
	private long lastRequestTimestamp;
	
	@Unindexed
	private String applicationBaseUrl;
	
	/**
	 * The human-readable application name
	 */
	@Unindexed
	private String applicationName;
	
	/** The address to notify when new input is available
	 * 
	 */
	@Unindexed
	private String applicationServerNotificationUrl;
	
	/**
	 * The place this application belongs to
	 */
	@NotSaved
	private PlaceDaot place;
	
	/**
	 * The parent key
	 */
	@Parent
	private Key<PlaceDaot> placeKey;
	
	/**
	 * The list of widgets in use by this app
	 */
//	@NotSaved
//	private ArrayList<WidgetDao> widgets;
	
	public ApplicationDaot(PlaceDaot place, String applicationId) {
		this.applicationId = applicationId;
		
		this.lastRequestTimestamp = System.currentTimeMillis();
		this.setPlace(place);
	}
	
	@SuppressWarnings("unused")
	private ApplicationDaot() {
	}
	
	public String getApplicationId() {
		return this.applicationId;
	}

	public Key<ApplicationDaot> getKey() {
		return new Key<ApplicationDaot>(this.placeKey, ApplicationDaot.class, this.applicationId);
	}

	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}
	
	
	
	/**
	 * @return the place
	 */
	public PlaceDaot getPlace() {
		return place;
	} 
	
	/**
	 * @return the placeKey
	 */
	public Key<PlaceDaot> getPlaceKey() {
		return placeKey;
	}

	/**
	 * @return the widgets
	 */
//	public ArrayList<WidgetDao> getWidgets() {
//		return widgets;
//	}	

	

	public boolean isActive() {
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < MAXIMUM_ACTIVITY_INTERVAL;
	}

	public void setApplicationId(String appID) {
		this.applicationId = appID;
	}

	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}
	

	public void setPlace(PlaceDaot place) {
		if ( null != place ) {
			this.place = place;
			this.placeKey = new Key<PlaceDaot>(PlaceDaot.class, place.getPlaceId());
		}
	}

	/**
	 * @param placeKey the placeKey to set
	 */
	public void setPlaceKey(Key<PlaceDaot> placeKey) {
		this.placeKey = placeKey;
	}
	
	/**
	 * @param widgets
	 *            the widgets to set
	 */
//	public void setWidgets(ArrayList<WidgetDao> widgets) {
//		this.widgets = widgets;
//	}

	public String toDebugString() {
		return this.toString();
	}

	@Override
	public String toString() {
		return "Application: " + this.applicationId;
	}

	/**
	 * @return the applicationBaseUrl
	 */
	public String getApplicationBaseUrl() {
		return applicationBaseUrl;
	}

	/**
	 * @param applicationBaseUrl the applicationBaseUrl to set
	 */
	public void setApplicationBaseUrl(String applicationBaseUrl) {
		this.applicationBaseUrl = applicationBaseUrl;
	}

	/**
	 * @return the applicationServerNotificationUrl
	 */
	public String getApplicationServerNotificationUrl() {
		return applicationServerNotificationUrl;
	}

	/**
	 * @param applicationServerNotificationUrl the applicationServerNotificationUrl to set
	 */
	public void setApplicationServerNotificationUrl(String applicationServerNotificationUrl) {
		this.applicationServerNotificationUrl = applicationServerNotificationUrl;
	}

	/**
	 * @return the placeName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param placeName the placeName to set
	 */
	public void setApplicationName(String placeName) {
		this.applicationName = placeName;
	}

	
}
