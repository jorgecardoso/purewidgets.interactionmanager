package org.instantplaces.interactionmanager.server.rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.shared.Widget;

@XmlRootElement
public class WidgetREST implements Widget {
	
	@XmlAttribute
	String applicationId;
	
	@XmlAttribute
	String id;

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

}
