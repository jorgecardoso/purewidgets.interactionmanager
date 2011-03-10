package org.instantplaces.interactionmanager.server.resources;

import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;


import org.instantplaces.interactionmanager.server.PMF;
import org.instantplaces.interactionmanager.server.dataobjects.ContactDO;
import org.instantplaces.interactionmanager.shared.Contact;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.restlet.ext.jackson.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class HelloWorldResource extends InstantPlacesGenericResource {
	
	
	//Contact c;
	//String callback = "call";
	
	
	
	@Override
	protected Object doPost(Object in) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ContactDO incoming = (ContactDO)in;
		
		Key k = KeyFactory.createKey(ContactDO.class.getSimpleName(), incoming.getEmail());
		ContactDO fromDS = null;
		try {
			fromDS = pm.getObjectById(ContactDO.class, k);
		} catch (Exception e) {
			log.warning("Could not retrieve object");
		}
		if ( fromDS != null ) {
			log.info(fromDS.toString());
			fromDS.copyFrom(incoming);
			pm.close();
			return incoming;
		} else {
			pm.close();
			return null;
		}
		
        
        
        
		
	}


	@Override
	protected Object doPut(Object in) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		ContactDO incoming = (ContactDO)in;
		
		Key k = KeyFactory.createKey(ContactDO.class.getSimpleName(), incoming.getEmail());
		ContactDO fromDS = null;
		try {
			fromDS = pm.getObjectById(ContactDO.class, k);
		} catch (Exception e) {
			log.warning("Could not retrieve object");
		}
		if (fromDS != null) {
			String errorMessage = "Object already exists!";
			Error error = new Error(in, errorMessage);
			log.severe(errorMessage);
			
			pm.close();
			
			return error;
			
		} else {
			incoming.setAge(1000);
			try {
				pm.makePersistent(incoming);
			} finally {
				pm.close();
			}
		
		}
		return incoming;
	}


	@Override
	protected Object doGet() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Class getResourceClass() {
		return ContactDO.class;
	}

}
