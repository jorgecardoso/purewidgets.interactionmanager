package org.instantplaces.im.server.resource;

import java.util.ArrayList;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetInputDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;
import org.instantplaces.im.server.rest.ErrorREST;
import org.instantplaces.im.server.rest.WidgetInputArrayListREST;
import org.instantplaces.im.server.rest.WidgetInputREST;
import org.restlet.data.Status;

public class WidgetInputResource extends GenericResource {

	@Override
	protected Object doPost(Object incoming) {
		this.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "Only GET methods allowed for this resource.");
		
		return null;
	}

	@Override
	protected Object doPut(Object incoming) {
		this.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "Only GET methods allowed for this resource.");
		
		return null;
	}

	@Override
	protected Object doGet() {
		String fromParameter = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("from", "");
		long from = 0;
		try {
			from = Long.parseLong(fromParameter);
		} catch (Exception e) {
			Log.get().warn("Error parsing 'from' URL parameter. Assuming not specified.");
			fromParameter = "";
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
				Log.get().debug("Client specified an non-existing widget to fetch input from.");
				return new ErrorREST(null, "Specified widget does not exist.");
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
				Log.get().debug("The specified application does not have any widget");
				return new ErrorREST(null, "The specified application does not have any widget");
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
