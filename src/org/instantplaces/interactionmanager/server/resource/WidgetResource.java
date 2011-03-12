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
import org.instantplaces.interactionmanager.server.rest.WidgetArrayListREST;
import org.instantplaces.interactionmanager.server.rest.WidgetOptionREST;
import org.instantplaces.interactionmanager.server.rest.WidgetREST;
import org.instantplaces.interactionmanager.shared.WidgetOption;
import org.restlet.data.Status;


public class WidgetResource extends GenericResource {

	private String placeId;
	private String appId;
	private String widgetId;
	
	
	
	@Override 
	public void doInit() {
		super.doInit();
		
		placeId = (String)this.getRequestAttributes().get("placeid");
		appId = (String)getRequestAttributes().get("appid");
		widgetId = (String)getRequestAttributes().get("widgetid");
		
		Log.get().debug("Client request query parameters: placeid: " + placeId + " appid: " + appId + " widgetId: " + widgetId);
	}
	
	@Override
	protected Object doPost(Object in) {
		Log.get().debug("Responding to POST request.");
		
		WidgetREST receivedWidgetREST = (WidgetREST)in;
	
		Log.get().debug("Data received from client: " + receivedWidgetREST.toString());
		
		//TODO: Check consistency of query params and received widget data
		
		/*
		 * Fetch the widget from the data store.
		 */
		WidgetDSO storedWidgetDSO = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, receivedWidgetREST.getId());
		
		/*
		 * If the widget does not exist yet throw an error back to the client 
		 */
		if (storedWidgetDSO == null) {
			Log.get().debug("Client specified an non-existing widget to update.");
			return new ErrorREST(receivedWidgetREST, "Widget does not exist. Use PUT to... put.");
		}
		
		/*
		 * Convert the received REST data to DSO
		 */
		WidgetDSO receivedWidgetDSO = receivedWidgetREST.toDSO();
	
		
		/*
		 * And merge it with the existing widget in the data store.
		 */
		storedWidgetDSO.mergeWith(receivedWidgetDSO);
		
		
		
		/*
		for (WidgetOption wo : widgetREST.getWidgetOptions()) {
			wo.setReferenceCode(wo.getSuggestedReferenceCode());
		}*/
		
		/*
		 * Convert the updated widget back to REST to send it to the client 
		 */
		WidgetREST toClient = storedWidgetDSO.toREST();
		
		
		return toClient;
	}

	@Override
	protected Object doPut(Object in) {
		Log.get().debug("Responding to PUT request.");
				
		WidgetREST receivedWidgetREST = (WidgetREST)in;
		
		//TODO: Check consistency of query params and received widget data
		 
		PlaceDSO existingPlaceDSO = null;
		ApplicationDSO existingApplicationDSO = null;
		
		/*
		 * Try to fetch the widget from the data store (it should not exist)
		 */
		WidgetDSO existingWidgetDSO = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, receivedWidgetREST.getId());
		
		/*
		 * If the widget already exists in the data store throw an error back to the
		 * client
		 */
		if (existingWidgetDSO != null) {
			Log.get().debug("Client specified an existing widget to put.");
			this.setStatus(Status.CLIENT_ERROR_CONFLICT);
			return new ErrorREST(receivedWidgetREST, "Widget already exists. Use POST to update.");
		} 
		
		/*
		 * Convert the received REST widget to a DSO one 
		 */
		existingWidgetDSO = receivedWidgetREST.toDSO(); //new WidgetDSO(receivedWidgetREST.getId(), null, null);
		
		
		/*
		 * Get the Place from the store. Create one if it does not exist yet
		 */
	    existingPlaceDSO = PlaceDSO.getPlaceDSO(this.pm, this.placeId);
	    if (null == existingPlaceDSO) {
	    	Log.get().debug("The specified place was not found. Creating new...");
	        existingPlaceDSO = new PlaceDSO(this.placeId, null);
	    } 
	    
	    
	    /*
	     * Get the Application from the store. Create one if it does not exist yet.
	     */
	    existingApplicationDSO = ApplicationDSO.getApplicationDSO(this.pm, this.placeId, this.appId);
	    if (null == existingApplicationDSO) {
	    	Log.get().debug("The specified application was not found. Creating new...");
	        existingApplicationDSO = new ApplicationDSO(this.appId, existingPlaceDSO, null);
	    }
	    
	    /*
	     * Set the place for the application and add the application to the place. Set the application
	     * for the widget and add the widget to the application.
	     * These are (should be) idempotent operations.
	     */
	    existingApplicationDSO.setPlace(existingPlaceDSO);
	    existingPlaceDSO.addApplication(existingApplicationDSO);
	    
	    existingApplicationDSO.addWidget(existingWidgetDSO);
	    existingWidgetDSO.setApplication(existingApplicationDSO);
	    
	    //TODO: Fill the rest of the widget fields (ref codes)
	    
	    /*
	     * Make the objects persistent. This is only necessary in case the place does not yet exist
	     * but it does harm to call it everytime...
	     */
		try {
			pm.makePersistent(existingPlaceDSO);
		} catch (Exception e) {
			Log.get().error("Could not make the new place persistent.");
			e.printStackTrace();

			this.setStatus(Status.SERVER_ERROR_INTERNAL);
			return new ErrorREST(receivedWidgetREST, "Could not make the new place persistent." );
		}
		
		
		/*
		 * Return the complete widget back.
		 */
		return existingWidgetDSO.toREST();
	}

	@Override
	protected Object doGet() {
		Log.get().debug("Responding to GET request.");
		
		if (this.widgetId != null) { 
			/* 
			 * Return the specified widget
			 */
			WidgetDSO widget = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, this.widgetId);
			
			if (widget == null) {
				Log.get().debug("The specified widget was not found. ");
				
				this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return new ErrorREST(null, "Widget not found.");
			} else {
				Log.get().debug("Widget found: " + widget.toString());
				return widget.toREST();
			}
			
		} else {
			/*
			 * Return the list of widgets
			 */
			WidgetDSO[] widgets = WidgetDSO.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			
			if ( widgets != null ) {
				
				/*
				 * Convert all to WidgetREST
				 */
				ArrayList<WidgetREST> widgetsREST = new ArrayList<WidgetREST>();
				for (int i = 0; i < widgets.length; i++) {
					widgetsREST.add(widgets[i].toREST());
				}
				/*
				 * We put the ArrayList into a custom WidgetArrayListREST
				 * because I couldn't make JAXB work otherwise... :(
				 */
				WidgetArrayListREST walREST = new WidgetArrayListREST();
				walREST.widgets = widgetsREST;
				
				return walREST;
			} else {
				Log.get().debug("Could not find any widget for the specified application");
				return null;
			}
			
		}
		
	}

	@Override
	protected Class getResourceClass() {
		return WidgetREST.class;
	}
	
	
	
}
