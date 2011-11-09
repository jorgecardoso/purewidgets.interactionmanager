package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import org.instantplaces.im.server.rest.RestConverter;
import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.DaoConverter;
import org.instantplaces.im.server.dao.DsoFetcher;
import org.instantplaces.im.server.dao.PlaceDAO;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;

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
		
		PlaceDAO existingPlaceDSO = null;
		ReferenceCodeGeneratorDAO rcg = null;
		ApplicationDAO existingApplicationDSO = null;
		
		
		WidgetArrayListREST receivedWidgetListREST = (WidgetArrayListREST)in;
		
		
		/*
		 * Get the Place from the store. Create one if it does not exist yet
		 */
		DAO.beginTransaction();
	    existingPlaceDSO = DAO.getPlace(this.placeId);
	    if (null == existingPlaceDSO) {
	    	Log.get().info("The specified place " + this.placeId + " was not found. Creating new...");
	        existingPlaceDSO = new PlaceDAO(this.placeId);
	        DAO.putPlace(existingPlaceDSO);
	        
	        /*
	         * A new place needs a new ReferenceCodeGenerator
	         */
	        rcg = new ReferenceCodeGeneratorDAO(existingPlaceDSO);
	        DAO.putReferenceCodeGenerator(rcg);
	    } else {
	    	rcg = DAO.getReferenceCodeGenerator(this.placeId);
	    	Log.get().debug("Init: " + rcg.getCodes());
	    }
	   
	   
	    /*
	     * Get the Application from the store. Create one if it does not exist yet.
	     */
	    existingApplicationDSO = DAO.getApplication(this.placeId, this.appId);
	    if (null == existingApplicationDSO) {
	    	Log.get().info("The specified application " + this.appId + "was not found. Creating new...");
	        existingApplicationDSO = new ApplicationDAO(existingPlaceDSO, this.appId);
	        DAO.putApplication(existingApplicationDSO);
	    }   
		
		
		ArrayList<WidgetDAO> widgetsAdded = new ArrayList<WidgetDAO>();
		
		for ( WidgetREST receivedWidget : receivedWidgetListREST.widgets ) {
			
			WidgetDAO widget = DAO.getWidget(this.placeId, this.appId, receivedWidget.getWidgetId());
			
			if ( null != widget ) { 
				/* 
				 * Widget exists in data store so merge
				 */
				WidgetDAO wDSO = DaoConverter.widgetDSOfromRest(existingApplicationDSO, receivedWidget);
				widget.mergeOptionsToAdd(wDSO);
				ArrayList<WidgetOptionDAO> optionsToDelete = widget.mergeOptionsToDelete(wDSO);
				
				for (WidgetOptionDAO option : optionsToDelete) {
					rcg.recycleCode(option.getReferenceCode());
				}
				
				DAO.deleteWidgetOptions(optionsToDelete);
				DAO.putWidgetOption( widget.getWidgetOptions() );
			} else {
				widget = DaoConverter.widgetDSOfromRest(existingApplicationDSO, receivedWidget);
				widget.assignReferenceCodes(rcg);
				DAO.putWidget( widget );
				DAO.putWidgetOption( widget.getWidgetOptions() );
			}
			
			widgetsAdded.add(widget);
			
			//storedWidget.assignReferenceCodes(rcg); 
		}
		
		
		/*
		 * We are going to return this
		 */
		WidgetArrayListREST walr = RestConverter.widgetArrayListFromDso(widgetsAdded);
		
		
		DAO.putReferenceCodeGenerator(rcg);
		Log.get().debug(rcg.getCodes());
		
		
		if ( !DAO.commitOrRollbackTransaction() ) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Could not commit transaction");
		}
		
		
		long end = System.currentTimeMillis();
		Log.get().debug("Time: " + (end-start));
		return walr;
	}

//	
//	@Override
//	protected Object doGet() {
//		Log.get().debug("Responding to GET request.");
//		
//		Object toReturn = null;
//		this.beginTransaction();
//		
//		
//		if (this.widgetId != null) { 
//			/* 
//			 * Return the specified widget
//			 */
//			WidgetDAO widget = DsoFetcher.getWidgetFromDSO(this.pm, this.placeId, this.appId, this.widgetId);
//			this.commitTransaction();
//			
//			if (widget == null) {
//				String errorMessage =  "The specified widget was not found.";
//				this.rollbackAndThrowException(new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage));
//			} else {
//				Log.get().debug("Widget found: " + widget.toString());
//				toReturn =  RestConverter.widgetRestFromDso(widget);
//			}
//			
//		} else {
//			/*
//			 * Return the list of widgets
//			 */
//			ArrayList<WidgetDAO> widgets = DsoFetcher.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
//			this.commitTransaction();
//			
//			if ( widgets != null ) {
//				
//				/*
//				 * Convert all to WidgetREST
//				 */
//				toReturn =  RestConverter.widgetArrayListFromDso(widgets);
//				
//			} else {
//				Log.get().debug("Could not find any widget for the specified application");
//				toReturn = new WidgetArrayListREST();
//			}
//			
//		}
//		
//		
//		return toReturn;
//		
//	}
//	
	
	private WidgetArrayListREST deleteSpecifiedWidgets(String [] widgetsToDelete) {
		/*
		 * To be returned
		 */
		WidgetArrayListREST walr = new WidgetArrayListREST();
		walr.widgets = new ArrayList<WidgetREST>();
		
		if ( null != widgetsToDelete && widgetsToDelete.length > 0 ) {
			
			DAO.beginTransaction();
			
			for ( String widgetId : widgetsToDelete ) {  
				/*
				 * Fetch the widget from the data store 
				 */
				DAO.deleteWidget(this.placeId, this.appId, widgetId);
				
				WidgetREST toReturn = new WidgetREST();
				toReturn.setPlaceId(this.placeId);
				toReturn.setApplicationId(this.appId);
				toReturn.setWidgetId(widgetId);
				
				
				walr.widgets.add(toReturn);
			}
			if ( !DAO.commitOrRollbackTransaction() ) {
				 throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Could not commit transaction");
				
			}
		}
		return walr;
	}
	
	
	private WidgetArrayListREST deleteAllWidgets(boolean volatileOnly) {
		Log.get().debug("Deleting all "+(volatileOnly?" volatile ":"")+"widgets from " + this.appId );
		
		WidgetArrayListREST walr = new WidgetArrayListREST();
		walr.widgets = new ArrayList<WidgetREST>();
		
		DAO.beginTransaction();
		
		ArrayList<WidgetDAO> widgetsToDelete = DAO.getWidgets(this.placeId, this.appId);
			
					//DsoFetcher.getVolatileWidgetsFromDSO(this.pm, this.placeId, this.appId);
	
		//TODO: Recycle reference codes
		for ( WidgetDAO widget : widgetsToDelete ) {
			if ( volatileOnly ) {
				//DsoFetcher.deleteWidgetFromDSO(this.pm, this.placeId, this.appId, widget.getWidgetId());
				if ( widget.isVolatileWidget() ) {
					walr.widgets.add(RestConverter.widgetRestFromDso(widget));
					DAO.deleteWidget(this.placeId, this.appId, widget.getWidgetId());
				}
			} else {
				walr.widgets.add(RestConverter.widgetRestFromDso(widget));
				DAO.deleteWidget(this.placeId, this.appId, widget.getWidgetId());
			}
			
		}
			
		if ( !DAO.commitOrRollbackTransaction() ) {
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Could not commit transaction");
			
		}
			
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



	@Override
	protected Object doGet() {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
