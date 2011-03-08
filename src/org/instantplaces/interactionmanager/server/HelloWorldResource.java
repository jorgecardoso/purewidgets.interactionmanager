package org.instantplaces.interactionmanager.server;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.restlet.ext.jackson.*;

public class HelloWorldResource extends ServerResource {
	
	Contact c;
	String callback = "call";
	
	@Override
	public void doInit() {
		//System.out.println(this.getReference());
		c = new Contact("jorge cardoso", 30);
		callback = this.getQuery().getFirstValue("callback", "defaultCallback");
	}

	
	@Get("jsonp")
    public String representJsonp() {
		System.out.println(this.getReference());
		JacksonRepresentation jr = new JacksonRepresentation( c );
		String s = "";
		try {
			s = callback + "(" + jr.getText() + ")";
		} catch (Exception e) {
			e.printStackTrace();
		}
        return s;
	}
	
	@Get("json")
	 public Contact representJson() {
			//System.out.println(this.getReference());
			return c;
			
		}

}
