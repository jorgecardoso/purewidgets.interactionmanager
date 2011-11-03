package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import org.instantplaces.im.shared.Widget;
import org.instantplaces.im.shared.WidgetOption;
import org.instantplaces.im.server.Log;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="widget")
public class WidgetREST implements Widget {

	@XmlAttribute
	private	String placeId;
	
	@XmlAttribute
	private String applicationId;
	
	@XmlAttribute
	private String widgetId;
	
	
	@XmlAttribute
	private String controlType;

	@XmlElement
	private ArrayList<WidgetOptionREST>widgetOptions;

	@XmlElement
	private boolean volatileWidget;
	
	/**
	 * A short description (label) for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 *
	 */	
	@XmlElement
	private String shortDescription;
	
	/**
	 * A long description for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 */	
	@XmlElement
	private String longDescription;
	
	public WidgetREST() {
		this.widgetOptions = new ArrayList<WidgetOptionREST>();
		this.volatileWidget = false;
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
	public WidgetOptionREST[] getWidgetOptions() {
		if ( this.widgetOptions != null ) { 
			return this.widgetOptions.toArray(new WidgetOptionREST[0]);
		} else {
			return null;
		}
	}

	
	@Override
	public void addWidgetOption(WidgetOption widgetOption) {
		
		if ( !this.widgetOptions.contains(widgetOption) ) {
			this.widgetOptions.add((WidgetOptionREST)widgetOption);
		}
		
	}

	/*
	 * Needed so that Jackson can deserialize JSON correctly
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionREST> options) {
		this.widgetOptions = new ArrayList<WidgetOptionREST>();
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
			for (WidgetOptionREST wo : this.widgetOptions) {
				sb.append(wo.toDebugString());
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public boolean isVolatileWidget() {
		return volatileWidget;
	}

	public void setVolatileWidget(boolean volatileWidget) {
		this.volatileWidget = volatileWidget;
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



}
