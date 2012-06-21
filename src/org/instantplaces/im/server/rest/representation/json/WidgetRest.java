package org.instantplaces.im.server.rest.representation.json;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.instantplaces.im.shared.Widget;
import org.instantplaces.im.shared.WidgetOption;


@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class WidgetRest implements Widget {

	private	String placeId;
	

	private String applicationId;
	
	 
	private String widgetId;
	
	
	private String controlType;


	private ArrayList<WidgetOptionRest>widgetOptions;
	
	private ArrayList<WidgetParameterRest>widgetParameters;

	
	/**
	 * A short description (label) for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 *
	 */	
	private String shortDescription;
	
	/**
	 * A long description for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 */	
	private String longDescription;
	
	public WidgetRest() {
		this.widgetOptions = new ArrayList<WidgetOptionRest>();
		this.widgetParameters = new ArrayList<WidgetParameterRest>();
		
	}	
	
	@Override
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	@Override
	public String getPlaceId() {
		return placeId;
	}
	
	@Override
	public void setApplicationId(String appId) {
		this.applicationId = appId;
		
	}


	@Override
	public String getApplicationId() {
		return this.applicationId;
	}

	@Override
	public void setWidgetId(String id) {
		this.widgetId = id;
		
	}

	@Override
	public String getWidgetId() {
		return this.widgetId;
	}


	@Override
	public WidgetOptionRest[] getWidgetOptions() {
		if ( this.widgetOptions != null ) { 
			return this.widgetOptions.toArray(new WidgetOptionRest[0]);
		} else {
			return null;
		}
	}

	
	@Override
	public void addWidgetOption(WidgetOption widgetOption) {
		
		if ( !this.widgetOptions.contains(widgetOption) ) {
			this.widgetOptions.add((WidgetOptionRest)widgetOption);
		}
		
	}


	public void addWidgetParameter(WidgetParameterRest widgetParameterFromDSO) {
		this.widgetParameters.add(widgetParameterFromDSO);
		
	}
	
	/*
	 * Needed so that Jackson can deserialize JSON correctly
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionRest> options) {
		this.widgetOptions = new ArrayList<WidgetOptionRest>();
		this.widgetOptions.addAll(options);
		//this.widgetOptions = (ArrayList<WidgetOption>)options;
	}
	
	
	@Override
	public String toString() {
		return "WidgetRest ( " + this.widgetId + " )";
		
	}
	
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Widget(id: ").append(this.widgetId).append("; options: ");
		if ( this.widgetOptions != null ) {
			for (WidgetOptionRest wo : this.widgetOptions) {
				sb.append(wo.toDebugString());
			}
		}
		sb.append(")");
		return sb.toString();
	}

	

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @return the controlType
	 */
	public String getControlType() {
		return controlType;
	}

	/**
	 * @param controlType the controlType to set
	 */
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

	


	/**
	 * @return the widgetParameters
	 */
	public ArrayList<WidgetParameterRest> getWidgetParameters() {
		return widgetParameters;
	}

	/**
	 * @param widgetParameters the widgetParameters to set
	 */
	public void setWidgetParameters(ArrayList<WidgetParameterRest> widgetParameters) {
		this.widgetParameters = widgetParameters;
	}




}
