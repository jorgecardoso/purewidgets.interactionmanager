package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;




@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class WidgetListRest {

	private String placeId;
	private String applicationId;
	
	private ArrayList<WidgetRest> widgets;
	
	
	public WidgetListRest() {
		
	}
	
	@Override
	public String toString() {
		if ( null != getWidgets() ) {
			return "WidgetArrayListRest ( " + getWidgets().size() + " widgets )";
		} else {
			return "WidgetArrayListRest";
		}
	}

	/**
	 * @return the widgets
	 */
	public ArrayList<WidgetRest> getWidgets() {
		return widgets;
	}

	/**
	 * @param widgets the widgets to set
	 */
	public void setWidgets(ArrayList<WidgetRest> widgets) {
		this.widgets = widgets;
	}

	/**
	 * @return the placeId
	 */
	public String getPlaceId() {
		return placeId;
	}

	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
}
