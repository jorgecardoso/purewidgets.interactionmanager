package org.instantplaces.im.server.rest;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.im.shared.WidgetOption;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.WidgetOptionDSO;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class WidgetOptionREST implements WidgetOption {
	
	//@XmlAttribute
	//private	String widgetId;
	
	
	@XmlAttribute
	private String widgetOptionId;
	
	@XmlAttribute
	private String suggestedReferenceCode;
	
	@XmlAttribute
	private String referenceCode;
	
	
	public WidgetOptionREST() {
		
	}
	

	@Override
	public void setWidgetOptionId(String id) {
		this.widgetOptionId = id;
	}

	@Override
	public String getWidgetOptionId() {
		return this.widgetOptionId;
	}

	@Override
	public void setSuggestedReferenceCode(String refCode) {
		this.suggestedReferenceCode = refCode;
	}

	@Override
	public String getSuggestedReferenceCode() {
		return this.suggestedReferenceCode;
	}

	@Override
	public void setReferenceCode(String refCode) {
		this.referenceCode = refCode;		
	}

	@Override
	public String getReferenceCode() {
		return this.referenceCode;
	}
	
	public WidgetOptionDSO toDSO() {
		Log.get().debug("Converting WidgetOptionREST to DSO");
		
		WidgetOptionDSO woDSO = new WidgetOptionDSO();
		
		woDSO.setWidgetOptionId(this.widgetOptionId);
		woDSO.setSuggestedReferenceCode(this.suggestedReferenceCode);
		woDSO.setReferenceCode(this.referenceCode);
		
		return woDSO;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WidgetOption(id: ").append(this.widgetOptionId).append("; suggestedReferenceCode: ").append(this.suggestedReferenceCode);
		sb.append("; referenceCode: ").append(this.referenceCode);
		sb.append(")");
		return sb.toString();
	}


}
