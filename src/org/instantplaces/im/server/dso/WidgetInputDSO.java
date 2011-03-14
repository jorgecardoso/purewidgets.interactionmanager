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
	private WidgetOptionDSO widgetOption;
		
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

	public void setWidgetOptionDSO(WidgetOptionDSO widgetOption) {
		this.widgetOption = widgetOption;
	}

	public WidgetOptionDSO getWidgetOptionDSO() {
		return this.widgetOption;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return true;
		}
		
		if ( !(o instanceof WidgetInputDSO) ) {
			return false;
		}
		
		WidgetInputDSO other  = (WidgetInputDSO)o;
		
		if ( !this.timeStamp.equals(other.getTimeStamp()) ) {
			return false;
		}
		
		if ( !this.persona.equals(other.getPersona()) ) {
			return false;
		}
		
		if ( this.parameters.length != other.getParameters().length ) {
			return false;
		}
		
		for (int i = 0; i < this.parameters.length; i++) {
			if ( !this.parameters[i].equals(other.getParameters()[i]) ) {
				return false;
			}
		}
		return true;
	}
	
}
