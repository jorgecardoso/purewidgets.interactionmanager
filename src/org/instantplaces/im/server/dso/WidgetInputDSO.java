package org.instantplaces.im.server.dso;

import java.util.Arrays;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.WidgetInputREST;
import org.instantplaces.im.server.rest.WidgetREST;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetInputDSO {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private WidgetOptionDSO widgetOption;
		
	@Persistent
	private long timeStamp;
	
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

	

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getTimeStamp() {
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
	

	public WidgetInputREST toREST() {
		Log.get().debug("Converting to REST " + this.toString());
		
		WidgetInputREST  w = new WidgetInputREST();
	
		if (this.widgetOption != null) {
			w.setReferenceCode(this.widgetOption.getReferenceCode());
			w.setWidgetId(this.widgetOption.getWidget().getId());
		}
		w.setParameters(this.parameters);
		w.setPersona(this.persona);
		w.setTimeStamp(this.timeStamp);
		
		Log.get().debug("Converted: " + w.toString());
		return w; 
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
		
		if ( !(this.timeStamp == other.getTimeStamp()) ) {
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
	
	@Override
	public String toString() {
		
		return "WidgetInput(" + "persona: " + this.persona + "; parameters " + Arrays.toString(this.parameters) + ")";
	}
	
}
