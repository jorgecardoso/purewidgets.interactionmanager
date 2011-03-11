package org.instantplaces.interactionmanager.server.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonSetter;
import org.instantplaces.interactionmanager.server.dso.WidgetDSO;
import org.instantplaces.interactionmanager.server.dso.WidgetOptionDSO;
import org.instantplaces.interactionmanager.shared.Widget;
import org.instantplaces.interactionmanager.shared.WidgetOption;
import org.mortbay.log.Log;

@XmlRootElement
public class WidgetREST implements Widget {
	protected static Logger log = Logger.getLogger("InteractionManagerApplication"); 
	@XmlAttribute
	private	String placeId;
	
	
	@XmlAttribute
	private String applicationId;
	
	@XmlAttribute
	private String id;

	@XmlElement
	private ArrayList<WidgetOption>widgetOptions;
	
	public WidgetREST() {
		this.widgetOptions = new ArrayList<WidgetOption>();
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
			this.widgetOptions.add(widgetOption);
		}
		
	}

	/*
	 * Needed so that Jackson can deserialize JSON correctly
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionREST> options) {
		this.widgetOptions = new ArrayList<WidgetOption>();
		this.widgetOptions.addAll(options);
		//this.widgetOptions = (ArrayList<WidgetOption>)options;
	}
	
	
	public WidgetDSO toDSO() {
		WidgetDSO wDSO = new WidgetDSO();
		log.info("Converting WidgetREST to DSO");
		wDSO.setId(this.id);
		log.info(this.widgetOptions.toString());
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
