package org.instantplaces.interactionmanager.server.resource;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.instantplaces.interactionmanager.server.PMF;
import org.instantplaces.interactionmanager.server.dso.ApplicationDSO;
import org.instantplaces.interactionmanager.server.dso.PlaceDSO;
import org.instantplaces.interactionmanager.server.dso.WidgetDSO;
import org.instantplaces.interactionmanager.server.rest.ErrorREST;
import org.instantplaces.interactionmanager.server.rest.WidgetREST;
import org.restlet.data.Status;


public class WidgetResource extends InstantPlacesGenericResource {

	private String placeId;
	private String appId;
	private String widgetId;
	
	
	
	@Override 
	public void doInit() {
		super.doInit();
		
		placeId = (String)this.getRequestAttributes().get("placeid");
		appId = (String)getRequestAttributes().get("appid");
		widgetId = (String)getRequestAttributes().get("widgetid");
		
		log.info("Request info: placeid: " + placeId + " appid: " + appId + " widgetId: " + widgetId);
	}
	
	@Override
	protected Object doPost(Object incoming) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object doPut(Object in) {
		
		//PersistenceManager pm = PMF.get().getPersistenceManager();
		
		WidgetREST widgetREST = (WidgetREST)in;
		
		 
		PlaceDSO place = null;
		ApplicationDSO application = null;
		WidgetDSO widget = this.getWidgetDO(this.placeId, this.appId, widgetREST.getId());
		//TODO: Check if url parameters match widgetREST parameters
		
		/*
		 * Check if widget already exists in the data store
		 */
		if (widget != null) {
			return new ErrorREST(widgetREST, "Widget already exists. Use POST to update.");
		} else {
			widget = new WidgetDSO(widgetREST.getId(), null, null);
		}
		
		/*
		 * Get the Place from the store. Create one if it does not exist yet
		 */
	    place = getPlaceDO(this.placeId);
	    if (null == place) {
	    	log.info("The place was not found. Creating new...");
	        place = new PlaceDSO(this.placeId, null);
	    } 
	    place.debug();
	    
	    /*
	     * Get the Application from the store. Create one if it does not exist yet.
	     */
	    application = getApplicationDO(this.placeId, this.appId);
	    if (null == application) {
	    	log.info("The application was not found. Creating new...");
	        application = new ApplicationDSO(this.appId, place, null);
	    }
	    
	    /*
	     * Set the place for the application and add the application to the place. Set the application
	     * for the widget and add the widget to the application.
	     * These are (should be) idempotent operations.
	     */
	    application.setPlace(place);
	    place.addApplication(application);
	    
	    application.addWidget(widget);
	    widget.setApplication(application);
	    
	    //TODO: Fill the rest of the widget fields (ref codes)
	    
	    /*
	     * Make the objects persistent. This is only necessary in case the place does not yet exist
	     * but it does harm to call it everytime...
	     */
		try {
			pm.makePersistent(place);
			//pm.makePersistent(application);
			//pm.makePersistent(widget);
		} finally {
			//pm.close();
		}
		
		place.debug();
		/*
		 * Return the complete widget back.
		 */
		return widget.toREST();
	}

	@Override
	protected Object doGet() {
		if (this.widgetId != null) { //return the widget
			WidgetDSO widget = getWidgetDO(this.placeId, this.appId, this.widgetId);
			if (widget == null) {
				this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return new ErrorREST(null, "Resource not found.");
			} else {
				return widget.toREST();
			}
			
		} else {
			WidgetDSO[] widgets = this.getWidgetsDO(this.placeId, this.appId);
			
			if ( widgets != null ) {
				ArrayList<WidgetREST> widgetsREST = new ArrayList<WidgetREST>();
				for (int i = 0; i < widgets.length; i++) {
					widgetsREST.add(widgets[i].toREST());
				}
				return widgetsREST;
			} else {
				return null;
			}
			
		}
		
	}

	@Override
	protected Class getResourceClass() {
		return WidgetREST.class;
	}
	
	
	
}
