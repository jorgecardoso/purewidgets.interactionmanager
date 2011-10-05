/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.ArrayList;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.rest.ApplicationArrayListREST;


/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class ApplicationResource extends GenericResource {

	/* (non-Javadoc)
	 * @see org.instantplaces.im.server.resource.GenericResource#doPost(java.lang.Object)
	 */
	@Override
	protected Object doPost(Object incoming) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.instantplaces.im.server.resource.GenericResource#doPut(java.lang.Object)
	 */
	@Override
	protected Object doPut(Object incoming) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.instantplaces.im.server.resource.GenericResource#doGet()
	 */
	@Override
	protected Object doGet() {
		/*
		 * Return the list of applications
		 */
		ArrayList<ApplicationDSO> applications = ApplicationDSO.getApplicationsDSO(this.pm, this.placeId);
		
		if ( applications != null ) {
			
			/*
			 * We put the ArrayList into a custom WidgetArrayListREST
			 * because I couldn't make JAXB work otherwise... :(
			 */
			ApplicationArrayListREST aalREST = ApplicationArrayListREST.fromDSO(applications); 
					
			return aalREST;
		} else {
			Log.get().debug("Could not find any application for the specified place");
			return new ApplicationArrayListREST();
		}
		
		
	}

	/* (non-Javadoc)
	 * @see org.instantplaces.im.server.resource.GenericResource#doDelete()
	 */
	@Override
	protected Object doDelete() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.instantplaces.im.server.resource.GenericResource#getResourceClass()
	 */
	@Override
	protected Class getResourceClass() {
		return ApplicationResource.class;
	}

}
