package org.instantplaces.interactionmanager.server.dso;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.shared.Application;
import org.instantplaces.interactionmanager.shared.Place;
import org.instantplaces.interactionmanager.shared.Widget;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ApplicationDSO  {
	protected static Logger log = Logger.getLogger("InteractionManagerApplication"); 

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	@Persistent(defaultFetchGroup = "true")
	private PlaceDSO place;
	
	@Persistent(mappedBy = "application")
	private ArrayList<WidgetDSO> widgets;
	
	public ApplicationDSO() {
		this(null, null, null);
	}
	
	public ApplicationDSO(String id, PlaceDSO place, ArrayList<WidgetDSO> widgets) {
		this.id = id;
		this.place = place;
		
		if (widgets != null) {
			this.widgets = widgets;
		} else {
			this.widgets = new ArrayList<WidgetDSO>();
		}
	}
	
	
	public void setId(String appID) {
		this.id = appID;
	}

	public String getId() {
		return this.id;
	}

	public void setPlace(PlaceDSO place) {
		this.place = place;
		
	}

	public PlaceDSO getPlace() {
		return this.place;
	}

	public void addWidget(WidgetDSO widget) {
		this.widgets.add(widget);
		
	}

	public WidgetDSO[] getWidgets() {
		return this.widgets.toArray(new WidgetDSO[0]);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	public String toString() {
		return "application(id: " + this.id + ")";
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof ApplicationDSO) ) {
			return false;
		}
		return ((ApplicationDSO) app).getKey().equals(this.key);
	} 
	
	public static ApplicationDSO[] getApplicationsDSO( PersistenceManager pm, String placeId ) {
		PlaceDSO place = PlaceDSO.getPlaceDSO(pm, placeId);
		if ( place == null ) {
			return null;
		}
		if (place.getApplications() == null) {
			log.info("Retrieved 0 applications from " + place.toString());
		} else {
			log.info("Retrieved " + place.getApplications().length + " applications for " + place.toString());
		}
		return place.getApplications();
	}	
	
	public static ApplicationDSO getApplicationDSO( PersistenceManager pm, String placeId, String applicationId ) {
		ApplicationDSO[] applications = getApplicationsDSO(pm, placeId);
		if ( applications == null ) {
			return null;
		}
		for ( ApplicationDSO app : applications ) {
			if (app.getId().equals(applicationId)) {
				log.info("Retrieved " + app.toString());
				return app;
			}
		}
		return null;
	}	

}
