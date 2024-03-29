package org.instantplaces.im.server.dao;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class PlaceDao {

    @Id 
    private String placeId;
	
    
    /**
     * The human-readable place name
     */
    @Unindexed
    private String placeName;
    
    /*
     * The reference code used for text-based interactions
     */
    private String placeReferenceCode;
    
    /*
     * The phone number users can text in order to interact.
     */
    @Unindexed
    private String placePhoneNumber;
    
    /**
     * The email address to which users can send emails to interact
     */
    @Unindexed
    private String placeEmailAddress;
    
    /**
     * The web address of the placeinteraction webpage.
     */
    @Unindexed
    private String placeInteractionUrl;
    
    
	public PlaceDao(String id) {
		this.placeId = id;
		
		/*
		 * By default we set the place reference code to a lower case version of the id.
		 */
		this.placeReferenceCode = id.toLowerCase();
	}
	
	@SuppressWarnings("unused")
	private PlaceDao() {
	}
	

	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof PlaceDao) ) {
			return false;
		}
		return ((PlaceDao) app).getPlaceId().equals(this.placeId);
	}


	public Key<PlaceDao> getKey() {
		return new Key<PlaceDao>(PlaceDao.class, this.placeId);
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
