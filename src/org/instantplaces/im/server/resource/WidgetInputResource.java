package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Arrays;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.comm.InputRequest;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.DsoFetcher;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetInputDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;
import org.instantplaces.im.server.resource.GenericResource.ContentType;
import org.instantplaces.im.server.rest.ErrorREST;
import org.instantplaces.im.server.rest.RestConverter;
import org.instantplaces.im.server.rest.WidgetInputArrayListREST;
import org.instantplaces.im.server.rest.WidgetInputREST;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class WidgetInputResource extends GenericResource {

	@Override
	protected Object doDelete() {
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

	
	private WidgetInputArrayListREST getInputForAllWidgets(long from) {
		/*
		 * Return input newer than from
		 */
		if ( from >= 0 ) {
			
			/*
			 * Fetch the widgets from the data store.
			 */
			DAO.beginTransaction();
			ArrayList<WidgetInputDAO> widgetInputs = DAO.getWidgetInput(this.placeId, this.appId, from); 
					//DsoFetcher.getWidgetInputFromDSO(this.pm, this.placeId, this.appId, from);
			DAO.commitOrRollbackTransaction();
			
			return RestConverter.widgetInputArrayListFromDso(widgetInputs);
		/*
		 * Return the last input to every widget
		 */
		} else {
			DAO.beginTransaction();
			ArrayList<WidgetDAO> widgets = DAO.getWidgets(this.placeId, this.appId);
					//DsoFetcher.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			ArrayList<WidgetInputDAO> inputs = new ArrayList<WidgetInputDAO>();
			
			for ( WidgetDAO w : widgets ) {
				WidgetInputDAO input = DAO.getLastWidgetInput(this.placeId, this.appId, w.getWidgetId()); 
						//DsoFetcher.getLastWidgetInputFromDSO(this.pm, this.placeId, this.appId, w.getWidgetId());
				if ( null != input ) {
					inputs.add(input);
				}
			}
			DAO.commitOrRollbackTransaction();
			return RestConverter.widgetInputArrayListFromDso(inputs);
		}
	}
	
	@Override
	protected Object doGet() {
		WidgetInputArrayListREST toReturn = new WidgetInputArrayListREST();
		
		
		//InputRequest.getPresences();
		
		String fromParameter = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("from", "");
		long from = -1;
		
		if ( !fromParameter.equals("") ) { // if something was specified in the query try to parse it
			try {
				from = Long.parseLong(fromParameter);
			} catch (Exception e) {
				String errorMessage =  "Sorry, 'from' query parameter must be an integer value.";
		
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorMessage);
			}
		} 
		
		
		return 	getInputForAllWidgets(from);

	}

	@Override
	protected Class getResourceClass() {
		return WidgetInputREST.class;
	}


}
