/**
 * 
 */
package org.instantplaces.im.server.rest.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.DaoTmp;
import org.instantplaces.im.server.dao.DaoConverter;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.rest.representation.json.ApplicationListRest;
import org.instantplaces.im.server.rest.representation.json.ApplicationRest;
import org.instantplaces.im.server.rest.representation.json.RestConverter;
import org.instantplaces.im.server.rest.representation.json.WidgetListRest;
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
		PlaceDao existingPlaceDSO = null;
		
		ApplicationRest applicationRest = (ApplicationRest) incoming;

		
		/*
		 * Get the Place from the store. Create one if it does not exist yet
		 */
		DaoTmp.beginTransaction();
		existingPlaceDSO = DaoTmp.getPlace(this.placeId);
		if (null == existingPlaceDSO) {
			Log.get().info(
					"The specified place " + this.placeId + " was not found. Creating new...");
			existingPlaceDSO = new PlaceDao(this.placeId);
			DaoTmp.put(existingPlaceDSO);

			/*
			 * A new place needs a new ReferenceCodeGenerator
			 */
			ReferenceCodeGeneratorDAO rcg = new ReferenceCodeGeneratorDAO(existingPlaceDSO);
			DaoTmp.put(rcg);
		} 

		ApplicationDao applicationDao = DaoConverter.getApplicationDao(existingPlaceDSO, applicationRest);
		
		
		DaoTmp.put(applicationDao);
		
		DaoTmp.commitOrRollbackTransaction();
		
		return applicationRest;

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

		/*
		 * If the request is for a specific app return it
		 */
		if ( null != this.appId ) {
			DaoTmp.beginTransaction();
			/*
			 * Return the list of applications
			 */
		    ApplicationDao applicationDao = DaoTmp.getApplication(this.placeId, this.appId);
			
			DaoTmp.commitOrRollbackTransaction();
			
			if ( null != applicationDao ) {
				ApplicationRest applicationRest= RestConverter.applicationFromDSO(applicationDao);
			
				return applicationRest; 
			} else {
				return null;
			}
			
			/*
			 * Else return the list of apps for this place
			 */
		} else {
		
			String activeParameter = this.getRequest().getOriginalRef()
					.getQueryAsForm().getFirstValue("active", "");
			
			int active = -1; // by default, we list all apps
	
			if (!activeParameter.equals("")) { // if something was specified in the active parameter 
												// query try to parse it
				if (activeParameter.equalsIgnoreCase("true")) {
					active = 1;
				} else if (activeParameter.equalsIgnoreCase("false")) {
					active = 0;
				} else {
					String errorMessage = "Sorry, 'active' query parameter must be, 'true', or 'false'.";
	
					Log.get().error(errorMessage);
	
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
							errorMessage);
				}
			}
	
			
			DaoTmp.beginTransaction();
			/*
			 * Return the list of applications
			 */
			List<ApplicationDao> applications = DaoTmp.getApplications(this.placeId);
			
			DaoTmp.commitOrRollbackTransaction();
			
			
	
			/*
			 * We put the ArrayList into a custom WidgetArrayListREST because I
			 * couldn't make JAXB work otherwise... :(
			 */
			ApplicationListRest aalRest = RestConverter.applicationArrayListFromDSO(applications);
			aalRest.setPlaceId(this.placeId);
			
			/*
			 * Filter the app list, based on the active parameter
			 */
			Iterator<ApplicationRest> it = aalRest.getApplications().iterator();
			while (it.hasNext()) {
				ApplicationRest app = it.next();
	
				/*
				 * We only want inactive apps
				 */
				if (app.isActive() && active == 0) {
					it.remove();
					/*
					 * We only want active apps
					 */
				} else if ( !app.isActive() && active == 1) {
					it.remove();
				}
			}
			
			return aalRest;
	
		}
	}

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
		return ApplicationRest.class;
	}

}
