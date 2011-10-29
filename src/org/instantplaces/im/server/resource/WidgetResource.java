package org.instantplaces.im.server.resource;

import java.util.ArrayList;


import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.PlaceDSO;
import org.instantplaces.im.server.dso.WidgetDSO;

import org.restlet.data.Method;
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
		
		PlaceDSO existingPlaceDSO = null;
		ApplicationDSO existingApplicationDSO = null;
		
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
	    
	   
		
		
		WidgetArrayListREST receivedWidgetListREST = (WidgetArrayListREST)in;
		
		ArrayList<WidgetDSO> receivedWidgetListDSO = WidgetDSO.fromREST(receivedWidgetListREST);
		
		/*
		 * The list of widgets, with ref codes, that will be sent back to the client
		 */
		ArrayList<WidgetDSO> storedWidgetListDSO = new ArrayList<WidgetDSO>();
		
		for ( WidgetDSO receivedWidgetDSO : receivedWidgetListDSO ) {
			/*
			 * Try to fetch the widget from the data store (it should not exist)
			 */
			WidgetDSO storedWidgetDSO = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, receivedWidgetDSO.getWidgetId());
			
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
				
			    
			    existingApplicationDSO.addWidget(storedWidgetDSO);
			    storedWidgetDSO.setApplication(existingApplicationDSO);
			    
			  
			    // Assign the reference codes to the widget.
			    storedWidgetDSO.assignReferenceCodes();
			}
			storedWidgetListDSO.add(storedWidgetDSO);
		}
		
		/*
	     * Make the objects persistent. This is only necessary in case the place does not yet exist
	     * but it does harm to call it everytime...
	     */
		try {
			existingPlaceDSO = pm.makePersistent(existingPlaceDSO);
		} catch (Exception e) {
			String errorMessage =  "Sorry, could not make the new place persistent.";
			Log.get().error(errorMessage);
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, errorMessage);
		}
		
		/*
		 * Send back the list of added widgets
		 */
		return WidgetArrayListREST.fromDSO(storedWidgetListDSO);
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
				String errorMessage =  "The specified widget was not found.";
				Log.get().warn(errorMessage);
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
			} else {
				Log.get().debug("Widget found: " + widget.toString());
				return WidgetREST.fromDSO(widget);
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
					widgetsREST.add(WidgetREST.fromDSO(widgetDSO));
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
				return new WidgetArrayListREST();
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
				/*String errorMessage =  "The specified widget was not found.";
				Log.get().warn(errorMessage);
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);*/
				WidgetREST toReturn = new WidgetREST();
				toReturn.setPlaceId(this.placeId);
				toReturn.setApplicationId(this.appId);
				toReturn.setWidgetId(this.widgetId);
				
				
				WidgetArrayListREST walr = new WidgetArrayListREST();
				walr.widgets = new ArrayList<WidgetREST>();
				walr.widgets.add(toReturn);
				return walr;
				
			} else {
				Log.get().debug("Widget found: " + widget.toString());
				
				WidgetREST toReturn = WidgetREST.fromDSO(widget);
				
				/*
				 * Delete the widget from the application
				 */
				widget.getApplication().removeWidget(widget);
				
				WidgetArrayListREST walr = new WidgetArrayListREST();
				walr.widgets = new ArrayList<WidgetREST>();
				walr.widgets.add(toReturn);
				return walr;
			}
			
			
		} else {
			String widgetsToDelete =  this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("widgets", "");
			
			if ( widgetsToDelete.length() > 0 ) {
				String widgetIds[] = widgetsToDelete.split(",");
				
				WidgetArrayListREST walr = new WidgetArrayListREST();
				walr.widgets = new ArrayList<WidgetREST>();
				
				for (String widgetId : widgetIds) {
					/*
					 * Fetch the widget from the data store 
					 */
					WidgetDSO widget = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, widgetId);
					
	
					if (widget == null) {
						/*String errorMessage =  "The specified widget was not found.";
						Log.get().warn(errorMessage);
						throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);*/
						WidgetREST toReturn = new WidgetREST();
						toReturn.setPlaceId(this.placeId);
						toReturn.setApplicationId(this.appId);
						toReturn.setWidgetId(widgetId);
						
						
						
						walr.widgets.add(toReturn);
						
						
					} else {
						Log.get().debug("Widget found: " + widget.toString());
						
						WidgetREST toReturn = WidgetREST.fromDSO(widget);
						
						/*
						 * Delete the widget from the application
						 */
						widget.getApplication().removeWidget(widget);
						
						
						walr.widgets.add(toReturn);
						
					}	
				}
				
				return walr;
				
			} else {  //Delete all widgets from this app!
				
				boolean volatileOnly = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("volatileonly", "true").equalsIgnoreCase("true");
				
				/*
				 * Fetch the app from the store 
				 */
				ApplicationDSO app = ApplicationDSO.getApplicationDSO(this.pm, this.placeId, this.appId);
				
				if ( null == app ) { // app doesn't exist, throw error
					String errorMessage =  "The specified application was not found.";
					Log.get().warn(errorMessage);
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
					
				} else {
					Log.get().debug("Deleting all "+(volatileOnly?" volatile ":"")+"widgets from " + app.toString());
					
					
					/*
					 * Convert all app widgets to WidgetREST, so that we can return them
					 */
					ArrayList<WidgetREST> widgetsREST = new ArrayList<WidgetREST>();
					for ( WidgetDSO widgetDSO : app.getWidgets() ) {
						if ( volatileOnly ){
							if ( widgetDSO.isVolatileWidget() ) {
								widgetsREST.add(WidgetREST.fromDSO(widgetDSO));
							}
						} else {
							widgetsREST.add(WidgetREST.fromDSO(widgetDSO));
						}
					}
					
					WidgetArrayListREST walREST = new WidgetArrayListREST();
					walREST.widgets = widgetsREST;
					
					
					/*
					 * Delete everything!
					 */
					if ( volatileOnly ) {
						app.removeVolatileWidgets();
					} else {
						app.removeAllWidgets();
					}
					
					return walREST;
				}
			}
		}
		
	}
	

	@Override
	protected Class getResourceClass() {
		if ( this.getMethod().equals(Method.DELETE) ) {
			return WidgetREST.class;
		} else {
			return WidgetArrayListREST.class;
		}
	}


	
	
	
}
