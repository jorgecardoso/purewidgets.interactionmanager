package org.instantplaces.im.server.dao;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;

@Cached
public class PlaceDaoTmp {

    @Id 
    private String placeId;
	
    
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
    
    
	public PlaceDaoTmp(String id) {
		this.placeId = id;
		
		/*
		 * By default we set the place reference code to a lower case version of the id.
		 */
		this.placeReferenceCode = id.toLowerCase();
	}
	
	@SuppressWarnings("unused")
	private PlaceDaoTmp() {
	}
	

	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof PlaceDaoTmp) ) {
			return false;
		}
		return ((PlaceDaoTmp) app).getPlaceId().equals(this.placeId);
	}


	public Key<PlaceDaoTmp> getKey() {
		return new Key<PlaceDaoTmp>(PlaceDaoTmp.class, this.placeId);
	}

	public String getPlaceId() {
		return this.placeId;
	}
	
	
	public void setPlaceId(String placeID) {
		this.placeId = placeID;
	} 	
	
	@Override
	public String toString() {
		return "Place: " + this.placeId;
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

}
