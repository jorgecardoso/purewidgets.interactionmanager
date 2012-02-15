/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.instantplaces.im.server.dao.ApplicationDao;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties({ "widgets" }) // @JsonIgnore seems to not work correctly, so we duplicate
public class ApplicationRest {
	

	private	String placeId;

	private String applicationId;
	
	private String iconBaseUrl;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	private long lastRequestTimestamp;
	

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
	 * @return the iconBaseUrl
	 */
	public String getIconBaseUrl() {
		return iconBaseUrl;
	}


	/**
	 * @param iconBaseUrl the iconBaseUrl to set
	 */
	public void setIconBaseUrl(String iconBaseUrl) {
		this.iconBaseUrl = iconBaseUrl;
	}
	
}
