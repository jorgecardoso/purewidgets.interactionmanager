package org.instantplaces.im.server.rest.representation.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.instantplaces.im.shared.WidgetInput;

/**
 * An input is issued by a Persona and consists of a list
 * of zero or more parameters.
 * 
 * @author Jorge C. S. Cardoso
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class WidgetInputRest {
	
	private String placeId;
	
	private String applicationId;

	private String referenceCode;
	
	private String widgetId;

	private String widgetOptionId;
	

	private String timeStamp;	
	

	private String[] parameters;
	
	private String userId;
	
	private String nickname;	
	
	/**
	 * The input mechanism that was used to generate this input
	 */

	private String inputMechanism;
	
	/**
	 * Indicates whether this input has already been delivered to the owner application
	 * Not used 
	 */
	private boolean delivered;
	
	/**
	 * The age of this input in milliseconds.
	 */
	private int age;
	
	
	public WidgetInputRest() {
		this.delivered = false;
	}

	
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}


	public String getWidgetId() {
		return this.widgetId;
	}


	public void setWidgetOptionId(String widgetOptionId) {
		this.widgetOptionId = widgetOptionId;
	}


	public String getWidgetOptionId() {
		return this.widgetOptionId;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}


	public String getTimeStamp() {
		return this.timeStamp;
	}


	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}


	public String[] getParameters() {
		return this.parameters;
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
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
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
	 * @return the referenceCode
	 */
	public String getReferenceCode() {
		return referenceCode;
	}

	/**
	 * @param referenceCode the referenceCode to set
	 */
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	
}
