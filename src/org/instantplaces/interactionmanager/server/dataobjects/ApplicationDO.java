package org.instantplaces.interactionmanager.server.dataobjects;

import java.util.ArrayList;

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
public class ApplicationDO  {
	

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	@Persistent(defaultFetchGroup = "true")
	private PlaceDO place;
	
	@Persistent(mappedBy = "application")
	private ArrayList<WidgetDO> widgets;
	
	public ApplicationDO() {
		this(null, null, null);
	}
	
	public ApplicationDO(String id, PlaceDO place, ArrayList<WidgetDO> widgets) {
		this.id = id;
		this.place = place;
		
		if (widgets != null) {
			this.widgets = widgets;
		} else {
			this.widgets = new ArrayList<WidgetDO>();
		}
	}
	
	
	public void setId(String appID) {
		this.id = appID;
	}

	public String getId() {
		return this.id;
	}

	public void setPlace(PlaceDO place) {
		this.place = place;
		
	}

	public PlaceDO getPlace() {
		return this.place;
	}

	public void addWidget(WidgetDO widget) {
		this.widgets.add(widget);
		
	}

	public WidgetDO[] getWidgets() {
		return this.widgets.toArray(new WidgetDO[0]);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof ApplicationDO) ) {
			return false;
		}
		return ((ApplicationDO) app).getKey().equals(this.key);
	} 

}
