package org.instantplaces.im.server.rest;

import org.instantplaces.im.shared.WidgetInput;

/**
 * An input is issued by a Persona and consists of a list
 * of zero or more parameters.
 * 
 * @author Jorge C. S. Cardoso
 *
 */


public class WidgetInputREST implements WidgetInput{
	

	private String widgetId;
	

	private String widgetOptionId;
	

	private String timeStamp;	
	

	private String[] parameters;
	

	private String persona;	
	
	/**
	 * The input mechanism that was used to generate this input
	 */

	private String inputMechanism;
	
	/**
	 * Indicates whether this input has already been delivered to the owner application
	 */
	private boolean delivered;
	
	public WidgetInputREST() {
		this.delivered = false;
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
	public void setWidgetOptionId(String widgetOptionId) {
		this.widgetOptionId = widgetOptionId;
	}

	@Override
	public String getWidgetOptionId() {
		return this.widgetOptionId;
	}

	@Override
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String getTimeStamp() {
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
}
