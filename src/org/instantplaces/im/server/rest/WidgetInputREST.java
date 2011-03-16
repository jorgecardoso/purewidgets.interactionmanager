package org.instantplaces.im.server.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.im.shared.WidgetInput;

/**
 * An input is issued by a Persona and consists of a list
 * of zero or more parameters.
 * 
 * @author Jorge C. S. Cardoso
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class WidgetInputREST implements WidgetInput{
	
	@XmlAttribute
	private String widgetId;
	
	@XmlAttribute
	private String referenceCode;
	
	@XmlAttribute
	private long timeStamp;	
	
	@XmlElement
	private String[] parameters;
	
	@XmlAttribute
	private String persona;	
	
	public WidgetInputREST() {
		
	}

	@Override
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	@Override
	public String getWidgetId() {
		return this.widgetId;
	}

	@Override
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	@Override
	public String getReferenceCode() {
		return this.referenceCode;
	}

	@Override
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public Long getTimeStamp() {
		return this.timeStamp;
	}

	@Override
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public String[] getParameters() {
		return this.parameters;
	}

	@Override
	public void setPersona(String persona) {
		this.persona = persona;
	}

	@Override
	public String getPersona() {
		return this.persona;
	}
}
