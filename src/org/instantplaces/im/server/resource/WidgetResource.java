package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.instantplaces.im.server.rest.RestConverter;
import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.DaoConverter;
import org.instantplaces.im.server.dao.PlaceDAO;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetInputDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.googlecode.objectify.Key;


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
	        DAO.put(existingPlaceDSO);
	        
	        /*
	         * A new place needs a new ReferenceCodeGenerator
	         */
	        rcg = new ReferenceCodeGeneratorDAO(existingPlaceDSO);
	        DAO.put(rcg);
	    } else {
	    	rcg = DAO.getReferenceCodeGenerator(this.placeId);
	    	
	    }
	   
	   
	    /*
	     * Get the Application from the store. Create one if it does not exist yet.
	     */
	    existingApplicationDSO = DAO.getApplication(this.placeId, this.appId);
	    if (null == existingApplicationDSO) {
	    	Log.get().info("The specified application " + this.appId + "was not found. Creating new...");
	        existingApplicationDSO = new ApplicationDAO(existingPlaceDSO, this.appId);
	        DAO.put(existingApplicationDSO);
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
				
				/*
				 * Recycle the reference codes and delete all input associated with each option that not longer exists in the widget
				 */
				for (WidgetOptionDAO option : optionsToDelete) {
					rcg.recycleCode(option.getReferenceCode());
					DAO.delete( DAO.getWidgetInputsKeys(this.placeId, this.appId, widget.getWidgetId(), option.getWidgetOptionId()) );
				}
				
				/*
				 * Bulk delete the options
				 */
				DAO.delete(optionsToDelete);
				
				
				DAO.put( widget.getWidgetOptions() );
				
				
			} else {
				widget = DaoConverter.widgetDSOfromRest(existingApplicationDSO, receivedWidget);
				widget.assignReferenceCodes(rcg);
				DAO.put( widget );
				DAO.put( widget.getWidgetOptions() );
			}
			widgetsAdded.add(widget);
		}
		
		
		/*
		 * We are going to return this
		 */
		WidgetArrayListREST walr = RestConverter.widgetArrayListFromDso(widgetsAdded);
		
		
		/*
		 * Save the reference code
		 */
		DAO.put(rcg);
		
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
	
	
	
	private WidgetArrayListREST deleteSpecifiedWidgets(ArrayList<Key<WidgetDAO>> widgetsToDeleteKeys, boolean volatileOnly) {
		
		ReferenceCodeGeneratorDAO rcg = DAO.getReferenceCodeGenerator(this.placeId);
		
		
		Collection<WidgetDAO> widgetsToDelete = DAO.get(widgetsToDeleteKeys).values();
		
		ArrayList<WidgetOptionDAO> widgetOptionsToDelete = new ArrayList<WidgetOptionDAO>();
		
		ArrayList<WidgetInputDAO> widgetInputToDelete = new ArrayList<WidgetInputDAO>();
		
		Iterator<WidgetDAO> iterator = widgetsToDelete.iterator();
		
		while ( iterator.hasNext() ) {
			WidgetDAO widget = iterator.next();
			
			if ( volatileOnly && !widget.isVolatileWidget() ) {
				iterator.remove();
				continue;
			}
			
			/*
			 * Recycle the reference codes
			 */
			List<WidgetOptionDAO> widgetOptions = DAO.getWidgetOptions(this.placeId, this.appId, widget.getWidgetId());
			
			for ( WidgetOptionDAO option : widgetOptions ) {
				rcg.recycleCode(option.getReferenceCode());
				
				/*
				 * Add the option to the list of options that are going to be deleted in the end.
				 */
				widgetOptionsToDelete.add(option);
			}
			
			
			
			
			/*
			 * Get the list of input that belong to this widget, so that it can be deleted later
			 */
			widgetInputToDelete.addAll( DAO.getWidgetInputs(this.placeId, this.appId, widget.getWidgetId()) );
			
			
			
			
			
		}
		DAO.delete(widgetInputToDelete);
		DAO.delete(widgetOptionsToDelete);
		DAO.delete(widgetsToDelete);
		
		
		/*
		 * To be returned
		 */
		WidgetArrayListREST walr = new WidgetArrayListREST();
		walr.widgets = new ArrayList<WidgetREST>();
		
		
		for ( Key<WidgetDAO>  widgetKey : widgetsToDeleteKeys ) {
		/*
		 * Add the rest version to the list to return to the client
		 */
		WidgetREST toReturn = new WidgetREST();
		toReturn.setPlaceId(this.placeId);
		toReturn.setApplicationId(this.appId);
		toReturn.setWidgetId(widgetKey.getName());
		
				
			walr.widgets.add(toReturn);
		}
		
		
		return walr;
	}
	
	
	
	
	
	@Override
	protected Object doDelete() {
		
		Log.get().debug("Responding to DELETE request.");

		WidgetArrayListREST toReturn = new WidgetArrayListREST();
		ArrayList<Key<WidgetDAO>> list = new ArrayList<Key<WidgetDAO>>();
		
		DAO.beginTransaction();
		
		if (this.widgetId != null) { // Delete the specified widget
			
			Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, this.placeId);
			Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,	ApplicationDAO.class, this.appId);
			Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,	WidgetDAO.class, this.widgetId);
			
			list.add(widgetKey);
			toReturn = this.deleteSpecifiedWidgets(list, false);
			
		} else { // delete the widgets passed in the url param
		
			String widgetsToDeleteParam =  this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("widgets", "");
			
			
			if ( widgetsToDeleteParam.length() > 0 ) {
				
				Log.get().debug("Deleting widgets: " + widgetsToDeleteParam );
				for ( String widgetId : widgetsToDeleteParam.split(",") ) {
					Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, this.placeId);
					Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,	ApplicationDAO.class, this.appId);
					Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,	WidgetDAO.class, widgetId);
					
					list.add(widgetKey);
				}
				
				
				toReturn = this.deleteSpecifiedWidgets(list, false);
				
			} else { // delete all widgets from the app
				boolean volatileOnly = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("volatileonly", "true").equalsIgnoreCase("true");
				
				list.addAll( DAO.getWidgetsKeys(this.placeId, this.appId) );
				
				toReturn = this.deleteSpecifiedWidgets(list, volatileOnly);
				
			}
		}
		
		
		
		if ( !DAO.commitOrRollbackTransaction() ) {
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Could not commit transaction");
			
		}
		return toReturn;
	
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
