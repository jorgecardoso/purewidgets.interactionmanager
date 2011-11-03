/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;
import org.instantplaces.im.server.rest.ApplicationArrayListREST;
import org.instantplaces.im.server.rest.ApplicationREST;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class ApplicationResource extends GenericResource {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.instantplaces.im.server.resource.GenericResource#doPost(java.lang
	 * .Object)
	 */
	@Override
	protected Object doPost(Object incoming) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.instantplaces.im.server.resource.GenericResource#doPut(java.lang.
	 * Object)
	 */
	@Override
	protected Object doPut(Object incoming) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.instantplaces.im.server.resource.GenericResource#doGet()
	 */
	@Override
	protected Object doGet() {
		return null;
	}
//		String activeParameter = this.getRequest().getOriginalRef()
//				.getQueryAsForm().getFirstValue("active", "");
//		int active = -1; // by default, we list all apps
//
//		if (!activeParameter.equals("")) { // if something was specified in the
//											// query try to parse it
//			if (activeParameter.equalsIgnoreCase("true")) {
//				active = 1;
//			} else if (activeParameter.equalsIgnoreCase("false")) {
//				active = 0;
//			} else {
//				String errorMessage = "Sorry, 'active' query parameter must be, 'true', or 'false'.";
//
//				Log.get().error(errorMessage);
//
//				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
//						errorMessage);
//			}
//		}
//
//		
//		this.beginTransaction();
//		/*
//		 * Return the list of applications
//		 */
//		
//		ArrayList<WidgetDSO> widgetsDetached = (ArrayList<WidgetDSO>) this.pm.detachCopyAll(WidgetDSO.getWidgetsFromDSO(this.pm, this.placeId));
//		ArrayList<WidgetOptionDSO> widgetOptionsDetached = (ArrayList<WidgetOptionDSO>) this.pm.detachCopyAll(WidgetDSO.getWidgetOptionsFromDSO(this.pm, this.placeId));
//		ArrayList<ApplicationDSO> applicationsDetached = (ArrayList<ApplicationDSO>) this.pm.detachCopyAll(ApplicationDSO.getApplicationsDSO(this.pm, this.placeId));
//		this.commitTransaction();
//		
//		if ( null == applicationsDetached || null == widgetsDetached || null == widgetOptionsDetached) {
//			return  new ApplicationArrayListREST();
//		}
//		
//		for ( WidgetOptionDSO widgetOption : widgetOptionsDetached ) {
//			
//			for ( WidgetDSO widget : widgetsDetached ) {
//				
//				if ( widgetOption.getWidgetId().equals(widget.getWidgetId()) ) {
//					widget.addWidgetOption(widgetOption);
//				}
//			}
//			
//		}
//		
//		for ( ApplicationDSO app : applicationsDetached ) {
//			
//			for ( WidgetDSO widget : widgetsDetached ) {
//				if ( widget.getApplicationId().equals(app.getApplicationId()) ) {
//					app.addWidget(widget);
//				}
//			}
//			
//			
//		}
//		
//		
//		/*
//		 * We put the ArrayList into a custom WidgetArrayListREST because I
//		 * couldn't make JAXB work otherwise... :(
//		 */
//		ApplicationArrayListREST aalREST = ApplicationArrayListREST
//				.fromDSO(applicationsDetached);
//		
//		/*
//		 * Filter the app list, based on the active parameter
//		 */
//		Iterator<ApplicationREST> it = aalREST.applications.iterator();
//		while (it.hasNext()) {
//			ApplicationREST app = it.next();
//
//			/*
//			 * We only want inactive apps
//			 */
//			if (app.isActive() && active == 0) {
//				it.remove();
//				/*
//				 * We only want active apps
//				 */
//			} else if ( !app.isActive() && active == 1) {
//				it.remove();
//			}
//		}
//		
//		return aalREST;
//	
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.instantplaces.im.server.resource.GenericResource#doDelete()
	 */
	@Override
	protected Object doDelete() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.instantplaces.im.server.resource.GenericResource#getResourceClass()
	 */
	@Override
	protected Class getResourceClass() {
		return ApplicationResource.class;
	}

}
