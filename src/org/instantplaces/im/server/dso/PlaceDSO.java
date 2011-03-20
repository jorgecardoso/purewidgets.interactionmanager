package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;


import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class PlaceDSO {

	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	@Persistent(mappedBy = "place")
	private ArrayList<ApplicationDSO> applications;
	
	@Persistent
	private ReferenceCodeGenerator codeGenerator;
	
	public PlaceDSO() {
		this(null, null);
	}
	
	public PlaceDSO(String id, ArrayList<ApplicationDSO> applications) {
		this.id = id;
		
		
		if (applications != null) {
			this.applications = applications;
		} else {
			this.applications = new ArrayList<ApplicationDSO>();
		}
		this.codeGenerator = new ReferenceCodeGenerator();
		
	}
	

	public void setPlaceID(String placeID) {
		this.id = placeID;
	}


	public String getPlaceID() {
		return this.id;
	}


	public void addApplication(ApplicationDSO app) {
		if (!this.applications.contains(app)) {
			this.applications.add(app);
		}
		
	}


	public ArrayList<ApplicationDSO> getApplications() {
		return this.applications;
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
	
	public static PlaceDSO getPlaceDSO(PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching place Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(PlaceDSO.class);
	    query.setFilter("id == idParam");
	    query.declareParameters("String idParam");
	    
	    try {
	        List<Object> results = (List<Object>) query.execute(placeId);
	        if (!results.isEmpty()) {
	        	Log.get().debug("Found " + results.size() + " places. Returning first.");
	        	PlaceDSO place = (PlaceDSO)results.get(0);
	        	return place;
	        } else {
	        	Log.get().debug("Place not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store.");
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public String toString() {
		return "Place(id: " + this.id + ")";
	}

	public void setCodeGenerator(ReferenceCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}

	public ReferenceCodeGenerator getCodeGenerator() {
		return codeGenerator;
		//return null;
	}

}
