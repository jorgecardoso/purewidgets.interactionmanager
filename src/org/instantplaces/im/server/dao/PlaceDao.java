package org.instantplaces.im.server.dao;

import javax.persistence.Id;

import com.googlecode.objectify.Key;


public class PlaceDao {

    @Id 
    private String placeId;
	
    
	public PlaceDao(String id) {
		this.placeId = id;
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

}
