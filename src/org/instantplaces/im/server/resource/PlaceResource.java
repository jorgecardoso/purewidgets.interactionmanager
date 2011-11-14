/**
 * 
 */
package org.instantplaces.im.server.resource;

import java.util.Iterator;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.rest.ApplicationListRest;
import org.instantplaces.im.server.rest.ApplicationRest;
import org.instantplaces.im.server.rest.PlaceListRest;
import org.instantplaces.im.server.rest.RestConverter;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class PlaceResource extends GenericResource {

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
		List<PlaceDao> places = Dao.getPlaces();
		
		
		PlaceListRest placeListRest = RestConverter.getPlaceListRest(places);
				
		
		return placeListRest;
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
		return PlaceResource.class;
	}

}
