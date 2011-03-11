package org.instantplaces.interactionmanager.server.resource;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.instantplaces.interactionmanager.server.Log;
import org.instantplaces.interactionmanager.server.PMF;
import org.instantplaces.interactionmanager.server.dso.ApplicationDSO;
import org.instantplaces.interactionmanager.server.dso.PlaceDSO;
import org.instantplaces.interactionmanager.server.dso.WidgetDSO;
import org.instantplaces.interactionmanager.server.dso.WidgetOptionDSO;
import org.instantplaces.interactionmanager.server.rest.ErrorREST;
import org.instantplaces.interactionmanager.server.rest.WidgetOptionREST;
import org.instantplaces.interactionmanager.server.rest.WidgetREST;
import org.instantplaces.interactionmanager.shared.WidgetOption;
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
	protected Object doPost(Object in) {
		WidgetREST widgetREST = (WidgetREST)in;
	
		Log.get().debug("Received from client: " + widgetREST.toString());
		
		WidgetDSO storedWidgetDSO = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, widgetREST.getId());
		
		/*
		 * Check if widget already exists in the data store
		 */
		if (storedWidgetDSO == null) {
			return new ErrorREST(widgetREST, "Widget does not exist. Use PUT to... put.");
		} 
		
		WidgetDSO receivedWidgetDSO = widgetREST.toDSO();
		log.info("Converted from REST" + receivedWidgetDSO.toString());
		
		storedWidgetDSO.mergeWith(receivedWidgetDSO);
		
		log.info("Merged Widget " + storedWidgetDSO.toString());
		/*
		// TODO: Check if options changed and generate correct references
		ArrayList<WidgetOptionDSO> widgetOptionsDSO = widget.getWidgetOptionsAsArrayList();
		for (WidgetOptionREST woREST : widgetREST.getWidgetOptions()) {
			WidgetOptionDSO woDSO = new WidgetOptionDSO(woREST.getId(), woREST.getSuggestedReferenceCode(), woREST.getReferenceCode(), null);
			if ( widgetOptionsDSO.contains(woDSO) ) {
				WidgetOptionDSO existing = widgetOptionsDSO.get(widgetOptionsDSO.indexOf(woDSO));
				//existing.setId(woREST.getId())
			}
			
			//if (woREST.getId())
		}*/
		
		/*
		for (WidgetOption wo : widgetREST.getWidgetOptions()) {
			wo.setReferenceCode(wo.getSuggestedReferenceCode());
		}*/
		
		WidgetREST toClient = storedWidgetDSO.toREST();
		log.info(toClient.toString());
		return toClient;
	}

	@Override
	protected Object doPut(Object in) {
		
		//PersistenceManager pm = PMF.get().getPersistenceManager();
		
		WidgetREST widgetREST = (WidgetREST)in;
		
		 
		PlaceDSO place = null;
		ApplicationDSO application = null;
		WidgetDSO widget = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, widgetREST.getId());
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
	    place = PlaceDSO.getPlaceDSO(this.pm, this.placeId);
	    if (null == place) {
	    	log.info("The place was not found. Creating new...");
	        place = new PlaceDSO(this.placeId, null);
	    } 
	    
	    
	    /*
	     * Get the Application from the store. Create one if it does not exist yet.
	     */
	    application = ApplicationDSO.getApplicationDSO(this.pm, this.placeId, this.appId);
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
		
		
		/*
		 * Return the complete widget back.
		 */
		return widget.toREST();
	}

	@Override
	protected Object doGet() {
		if (this.widgetId != null) { //return the widget
			WidgetDSO widget = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, this.widgetId);
			if (widget == null) {
				this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return new ErrorREST(null, "Resource not found.");
			} else {
				return widget.toREST();
			}
			
		} else {
			WidgetDSO[] widgets = WidgetDSO.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
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
