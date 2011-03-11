package org.instantplaces.interactionmanager.server.dso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

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
import org.instantplaces.interactionmanager.server.rest.WidgetOptionREST;
import org.instantplaces.interactionmanager.server.rest.WidgetREST;
import org.instantplaces.interactionmanager.shared.Application;
import org.instantplaces.interactionmanager.shared.Widget;
import org.instantplaces.interactionmanager.shared.WidgetOption;
import org.mortbay.log.Log;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetDSO {
	protected static Logger log = Logger.getLogger("InteractionManagerApplication"); 
	
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	
	@Persistent(defaultFetchGroup = "true")
	private ApplicationDSO application;
	
	
	@Persistent
	private String id;
	
	
	@Persistent(mappedBy = "widget")
	private ArrayList <WidgetOptionDSO> options; 

	
	public WidgetDSO() {
		this(null, null, null);
	}

	
	public WidgetDSO(String id, ApplicationDSO app, ArrayList<WidgetOptionDSO>options) {
		this.id = id;
		this.application = app;
		
		if (options != null) {
			this.options = options;
		} else {
			this.options = new ArrayList<WidgetOptionDSO>();
		}
			
	}
	

	public void setApplication(	ApplicationDSO app) {
		this.application = app;
		
	}


	public 	ApplicationDSO getApplication() {
		return this.application;
	}



	public void setId(String id) {
		this.id = id;
		
	}



	public String getId() {
		return this.id;
	}



	public void addWidgetOption(WidgetOptionDSO option) {
		if (!this.options.contains(option)) {
			this.options.add(option);
		}
	}


	public WidgetOptionDSO[] getWidgetOptions() {
		return this.options.toArray(new WidgetOptionDSO[0]);
	}

	public ArrayList<WidgetOptionDSO> getWidgetOptionsAsArrayList() {
		return this.options;
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
			
			if (this.application.getPlace() != null) {
				w.setPlaceId(this.application.getPlace().getPlaceID());
			}
		}
		for (WidgetOptionDSO option : this.options) {
			w.addWidgetOption( option.toREST() );
		}
		
		w.setId(this.id);
		
		return w; 
	}
	/*
	 * 	public WidgetDSO toDSO() {
		WidgetDSO wDSO = new WidgetDSO();
		log.info("Converting WidgetREST to DSO");
		wDSO.setId(this.id);
		log.info(this.widgetOptions.toString());
		for (WidgetOption wo : this.widgetOptions) {
			
			WidgetOptionREST woREST = (WidgetOptionREST)wo;
			
			wDSO.addWidgetOption(woREST.toDSO());
		}
		
		return wDSO;
	}*/
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof WidgetDSO) ) {
			return false;
		}
		return ((WidgetDSO) app).getKey().equals(this.key);
	} 	
	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Widget(id: ").append(this.id).append("; options: ");
		if ( this.options != null ) {
			for (WidgetOptionDSO wo : this.options) {
				sb.append(wo.toString());
			}
		}
		sb.append(")");
		return sb.toString();
	}

	
	public static WidgetDSO[] getWidgetsFromDSO( PersistenceManager pm, String placeId, String applicationId) {
	
		ApplicationDSO application = ApplicationDSO.getApplicationDSO(pm, placeId, applicationId);
		if (application == null) {
			return null;
		}
		if (application.getWidgets() == null) {
			log.info("Retrieved 0 widgets from " + application.toString());
		} else {
			log.info("Retrieved " + application.getWidgets().length + " widgets for " + application.toString());
			
		}
		return application.getWidgets();
	}
	
	public static WidgetDSO getWidgetFromDSO( PersistenceManager pm, String placeId, String applicationId, String widgetId) {
		WidgetDSO widgets[] = getWidgetsFromDSO(pm, placeId, applicationId);
		
		if ( widgets == null ) {
			return null;
		}
		for ( WidgetDSO widget : widgets ) {
			if (widget.getId().equals(widgetId)) {
				return widget;
			}
		}
		return null;
	}	
	
	/**
	 * Merges this WidgetDSO with the information from that WidgetDSO.
	 * 
	 * @param that
	 */
	public void mergeWith(WidgetDSO that) {
		log.info("Merging ");
		//right now only merging options. the widget itself has no other
		// information that can be merged
		
		/*
		 * Check options that have been deleted in that and... delete them in this 
		 */
		Iterator<WidgetOptionDSO> it = this.options.iterator();
		while (it.hasNext()) {
			WidgetOptionDSO next = it.next();
			if (!that.getWidgetOptionsAsArrayList().contains(next)) {
				log.info("Deleting from " + this.toString() + " unused option " + it.toString());
				it.remove();	
			} 
		}
		
		/*
		 * Add new options to this
		 */
		it = that.getWidgetOptionsAsArrayList().iterator();
		while (it.hasNext()) {
			WidgetOptionDSO next = it.next();
			if (!this.options.contains(next)) {
				log.info("Adding to " + this.toString() + " new option " + next.toString());
				this.addWidgetOption(next);	
			} 
		}
		
	}
}
