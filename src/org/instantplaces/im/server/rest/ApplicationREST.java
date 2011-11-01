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

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="application")
public class ApplicationREST {
	
	@XmlAttribute
	private	String placeId;

	@XmlAttribute
	private String applicationId;
	
	@XmlElement
	private ArrayList<WidgetREST> widgets;

	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@XmlElement
	private long lastRequestTimestamp;
	
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
	 * Converts a ApplicationDSO object to a ApplicationREST object.
	 * 
	 * @param widgetDSO
	 * @return
	 */
	public static ApplicationREST fromDSO(ApplicationDSO applicationDSO) {
		Log.get().debug("Converting to REST " + applicationDSO.toString());
		ApplicationREST  a = new ApplicationREST();
	
		a.setPlaceId( applicationDSO.getPlaceId() );
		a.setApplicationId( applicationDSO.getApplicationId() );
		
		
		for ( WidgetDSO widget : applicationDSO.getWidgets() ) {
			a.addWidget( WidgetREST.fromDSO( widget ));
		}
		
		a.setLastRequestTimestamp(applicationDSO.getLastRequestTimestamp());
		
		Log.get().debug("Converted: " + a.toString());
		return a; 
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
