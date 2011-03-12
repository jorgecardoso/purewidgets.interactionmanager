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
import org.instantplaces.im.server.dso.WidgetDSO;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="widget")
public class WidgetREST implements Widget {

	@XmlAttribute
	private	String placeId;
	
	
	@XmlAttribute
	private String applicationId;
	
	@XmlAttribute
	private String id;

	@XmlElement
	private ArrayList<WidgetOptionREST>widgetOptions;
	
	public WidgetREST() {
		this.widgetOptions = new ArrayList<WidgetOptionREST>();
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
	public void setId(String id) {
		this.id = id;
		
	}

	@Override
	public String getId() {
		return this.id;
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
	
	
	public WidgetDSO toDSO() {
		WidgetDSO wDSO = new WidgetDSO();
		Log.get().debug("Converting WidgetREST to DSO");
		wDSO.setId(this.id);
		for (WidgetOption wo : this.widgetOptions) {
			
			WidgetOptionREST woREST = (WidgetOptionREST)wo;
			
			wDSO.addWidgetOption(woREST.toDSO());
		}
		
		return wDSO;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Widget(id: ").append(this.id).append("; options: ");
		if ( this.widgetOptions != null ) {
			for (WidgetOption wo : this.widgetOptions) {
				sb.append(wo.toString());
			}
		}
		sb.append(")");
		return sb.toString();
	}

}
