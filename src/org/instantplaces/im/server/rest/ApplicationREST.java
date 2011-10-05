/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

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
	
		if (applicationDSO.getPlace() != null ) {
			a.setPlaceId( applicationDSO.getPlace().getPlaceId() );
		}
		
		a.setApplicationId( applicationDSO.getApplicationId() );
		
		
		for ( WidgetDSO widget : applicationDSO.getWidgets() ) {
			a.addWidget( WidgetREST.fromDSO( widget ));
		}
		
	
		
		Log.get().debug("Converted: " + a.toString());
		return a; 
	}
	
	
}
