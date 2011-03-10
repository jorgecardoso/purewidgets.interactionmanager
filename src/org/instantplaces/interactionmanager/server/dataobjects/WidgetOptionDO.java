package org.instantplaces.interactionmanager.server.dataobjects;

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
public class WidgetOptionDO {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	
	@Persistent(defaultFetchGroup = "true")
	private WidgetDO widget;
	
	public WidgetOptionDO() {
		this(null, null);
	}
	
	public WidgetOptionDO(String id, WidgetDO widget) {
		this.id = id;
		this.widget = widget;
	}
	

	public void setWidget(WidgetDO w) {
		this.widget = w;	
	}


	public WidgetDO getWidget() {
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
		if ( !(app instanceof WidgetOptionDO) ) {
			return false;
		}
		return ((WidgetOptionDO) app).getKey().equals(this.key);
	} 	
}
