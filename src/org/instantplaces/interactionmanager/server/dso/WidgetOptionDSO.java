package org.instantplaces.interactionmanager.server.dso;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.shared.Widget;
import org.instantplaces.interactionmanager.shared.WidgetOption;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetOptionDSO {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	
	@Persistent(defaultFetchGroup = "true")
	private WidgetDSO widget;
	
	public WidgetOptionDSO() {
		this(null, null);
	}
	
	public WidgetOptionDSO(String id, WidgetDSO widget) {
		this.id = id;
		this.widget = widget;
	}
	

	public void setWidget(WidgetDSO w) {
		this.widget = w;	
	}


	public WidgetDSO getWidget() {
		return this.widget;
	}


	public void setId(String id) {
		this.id = id;
		
	}


	public String getId() {
		return this.id;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof WidgetOptionDSO) ) {
			return false;
		}
		return ((WidgetOptionDSO) app).getKey().equals(this.key);
	} 	
}
