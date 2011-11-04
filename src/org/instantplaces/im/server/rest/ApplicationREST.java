/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.jdo.annotations.Persistent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="application")
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonIgnoreProperties({ "widgets" }) // @JsonIgnore seems to not work correctly, so we duplicate
public class ApplicationREST {
	
	@XmlAttribute
	private	String placeId;

	@XmlAttribute
	private String applicationId;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@XmlElement
	private long lastRequestTimestamp;
	
	@XmlElement
	@JsonIgnore
	private ArrayList<WidgetREST> widgets;

	
	public ApplicationREST() {
		this.widgets = new ArrayList<WidgetREST>();
		
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

	
	public void addWidget( WidgetREST widget ) {
		this.widgets.add(widget);
	}
	
	public void setWidgets(ArrayList<WidgetREST> widgets) {
		this.widgets = widgets;
		
	}


	public ArrayList<WidgetREST> getWidgets() {
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
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < ApplicationDSO.MAXIMUM_ACTIVITY_INTERVAL;
	}
	
}
