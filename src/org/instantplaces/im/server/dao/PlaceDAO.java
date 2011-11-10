package org.instantplaces.im.server.dao;

import javax.persistence.Id;

import com.googlecode.objectify.Key;


public class PlaceDAO {

    @Id 
    private String placeId;
	
    //@Unindexed
	//private ReferenceCodeGenerator codeGenerator;
	
	public PlaceDAO() {
		this(null);
	}
	
	public PlaceDAO(String id) {
		this.placeId = id;
		
		//this.codeGenerator = new ReferenceCodeGenerator();
		
	}
	

	public void setPlaceId(String placeID) {
		this.placeId = placeID;
	}


	public String getPlaceId() {
		return this.placeId;
	}

	public Key<PlaceDAO> getKey() {
		return new Key<PlaceDAO>(PlaceDAO.class, this.placeId);
	}
	
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof PlaceDAO) ) {
			return false;
		}
		return ((PlaceDAO) app).getPlaceId().equals(this.placeId);
	} 	
	
	@Override
	public String toString() {
		return "Place(placeId: " + this.placeId + ")";
	}

//	public void setCodeGenerator(ReferenceCodeGenerator codeGenerator) {
//		this.codeGenerator = codeGenerator;
//	}
//
//	public ReferenceCodeGenerator getCodeGenerator() {
//		return codeGenerator;
//		//return null;
//	}

}
