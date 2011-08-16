package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Arrays;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetInputDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;
import org.instantplaces.im.server.resource.GenericResource.ContentType;
import org.instantplaces.im.server.rest.ErrorREST;
import org.instantplaces.im.server.rest.WidgetInputArrayListREST;
import org.instantplaces.im.server.rest.WidgetInputREST;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class WidgetInputResource extends GenericResource {

	@Override
	protected Object doDelete(Object incoming) {
		String errorMessage =  "Delete not allowed. Sorry, only GET  methods allowed for this resource.";
		
		Log.get().error(errorMessage);

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}
	
	
	@Override
	protected Object doPost(Object incoming) {
		String errorMessage =  "Post not allowed. Sorry, only GET  methods allowed for this resource.";
		
		Log.get().error(errorMessage);

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}

	@Override
	protected Object doPut(Object incoming) {
		String errorMessage =  "Put not allowed. Sorry, only GET  methods allowed for this resource.";
		
		Log.get().error(errorMessage);

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}

	@Override
	protected Object doGet() {
		String fromParameter = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("from", "");
		long from = 0;
		
		if ( !fromParameter.equals("") ) { // if something was specified in the query try to parse it
			try {
				from = Long.parseLong(fromParameter);
			} catch (Exception e) {
				String errorMessage =  "Sorry, 'from' query parameter must be an integer value.";
		
				Log.get().error(errorMessage);
		
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorMessage);
				//Log.get().warn("Error parsing 'from' URL parameter. Assuming not specified.");
				//fromParameter = "";
			}
		}
		
		if (this.widgetId != null) { //Return input for the specified widget only
		
			/*
			 * Fetch the widget from the data store.
			 */
			WidgetDSO storedWidgetDSO = WidgetDSO.getWidgetFromDSO(this.pm, this.placeId, this.appId, this.widgetId);
			
			/*
			 * If the widget does not exist yet throw an error back to the client 
			 */
			if (storedWidgetDSO == null) {
				String errorMessage =  "Sorry, the specified widget does not exist.";
				
				Log.get().error(errorMessage);
		
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
				//Log.get().debug("Client specified an non-existing widget to fetch input from.");
				//return new ErrorREST(null, "Specified widget does not exist.");
			}

			/*
			 * Get all inputs for all widget options
			 */
			ArrayList<WidgetInputREST> list = new ArrayList<WidgetInputREST>();
			for ( WidgetOptionDSO option: storedWidgetDSO.getWidgetOptions() ) {
				
				if ( !fromParameter.equals("") ) {
					/*
					 * If fromParameter was specified 
					 * return only input more recent than the time the client specified
					 */
					for ( WidgetInputDSO input : option.getWidgetInputs() ) {
						if (input.getTimeStamp() > from) {
							list.add(input.toREST());
						}
					}
					
				} else {
					/*
					 * Return only the last input for each option
					 */
					ArrayList<WidgetInputDSO> inputs = option.getWidgetInputsAsArrayList();
					if ( null != inputs && inputs.size() > 0 ) {
						list.add( inputs.get(inputs.size()-1).toREST() );
					}
					
				}
			}
			
			WidgetInputArrayListREST toClient = new WidgetInputArrayListREST();
			toClient.inputs = list;
			return toClient;
			
		} else {
			/*
			 * Return input for all widgets for the specified application
			 */
			
			/*
			 * Fetch the widgets from the data store.
			 */
			ArrayList<WidgetDSO> storedWidgetsDSO = WidgetDSO.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			
			if ( null == storedWidgetsDSO || storedWidgetsDSO.size() == 0 ) {
				String errorMessage =  "Sorry, The specified application does not have any widget";
				
				Log.get().error(errorMessage);
		
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
				
				//Log.get().debug("The specified application does not have any widget");
				//return new ErrorREST(null, "The specified application does not have any widget");
			}
			
			ArrayList<WidgetInputREST> list = new ArrayList<WidgetInputREST>();
			
			for ( WidgetDSO storedWidgetDSO : storedWidgetsDSO ) {
				/*
				 * Get all inputs for all widget options
				 */
				
				for ( WidgetOptionDSO option: storedWidgetDSO.getWidgetOptions() ) {
				
					if ( !fromParameter.equals("") ) {
						/*
						 * If fromParameter was specified 
						 * return only input more recent than the time the client specified
						 */
						for ( WidgetInputDSO input : option.getWidgetInputs() ) {
							if (input.getTimeStamp() > from) {
								list.add(input.toREST());
							}
						}
						
					} else {
						/*
						 * Return only the last input for each option
						 */
						ArrayList<WidgetInputDSO> inputs = option.getWidgetInputsAsArrayList();
						if ( null != inputs && inputs.size() > 0 ) {
							list.add( inputs.get(inputs.size()-1).toREST() );
						}
						
					}
				}
			}
			
			WidgetInputArrayListREST toClient = new WidgetInputArrayListREST();
			toClient.inputs = list;
			return toClient;
			
		}
		//return null;
	}

	@Override
	protected Class getResourceClass() {
		return WidgetInputREST.class;
	}

	
}
