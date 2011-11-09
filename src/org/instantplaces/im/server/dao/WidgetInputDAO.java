package org.instantplaces.im.server.dao;

import java.util.Arrays;

import javax.persistence.Id;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

public class WidgetInputDAO {
	
	@Parent
	private
	Key<WidgetOptionDAO> widgetOptionKey;
	
	@Id
	private Long widgetInputId;
	
	@NotSaved
	private WidgetOptionDAO widgetOption;
	
	@Indexed
	private long timeStamp;
	
	@Unindexed
	private String []parameters;
	
	@Unindexed
	private String persona;
	
	/**
	 * The input mechanism that was used to generate this input
	 */
	@Unindexed
	private String inputMechanism;
	
	
	/**
	 * Indicates whether this input has already been delivered to the owner application
	 */
	@Unindexed
	private boolean delivered;
	

	public WidgetInputDAO() {
		delivered = false;
	}
	
	public WidgetInputDAO(WidgetOptionDAO widgetOptionDso, long timeStamp, String []parameters, String persona) {
		this.timeStamp = timeStamp;
		this.parameters = parameters;
		this.persona = persona;
		this.setWidgetOptionDSO(widgetOptionDso);
	}
	
	public void setWidgetOptionDSO(WidgetOptionDAO widgetOption) {
		this.widgetOption = widgetOption;
		if ( null != widgetOption ) {
			
			this.widgetOptionKey = new Key<WidgetOptionDAO>(widgetOption.getWidgetKey(), WidgetOptionDAO.class, widgetOption.getWidgetOptionId());
		}
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

	

	public WidgetOptionDAO getWidgetOptionDSO() {
		return this.widgetOption;
	}
	

	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return true;
		}
		
		if ( !(o instanceof WidgetInputDAO) ) {
			return false;
		}
		
		WidgetInputDAO other  = (WidgetInputDAO)o;
		
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
	 * @return the widgetInputId
	 */
	public Long getWidgetInputId() {
		return widgetInputId;
	}

	/**
	 * @param widgetInputId the widgetInputId to set
	 */
	public void setWidgetInputId(Long widgetInputId) {
		this.widgetInputId = widgetInputId;
	}

	/**
	 * @return the widgetOptionKey
	 */
	public Key<WidgetOptionDAO> getWidgetOptionKey() {
		return widgetOptionKey;
	}

	/**
	 * @param widgetOptionKey the widgetOptionKey to set
	 */
	public void setWidgetOptionKey(Key<WidgetOptionDAO> widgetOptionKey) {
		this.widgetOptionKey = widgetOptionKey;
	}
	
}
