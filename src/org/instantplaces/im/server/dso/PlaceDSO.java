package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.FetchGroup;
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
	
	// Careful: changing this field name implies changing the jdo query parameter in getPlaceDSO.
	@Persistent
	private String placeId;
	
	@Persistent(mappedBy = "place")
	private ArrayList<ApplicationDSO> applications;
	
	@Persistent
	private ReferenceCodeGenerator codeGenerator;
	
	public PlaceDSO() {
		this(null, null);
	}
	
	public PlaceDSO(String id, ArrayList<ApplicationDSO> applications) {
		this.placeId = id;
		
		
		if (applications != null) {
			this.applications = applications;
		} else {
			this.applications = new ArrayList<ApplicationDSO>();
		}
		this.codeGenerator = new ReferenceCodeGenerator();
		
	}
	

	public void setPlaceId(String placeID) {
		this.placeId = placeID;
	}


	public String getPlaceId() {
		return this.placeId;
	}


	public void addApplication(ApplicationDSO app) {
		if (!this.applications.contains(app)) {
			this.applications.add(app);
		}
		
	}
	
	public boolean deleteApplication(ApplicationDSO app) {
		
		if ( this.applications.contains(app) ) {
			app.removeAllWidgets();
			this.applications.remove(app);
			PersistenceManager pm = JDOHelper.getPersistenceManager(app);
			pm.deletePersistent(app);
			
			return true;
		} else {
			return false;
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
	    query.setFilter("placeId == idParam");
	    query.declareParameters("String idParam");
	    query.setUnique(true);
	    
	    try {
	        PlaceDSO result = (PlaceDSO) query.execute(placeId);
	        if ( null != result ) {
	        	Log.get().debug("Found places. Returning first.");
	        	
	        	return result;
	        } else {
	        	Log.get().debug("Place not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
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
