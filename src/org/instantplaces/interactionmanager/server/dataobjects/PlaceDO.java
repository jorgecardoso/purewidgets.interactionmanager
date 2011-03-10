package org.instantplaces.interactionmanager.server.dataobjects;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.shared.Application;
import org.instantplaces.interactionmanager.shared.Place;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
@XmlRootElement
public class PlaceDO {
	protected Logger log = Logger.getLogger("InteractionManagerApplication"); 
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	@Persistent(mappedBy = "place")
	private ArrayList<ApplicationDO> applications;
	
	public PlaceDO() {
		this(null, null);
	}
	
	public PlaceDO(String id, ArrayList<ApplicationDO> applications) {
		this.id = id;
		if (applications != null) {
			this.applications = applications;
		} else {
			this.applications = new ArrayList<ApplicationDO>();
		}
	}
	

	public void setPlaceID(String placeID) {
		this.id = placeID;
	}


	public String getPlaceID() {
		return this.id;
	}


	public void addApplication(ApplicationDO app) {
		if (!this.applications.contains(app)) {
			this.applications.add(app);
		}
		
	}


	public ApplicationDO[] getApplications() {
		log.info("Appications: " + this.applications.toString());
		return this.applications.toArray(new ApplicationDO[0]);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof PlaceDO) ) {
			return false;
		}
		return ((PlaceDO) app).getKey().equals(this.key);
	} 	
	
	public void debug() {
		log.info("Place " + this.id);
		log.info("Applications: ");
		for (ApplicationDO app : this.applications) {
			log.info("   " + app.getId());
		}
	}

}
