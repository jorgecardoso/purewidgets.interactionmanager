package org.instantplaces.interactionmanager.server;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.restlet.ext.jackson.*;

public class HelloWorldResource extends InstantPlacesGenericResource {
	
	//Contact c;
	//String callback = "call";
	
	@Override
	public void doInit() {
		super.doInit();
		this.setResource(new Contact("jorge cardoso", 30)); 
		//System.out.println(this.getReference());
		//resource = new Contact("jorge cardoso", 30);
		//callback = this.getQuery().getFirstValue("callback", "defaultCallback");
	}

	

}
