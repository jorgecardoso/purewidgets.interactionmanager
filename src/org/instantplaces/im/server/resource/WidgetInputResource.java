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
		long from = 0;
		try {
			from = Long.parseLong(this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("from", "0"));
		} catch (Exception e) {
			Log.get().warn("Error parsing 'from' URL parameter. Assuming 0.");
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
				Log.get().debug("Client specified an non-existing widget to update.");
				return new ErrorREST(null, "Specified widget does not exist.");
			}

			/*
			 * Get all inputs for all widget options
			 */
			ArrayList<WidgetInputREST> list = new ArrayList<WidgetInputREST>();
			for (WidgetOptionDSO option: storedWidgetDSO.getWidgetOptions()) {
				for ( WidgetInputDSO input : option.getWidgetInputs() ) {
					/*
					 * return only input more recent than the time the client specified
					 */
					if (input.getTimeStamp() > from) {
						list.add(input.toREST());
					}
				}
			}
			
			WidgetInputArrayListREST toClient = new WidgetInputArrayListREST();
			toClient.inputs = list;
			return toClient;
			
		} else {
			/*
			 * REturn input for all widgets for the specified application
			 */
			
			
		}
		return null;
	}

	@Override
	protected Class getResourceClass() {
		return WidgetInputREST.class;
	}

}
