package org.instantplaces.interactionmanager.server;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;


import org.instantplaces.interactionmanager.shared.Contact;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.restlet.ext.jackson.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class HelloWorldResource extends InstantPlacesGenericResource {
	static Logger log = Logger.getLogger("InteractionManagerApplication"); 
	
	//Contact c;
	//String callback = "call";
	
	@Override
	public void doInit() {
		super.doInit();
		this.setResource(new ContactImpl("jorge cardoso", 30)); 
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		/*ContactImpl e = new ContactImpl("Alfred", 31);

        try {
            pm.makePersistent(e);
        } finally {
            pm.close();
        }*/
		//System.out.println(this.getReference());
		//resource = new Contact("jorge cardoso", 30);
		//callback = this.getQuery().getFirstValue("callback", "defaultCallback");
	}

	
	@Override
	protected Object doPost(Object in) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ContactImpl incoming = (ContactImpl)in;
		
		Key k = KeyFactory.createKey(ContactImpl.class.getSimpleName(), incoming.getEmail());
		ContactImpl fromDS = null;
		try {
			fromDS = pm.getObjectById(ContactImpl.class, k);
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
		
		ContactImpl incoming = (ContactImpl)in;
		
		Key k = KeyFactory.createKey(ContactImpl.class.getSimpleName(), incoming.getEmail());
		ContactImpl fromDS = null;
		try {
			fromDS = pm.getObjectById(ContactImpl.class, k);
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

}
