package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;
import org.instantplaces.im.server.rest.RestConverter;
import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.DsoConverter;
import org.instantplaces.im.server.dso.DsoFetcher;
import org.instantplaces.im.server.dso.PlaceDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;

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
		long start = System.currentTimeMillis();
		Log.get().debug("Responding to Post request.");
		
		PlaceDSO existingPlaceDSO = null;
		ApplicationDSO existingApplicationDSO = null;
		WidgetArrayListREST receivedWidgetListREST = (WidgetArrayListREST)in;
		HashSet<String> receivedWidgetsSet = new HashSet<String>();
		for ( WidgetREST w : receivedWidgetListREST.widgets ) {
			receivedWidgetsSet.add(w.getWidgetId());
		}
		
		/*
		 * Get the Place from the store. Create one if it does not exist yet
		 */
		this.beginTransaction();
	    existingPlaceDSO = DsoFetcher.getPlaceFromDSO(this.pm, this.placeId);
	    if (null == existingPlaceDSO) {
	    	Log.get().info("The specified place " + this.placeId + " was not found. Creating new...");
	        existingPlaceDSO = new PlaceDSO(this.placeId, null);
	    } 
	    this.pm.makePersistent(existingPlaceDSO);
	       
	    /*
	     * Get the Application from the store. Create one if it does not exist yet.
	     */

	    existingApplicationDSO = DsoFetcher.getApplicationDSO(this.pm, this.placeId, this.appId);
	    if (null == existingApplicationDSO) {
	    	Log.get().info("The specified application " + this.appId + "was not found. Creating new...");
	        existingApplicationDSO = new ApplicationDSO(existingPlaceDSO, this.appId);
	        //existingApplicationDSO.setPlaceId(existingPlaceDSO.getPlaceId());
	        //existingApplicationDSO.setKey(KeyFactory.createKey(existingPlaceDSO.getKey(), ApplicationDSO.class.getSimpleName(),  this.appId));
	    }
	    this.pm.makePersistent(existingApplicationDSO);

	    
	    ReferenceCodeGenerator rcg = existingPlaceDSO.getCodeGenerator();
	    ArrayList<WidgetDSO> widgetsFromApplication = DsoFetcher.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
	    ArrayList<WidgetOptionDSO> widgetOptionsFromApplication = DsoFetcher.getWidgetOptionFromDSO(this.pm, this.placeId, this.appId);
	    

	    /*
	     * Throw away the widgets and options from the store that do not match the new ones
	     */
	    Iterator<WidgetDSO> wIt = widgetsFromApplication.iterator();
	    while ( wIt.hasNext() ) {
	    	WidgetDSO next = wIt.next();
	    	if ( !receivedWidgetsSet.contains(next.getWidgetId()) ) {
	    		wIt.remove();
	    	}
	    }
	    
	    Iterator<WidgetOptionDSO> woIt = widgetOptionsFromApplication.iterator();
	    while ( woIt.hasNext() ) {
	    	WidgetOptionDSO next = woIt.next();
	    	if ( !receivedWidgetsSet.contains(next.getWidgetId()) ) {
	    		woIt.remove();
	    	}
	    }
	    
	    
	    /*
	     * Now add the new widgets, or merge if they already exist.
	     * The result will be a set of widgets to add/modify, a set of widgetoptions to add/modify
	     * and, possibly, a set of widgetOptions to delete. 
	     * 
	     */   
	    // rebuild the widget->widgetOption tree
	    HashMap<String, WidgetDSO> widgetMap = new HashMap<String, WidgetDSO>();
	    for ( WidgetDSO w : widgetsFromApplication ) {
	    	widgetMap.put(w.getWidgetId(), w);
	    }
	    
	    for ( WidgetOptionDSO wo : widgetOptionsFromApplication ) {
	    	if ( widgetMap.containsKey(wo.getWidgetId()) ) {
	    		widgetMap.get(wo.getWidgetId()).addWidgetOption(wo);
	    	}
	    }
	    
	    
		
		
		ArrayList<WidgetOptionDSO> toDelete = new ArrayList<WidgetOptionDSO>();
		ArrayList<WidgetOptionDSO> toAdd = new ArrayList<WidgetOptionDSO>();
		
		for ( WidgetREST receivedWidget : receivedWidgetListREST.widgets ) {
			
			/*
			 * See if this widget already exists
			 */
			WidgetDSO storedWidget = widgetMap.get( receivedWidget.getWidgetId() );
			
			if ( null != storedWidget ) { 
				/* 
				 * Widget exists in data store so merge
				 */
				WidgetDSO wDSO = DsoConverter.widgetDSOfromRest(existingApplicationDSO, receivedWidget);
				
				toDelete.addAll( storedWidget.mergeOptionsToDelete(wDSO) );
				toAdd.addAll( storedWidget.mergeOptionsToAdd(wDSO) );

			} else {
				
				storedWidget = DsoConverter.widgetDSOfromRest(existingApplicationDSO, receivedWidget);
		
				toAdd.addAll( storedWidget.getWidgetOptions() );
				
				widgetsFromApplication.add(storedWidget);
			}
			
			storedWidget.assignReferenceCodes(rcg); 
		}
		
		/*
		 * We are going to return this
		 */
		WidgetArrayListREST walr = RestConverter.widgetArrayListFromDso(widgetsFromApplication);
		
		
		
		/*
	     * Recycle the codes and delete the input from the options that the widget is no longer using
	     */
		for ( WidgetOptionDSO woDso : toDelete ) {
			rcg.recycleCode(woDso.getReferenceCode());
			DsoFetcher.deleteWidgetInputDSO(this.pm, this.placeId, this.appId, woDso.getWidgetId(), woDso.getWidgetOptionId());
		}
		
		/*
		 * Make everything persistent
		 */
		Log.get().debug(widgetsFromApplication.size());
		Log.get().debug(widgetOptionsFromApplication.size());
		Log.get().debug(toAdd.size());
		Log.get().debug(toDelete.size());
		try {
			
			this.pm.makePersistentAll(widgetsFromApplication);
			this.pm.makePersistentAll(widgetOptionsFromApplication);
			this.pm.makePersistentAll(toAdd);
			this.pm.makePersistent(rcg);
			this.pm.deletePersistentAll(toDelete);
		} catch (Exception e) {
			String errorMessage = "Sorry, could not make persist objects: " + e.getMessage();
			
			this.rollbackAndThrowException(new ResourceException(Status.SERVER_ERROR_INTERNAL, errorMessage));
		}
		
		
		this.commitTransaction();
		
		long end = System.currentTimeMillis();
		Log.get().debug("Time: " + (end-start));
		return walr;
	}

	
	@Override
	protected Object doGet() {
		Log.get().debug("Responding to GET request.");
		
		Object toReturn = null;
		this.beginTransaction();
		
		
		if (this.widgetId != null) { 
			/* 
			 * Return the specified widget
			 */
			WidgetDSO widget = DsoFetcher.getWidgetFromDSO(this.pm, this.placeId, this.appId, this.widgetId);
			this.commitTransaction();
			
			if (widget == null) {
				String errorMessage =  "The specified widget was not found.";
				this.rollbackAndThrowException(new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage));
			} else {
				Log.get().debug("Widget found: " + widget.toString());
				toReturn =  RestConverter.widgetRestFromDso(widget);
			}
			
		} else {
			/*
			 * Return the list of widgets
			 */
			ArrayList<WidgetDSO> widgets = DsoFetcher.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			this.commitTransaction();
			
			if ( widgets != null ) {
				
				/*
				 * Convert all to WidgetREST
				 */
				toReturn =  RestConverter.widgetArrayListFromDso(widgets);
				
			} else {
				Log.get().debug("Could not find any widget for the specified application");
				toReturn = new WidgetArrayListREST();
			}
			
		}
		
		
		return toReturn;
		
	}
	
	private WidgetArrayListREST deleteSpecifiedWidgets(String [] widgetsToDelete) {
		/*
		 * To be returned
		 */
		WidgetArrayListREST walr = new WidgetArrayListREST();
		walr.widgets = new ArrayList<WidgetREST>();
		if ( null != widgetsToDelete && widgetsToDelete.length > 0 ) {
			this.beginTransaction();
			for ( String widgetId : widgetsToDelete ) {  
				/*
				 * Fetch the widget from the data store 
				 */
				DsoFetcher.deleteWidgetFromDSO(this.pm, this.placeId, this.appId, widgetId);
				
				WidgetREST toReturn = new WidgetREST();
				toReturn.setPlaceId(this.placeId);
				toReturn.setApplicationId(this.appId);
				toReturn.setWidgetId(widgetId);
				
				
				walr.widgets.add(toReturn);
			}
			this.commitTransaction();
		}
		return walr;
	}
	
	
	private WidgetArrayListREST deleteAllWidgets(boolean volatileOnly) {
		Log.get().debug("Deleting all "+(volatileOnly?" volatile ":"")+"widgets from " + this.appId );
		
		WidgetArrayListREST walr = new WidgetArrayListREST();
		this.beginTransaction();
		if ( volatileOnly ) {
			ArrayList<WidgetDSO> widgetsToDelete = DsoFetcher.getVolatileWidgetsFromDSO(this.pm, this.placeId, this.appId);
			walr = RestConverter.widgetArrayListFromDso(widgetsToDelete);
			
			for ( WidgetDSO widget : widgetsToDelete ) {
				DsoFetcher.deleteWidgetFromDSO(this.pm, this.placeId, this.appId, widget.getWidgetId());
			}
		} else {
			ArrayList<WidgetDSO> widgetsToDelete = DsoFetcher.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			walr = RestConverter.widgetArrayListFromDso(widgetsToDelete);
			DsoFetcher.deleteWidgetFromDSO(this.pm, this.placeId, this.appId);
		}
			
		this.commitTransaction();
			
		return walr;
			
		
	}

	
	
	@Override
	protected Object doDelete() {
		
		Log.get().debug("Responding to DELETE request.");
		
		
		
		
		
		if (this.widgetId != null) { // Delete the specified widget
			
			return this.deleteSpecifiedWidgets(new String[] {this.widgetId});
		} else { // delete the widgets passed in the url param
		
			String widgetsToDeleteParam =  this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("widgets", "");
	
			if ( widgetsToDeleteParam.length() > 0 ) {
				return this.deleteSpecifiedWidgets(widgetsToDeleteParam.split(","));
				
			} else { // delete all widgets from the app
				boolean volatileOnly = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("volatileonly", "true").equalsIgnoreCase("true");
				
				return this.deleteAllWidgets(volatileOnly);
				
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
