/**
 * 
 */
package org.instantplaces.im.server.rest.representation.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
//@JsonIgnoreProperties({ "widgets" }) // @JsonIgnore seems to not work correctly, so we duplicate
public class PlaceRest {
	

	private	String placeId;

	private String placeName;
	
	/*
     * The reference code used for text-based interactions
     */
    private String placeReferenceCode;
    
    /*
     * The phone number users can text in order to interact.
     */
    private String placePhoneNumber;
    
    /**
     * The email address to which users can send emails to interact
     */
    private String placeEmailAddress;
    
    /**
     * The web address of the placeinteraction webpage.
     */
    private String placeInteractionUrl;	

	public PlaceRest() {
	}	
	

	public void setPlaceId(String placeId) {
		this.placeId = placeId;	
	}

	
	public String getPlaceId() {
		return placeId;
	}


	/**
	 * @return the placeReferenceCode
	 */
	public String getPlaceReferenceCode() {
		return placeReferenceCode;
	}


	/**
	 * @param placeReferenceCode the placeReferenceCode to set
	 */
	public void setPlaceReferenceCode(String placeReferenceCode) {
		this.placeReferenceCode = placeReferenceCode;
	}


	/**
	 * @return the placePhoneNumber
	 */
	public String getPlacePhoneNumber() {
		return placePhoneNumber;
	}


	/**
	 * @param placePhoneNumber the placePhoneNumber to set
	 */
	public void setPlacePhoneNumber(String placePhoneNumber) {
		this.placePhoneNumber = placePhoneNumber;
	}


	/**
	 * @return the placeEmailAddress
	 */
	public String getPlaceEmailAddress() {
		return placeEmailAddress;
	}


	/**
	 * @param placeEmailAddress the placeEmailAddress to set
	 */
	public void setPlaceEmailAddress(String placeEmailAddress) {
		this.placeEmailAddress = placeEmailAddress;
	}


	/**
	 * @return the placeInteractionUrl
	 */
	public String getPlaceInteractionUrl() {
		return placeInteractionUrl;
	}


	/**
	 * @param placeInteractionUrl the placeInteractionUrl to set
	 */
	public void setPlaceInteractionUrl(String placeInteractionUrl) {
		this.placeInteractionUrl = placeInteractionUrl;
	}


	/**
	 * @return the placeName
	 */
	public String getPlaceName() {
		return placeName;
	}


	/**
	 * @param placeName the placeName to set
	 */
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}	
}