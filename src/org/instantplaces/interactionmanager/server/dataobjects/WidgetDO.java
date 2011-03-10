package org.instantplaces.interactionmanager.server.dataobjects;

import java.util.ArrayList;
import java.util.Arrays;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.server.PMF;
import org.instantplaces.interactionmanager.server.rest.WidgetREST;
import org.instantplaces.interactionmanager.shared.Application;
import org.instantplaces.interactionmanager.shared.Widget;
import org.instantplaces.interactionmanager.shared.WidgetOption;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetDO {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	
	@Persistent(defaultFetchGroup = "true")
	ApplicationDO application;
	
	@Persistent
	String id;
	
	@Persistent(mappedBy = "widget")
	ArrayList <WidgetOptionDO> options; 

	
	public WidgetDO() {
		this(null, null, null);
	}
	
	public WidgetDO(String id, 	ApplicationDO app, ArrayList<WidgetOptionDO>options) {
		this.id = id;
		this.application = app;
		
		if (options != null) {
			this.options = options;
		} else {
			this.options = new ArrayList<WidgetOptionDO>();
		}
			
	}
	

	public void setApplication(	ApplicationDO app) {
		this.application = app;
		
	}


	public 	ApplicationDO getApplication() {
		return this.application;
	}



	public void setId(String id) {
		this.id = id;
		
	}



	public String getId() {
		return this.id;
	}



	public void addOption(WidgetOptionDO option) {
		this.options.add(option);
		
	}


	public WidgetOptionDO[] getOptions() {
		return this.options.toArray(new WidgetOptionDO[0]);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public WidgetREST toREST() {
		WidgetREST  w = new WidgetREST();
		if (this.application != null) {
			w.setApplicationId(this.application.getId());
		}
		w.setId(this.id);
		
		return w; 
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof WidgetDO) ) {
			return false;
		}
		return ((WidgetDO) app).getKey().equals(this.key);
	} 	
}
