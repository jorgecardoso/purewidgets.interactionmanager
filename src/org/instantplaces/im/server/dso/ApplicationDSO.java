package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.Iterator;
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

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ApplicationDSO  {
	
	public static final int MAXIMUM_ACTIVITY_INTERVAL = 30*1000; // milliseconds
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String applicationId;
	
	@Persistent
	private PlaceDSO place;
	
	@Persistent 
	private String placeId;
	
	
	@Persistent(mappedBy = "application")
	private ArrayList<WidgetDSO> widgets;
	
	/**
	 * The timestamp of the last request made by this app.
	 * This is used to determine if an application is still active or not.
	 */
	@Persistent 
	private long lastRequestTimestamp;
	
	public ApplicationDSO() {
		this(null, null, null);
	}
	
	public ApplicationDSO(String id, PlaceDSO place, ArrayList<WidgetDSO> widgets) {
		this.applicationId = id;
		this.place = place;
		
		if (widgets != null) {
			this.widgets = widgets;
		} else {
			this.widgets = new ArrayList<WidgetDSO>();
		}
		this.lastRequestTimestamp = System.currentTimeMillis();
	}
	
	
	public void setApplicationId(String appID) {
		this.applicationId = appID;
	}

	public String getApplicationId() {
		return this.applicationId;
	}

	public void setPlace(PlaceDSO place) {
		this.place = place;
		this.placeId = place.getPlaceId();
	}

	public PlaceDSO getPlace() {
		return this.place;
	}

	public void addWidget(WidgetDSO widget) {
		this.widgets.add(widget);
		
	}
	
	public void removeWidget(WidgetDSO widget) {
		if ( null != this.widgets ) {
			widget.recycleReferenceCodes();
			this.widgets.remove(widget);
			PersistenceManager pm = JDOHelper.getPersistenceManager(widget);
			pm.deletePersistent(widget);
		}
	}
	
	public void removeWidgets(ArrayList<WidgetDSO> widgets) {
		for (WidgetDSO widget : widgets) {
			this.widgets.remove(widget);
		}
		PersistenceManager pm = JDOHelper.getPersistenceManager(this);
		pm.deletePersistentAll(widgets);
	}
	
	public boolean removeVolatileWidgets() {
		Iterator<WidgetDSO> it = this.widgets.iterator();
		ArrayList<WidgetDSO> toDelete = new ArrayList<WidgetDSO>();
		
		boolean any = false;
		while ( it.hasNext() ) {
			WidgetDSO widget = it.next();
			if (widget.isVolatileWidget()) {
				any = true;
				
				widget.recycleReferenceCodes();
				
				//PersistenceManager pm = JDOHelper.getPersistenceManager(widget);
				//pm.deletePersistent(widget);
				//it.remove();
				toDelete.add(widget);
			}
		}
		
		PersistenceManager pm = JDOHelper.getPersistenceManager(this);
		pm.deletePersistentAll(toDelete);
		
		for ( WidgetDSO widget : toDelete ) {
			this.widgets.remove(widget);
		}
		return any;
	}
	
	public boolean removeAllWidgets() {
		boolean any = false;
		Iterator<WidgetDSO> it = this.widgets.iterator();
		ArrayList<WidgetDSO> toDelete = new ArrayList<WidgetDSO>();
		while ( it.hasNext() ) {
			any = true;
			WidgetDSO widget = it.next();
			Log.get().debug("Deleting widget: " + widget.toString());
			widget.recycleReferenceCodes();
			
			
			
			//it.remove();
			toDelete.add(widget);
			
		}
		
		PersistenceManager pm = JDOHelper.getPersistenceManager(this);
		pm.deletePersistentAll(toDelete);
		
		for ( WidgetDSO widget : toDelete ) {
			this.widgets.remove(widget);
		}
		return any;
	}
	
	public WidgetDSO getWidget(String widgetId) {
	
		for ( WidgetDSO widget : this.widgets ) {
			if ( widget.getWidgetId().equals(widgetId) ) {
				return widget;
			}
		}
		return null;
	}
	
	public ArrayList<WidgetDSO> getWidgets() {
		return this.widgets;
	}
	
	

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	public String toString() {
		return "application(id: " + this.applicationId + ")";
	}
	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof ApplicationDSO) ) {
			return false;
		}
		return ((ApplicationDSO) app).getKey().equals(this.key);
	} 
	
	public static ArrayList<ApplicationDSO> getApplicationsDSO( PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching applications for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(ApplicationDSO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    
	    
	    try {
	    	List<ApplicationDSO> result = (List<ApplicationDSO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Applications not found.");
	    		
	    	} else {
	    		Log.get().debug("Found applications");
	    	}
	    	
	    	ArrayList<ApplicationDSO> toReturn = new ArrayList<ApplicationDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}	
	
	public static ApplicationDSO getApplicationDSO( PersistenceManager pm, String placeId, String applicationId ) {
		Log.get().debug("Fetching application Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
			
			Query query = pm.newQuery(ApplicationDSO.class);
		    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
		    query.declareParameters("String placeIdParam, String applicationIdParam");
		    query.setUnique(true);
		    
		    try {
		    	ApplicationDSO result = (ApplicationDSO) query.execute(placeId, applicationId);
		        if ( null != result ) {
		        	Log.get().debug("Found applications. Returning first.");
		        	
		        	return result;
		        } else {
		        	Log.get().debug("Application not found.");
		        }
		    } catch (Exception e) {
		    	Log.get().error("Could not access data store." + e.getMessage());
		    }  finally {
		        query.closeAll();
		    }
		    return null;
		}
	
	public void setLastRequestTimestamp(long lastRequestTimestamp) {
		this.lastRequestTimestamp = lastRequestTimestamp;
	}

	public long getLastRequestTimestamp() {
		return lastRequestTimestamp;
	}	

	public boolean isActive() {
		return (System.currentTimeMillis()-this.lastRequestTimestamp) < MAXIMUM_ACTIVITY_INTERVAL;
	}

	/**
	 * @return the placeId
	 */
	public String getPlaceId() {
		return placeId;
	}

	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
}
