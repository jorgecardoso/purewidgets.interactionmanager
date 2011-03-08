package org.instantplaces.interactionmanager.server;

import java.util.logging.Logger;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public abstract class InstantPlacesGenericResource<T> extends ServerResource {
	/**
	 * This is what will be serialized (in JSON, JSONP, XML, etc) 
	 * back to the client
	 **/
	protected T resource;

	/**
	 * If the request is for JSONP, this is the name of the callback function
	 * requested. The name "defaultCallback" is used if the client didn't supply
	 * a callback name.
	 */
	protected String callback;
	
	/**
	 * If the request is for JSONP, this is the name of the failure callback function
	 * requested. The name "defaultFailureCallback" is used if the client didn't supply
	 * a callback name.
	 */	
	protected String failureCallback;
	
	public InstantPlacesGenericResource() {
		resource = null;
	}
	
	/**
	 * Inits the resource and tries to read the callback and failurecallback function
	 * names. 
	 * 
	 * Subclasses should override to instantiate the resource to be serialized but
	 * should call super.doInit() to make sure that the callbacks are properly
	 * parsed.
	 */
	@Override
	public void doInit() {
		//resource = new ();
		callback = this.getQuery().getFirstValue("callback", "defaultCallback");
		failureCallback = this.getQuery().getFirstValue("failurecallback",
				"defaultFailureCallback");
		Logger.getLogger("InteractionManagerApplication").info("Using callback: " + callback + " and failureCallback: " +failureCallback);
		
	}

	public void setResource(T resource) {
		this.resource = resource;
	}
	
	@Get("xml")
	public Object representAsXML() {
		Logger.getLogger("InteractionManagerApplication").info("Representing as XML" );
		return resource;

	}
	
	@Get("json")
	public Object representAsJSON() {
		Logger.getLogger("InteractionManagerApplication").info("Representing as JSON" );
		return resource;

	}
	
	@Get("jsonp")
	public String representAsJSONP() {
		// System.out.println(this.getReference());
		Logger.getLogger("InteractionManagerApplication").info("Representing as JSONP");
		
		JacksonRepresentation<T> jr = new JacksonRepresentation<T>(this.resource);
		String s = "";
		try {
			s = callback + "(" + jr.getText() + ")";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


}
