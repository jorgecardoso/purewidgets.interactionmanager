package org.instantplaces.im.server.dso;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetInputDSO {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String widgetId;
	
	@Persistent
	private String referenceCode;
	
	@Persistent
	private String timeStamp;
	
	@Persistent
	private String []parameters;
	
	@Persistent
	private String persona;

	public WidgetInputDSO() {
		
	}
	
	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setParameters(String [] parameters) {
		this.parameters = parameters;
	}

	public String [] getParameters() {
		return parameters;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public String getPersona() {
		return persona;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public String getWidgetId() {
		return widgetId;
	}
	
	
}
