package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.instantplaces.im.server.rest.ErrorREST;
import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetOptionREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.shared.WidgetOption;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.comm.InputRequest;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.PlaceDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;


public class WidgetResource extends GenericResource {

		
	@Override
	protected Object doPut(Object in) {
		String errorMessage =  "Put not allowed. Sorry, only GET or POST methods allowed for this resource.";
		
		Log.get().error(errorMessage);

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}

	@Override
	protected Object doPost(Object in) {
		Log.get().debug("Responding to Post request.");
				
		WidgetREST receivedWidgetREST = (WidgetREST)in;
		
		WidgetDSO receivedWidgetDSO = receivedWidgetREST.toDSO();
		
		//TODO: Check consistency of query params and received widget data
		 
		PlaceDSO existingPlaceDSO = null;
		ApplicationDSO existingApplicationDSO = null;
		
		/*
		 * Try to fetch the widget from the data store (it should not exist)
		 */
		WidgetDSO storedWidgetDSO = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, receivedWidgetREST.getId());
		
		if ( null != storedWidgetDSO ) { 
			/* 
			 * Widget exists in data store so merge
			 */
			storedWidgetDSO.mergeWith(receivedWidgetDSO);
			
		} else {
			/*
			 * Widget does not exist so create and store 
			 */
		
			storedWidgetDSO = receivedWidgetDSO;
			
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
		    
		    existingApplicationDSO.addWidget(storedWidgetDSO);
		    storedWidgetDSO.setApplication(existingApplicationDSO);
	    
		    
		    
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
	    
		}
	  
		//TODO: Assign reference codes correctly
		storedWidgetDSO.copySuggestedReferenceCodesToReferenceCodes();
		
		
	
		/*
		 * Return the complete widget back.
		 */
		return storedWidgetDSO.toREST();
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
			ArrayList<WidgetDSO> widgets = WidgetDSO.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			
			if ( widgets != null ) {
				
				/*
				 * Convert all to WidgetREST
				 */
				ArrayList<WidgetREST> widgetsREST = new ArrayList<WidgetREST>();
				for ( WidgetDSO widgetDSO : widgets ) {
					widgetsREST.add(widgetDSO.toREST());
				}
				/*
				for (int i = 0; i < widgets.length; i++) {
					widgetsREST.add(widgets[i].toREST());
				}*/
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
	protected Object doDelete() {
		Log.get().debug("Responding to DELETE request.");
		
		if (this.widgetId != null) { // Delete the specified widget
			
			/*
			 * Fetch the widget from the data store 
			 */
			WidgetDSO widget = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, this.widgetId);
			

			if (widget == null) {
				String errorMessage =  "The specified widget was not found.";
				Log.get().warn(errorMessage);
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
			} else {
				Log.get().debug("Widget found: " + widget.toString());
				
				/*
				 * Delete the widget.
				 * 1. Delete it from the application
				 * 2. Delete from data store.
				 */
				widget.getApplication().removeWidget(widget);
				this.pm.deletePersistent(widget);
			}
			
			
		} else { //Delete all widgets from this app!
			
			/*
			 * Fetch the app from the store 
			 */
			ApplicationDSO app = ApplicationDSO.getApplicationDSO(this.pm, this.placeId, this.appId);
			
			if ( null == app ) { // app doesn't exist, throw error
				String errorMessage =  "The specified application was not found.";
				Log.get().warn(errorMessage);
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
				
			} else {
				Log.get().debug("Deleting all widgets from " + app.toString());
				/*
				 * Delete everything!
				 */
				Iterator<WidgetDSO> it = app.getWidgets().iterator();
				while (it.hasNext()) {
					WidgetDSO next = it.next();
					it.remove(); 
					this.pm.deletePersistent(next);
				}
			}
		}
		return null;
	}
	

	@Override
	protected Class getResourceClass() {
		return WidgetREST.class;
	}


	
	
	
}
