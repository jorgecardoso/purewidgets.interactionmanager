/**
 * 
 */
package org.instantplaces.im.server.rest.representation.json;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.instantplaces.im.server.dao.ApplicationDao;

import com.googlecode.objectify.annotation.Unindexed;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties({ "widgets" }) // @JsonIgnore seems to not work correctly, so we duplicate
public class ApplicationRest {
	

	private	String placeId;

	private String applicationId;
	
	private String applicationName;
	
	private String applicationBaseUrl;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	private long lastRequestTimestamp;
	
	/**
	 * Is this app currently on-screen?
	 */
	private boolean onScreen;	

	@JsonIgnore
	private ArrayList<WidgetRest> widgets;

	
	public ApplicationRest() {
		this.widgets = new ArrayList<WidgetRest>();
		
	}	
	
	
	public void setApplicationId(String appId) {
		this.applicationId = appId;
	}

	
	public String getApplicationId() {
		return this.applicationId;
	}

	
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
		
	}

	
	public String getPlaceId() {
		return placeId;
	}

	
	public void addWidget( WidgetRest widget ) {
		this.widgets.add(widget);
	}
	
	public void setWidgets(ArrayList<WidgetRest> widgets) {
		this.widgets = widgets;
		
	}


	public ArrayList<WidgetRest> getWidgets() {
		return this.widgets;
	}

	/**
	 * @return the lastRequestTimestamp
	 */
	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}


	/**
	 * @param lastRequestTimestamp the lastRequestTimestamp to set
	 */
	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}

	public boolean isActive() {
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < ApplicationDao.MAXIMUM_ACTIVITY_INTERVAL;
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
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}


	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


	/**
	 * @return the onScreen
	 */
	public boolean isOnScreen() {
		return onScreen;
	}


	/**
	 * @param onScreen the onScreen to set
	 */
	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}
	
}
