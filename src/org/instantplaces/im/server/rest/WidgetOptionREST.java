package org.instantplaces.im.server.rest;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.im.shared.WidgetOption;
import org.instantplaces.im.server.Log;


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
	
	@XmlAttribute
	private String shortDescription;
	
	@XmlAttribute
	private String longDescription;
	
	
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
	
	@Override
	public String toString() {
		return "WidgetOption ( " + this.widgetOptionId + " )";
	
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


	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WidgetOption(id: ").append(this.widgetOptionId).append("; suggestedReferenceCode: ").append(this.suggestedReferenceCode);
		sb.append("; referenceCode: ").append(this.referenceCode);
		sb.append(")");
		return sb.toString();
		
	}


}
