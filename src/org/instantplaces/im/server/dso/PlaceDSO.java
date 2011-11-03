package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;


import com.google.appengine.api.datastore.Key;


@PersistenceCapable(detachable="true")
public class PlaceDSO {

	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	// Careful: changing this field name implies changing the jdo query parameter in getPlaceDSO.
	@Persistent
	private String placeId;
	
	
	@Persistent
	private ReferenceCodeGenerator codeGenerator;
	
	public PlaceDSO() {
		this(null, null);
	}
	
	public PlaceDSO(String id, ArrayList<ApplicationDSO> applications) {
		this.placeId = id;
		
		this.codeGenerator = new ReferenceCodeGenerator();
		
	}
	

	public void setPlaceId(String placeID) {
		this.placeId = placeID;
	}


	public String getPlaceId() {
		return this.placeId;
	}


	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof PlaceDSO) ) {
			return false;
		}
		return ((PlaceDSO) app).getKey().equals(this.key);
	} 	
	
	public String toString() {
		return "Place(placeId: " + this.placeId + ")";
	}

	public void setCodeGenerator(ReferenceCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}

	public ReferenceCodeGenerator getCodeGenerator() {
		return codeGenerator;
		//return null;
	}

}
