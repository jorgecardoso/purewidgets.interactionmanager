package org.instantplaces.im.server.dso;

import java.util.Arrays;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.WidgetInputREST;


import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetInputDSO {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String placeId;
	
	@Persistent
	private String applicationId;
	
	@Persistent
	private String widgetId;
	
	@Persistent
	private String widgetOptionId;
	
	@Persistent
	private WidgetOptionDSO widgetOption;
	
	@Persistent
	private long timeStamp;
	
	@Persistent
	private String []parameters;
	
	@Persistent
	private String persona;
	
	@Persistent
	/**
	 * The input mechanism that was used to generate this input
	 */
	private String inputMechanism;
	
	@Persistent
	/**
	 * Indicates whether this input has already been delivered to the owner application
	 */
	private boolean delivered;

	public WidgetInputDSO() {
		delivered = false;
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
		this.widgetOptionId = widgetOption.getWidgetOptionId();
		this.widgetId = widgetOption.getWidgetId();
		this.applicationId = widgetOption.getApplicationId();
		this.placeId = widgetOption.getPlaceId();
	}

	public WidgetOptionDSO getWidgetOptionDSO() {
		return this.widgetOption;
	}
	

	public WidgetInputREST toREST() {
		Log.get().debug("Converting to REST " + this.toString());
		
		WidgetInputREST  w = new WidgetInputREST();
	
		if (this.widgetOption != null) {
			w.setWidgetOptionId(this.widgetOption.getWidgetOptionId());
			w.setWidgetId(this.widgetOption.getWidget().getWidgetId());
		}
		w.setParameters(this.parameters);
		w.setPersona(this.persona);
		w.setTimeStamp(""+this.timeStamp);
		w.setInputMechanism(this.inputMechanism);
		w.setDelivered(this.delivered);
		
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

	/**
	 * @return the inputMechanism
	 */
	public String getInputMechanism() {
		return inputMechanism;
	}

	/**
	 * @param inputMechanism the inputMechanism to set
	 */
	public void setInputMechanism(String inputMechanism) {
		this.inputMechanism = inputMechanism;
	}

	/**
	 * @return the delivered
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * @param delivered the delivered to set
	 */
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	/**
	 * @return the widgetOptionId
	 */
	public String getWidgetOptionId() {
		return widgetOptionId;
	}

	/**
	 * @param widgetOptionId the widgetOptionId to set
	 */
	public void setWidgetOptionId(String widgetOptionId) {
		this.widgetOptionId = widgetOptionId;
	}

	/**
	 * @return the widgetId
	 */
	public String getWidgetId() {
		return widgetId;
	}

	/**
	 * @param widgetId the widgetId to set
	 */
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the placeId
	 */
	public String getPlaceId() {
		return placeId;
	}

	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	
}
