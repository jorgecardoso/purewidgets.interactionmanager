package org.instantplaces.interactionmanager.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.MediaType;
import org.restlet.data.Method;

/**
 * This class provides generic functions for resources. It provides subclasses with
 * automatic generation of XML, JSON and JSONP formats for GET requests.
 * Objects are serialized automatically to XML based on JAXP annotations. 
 * 
 * @author Jorge C. S. Cardoso
 *
 * @param <T> The type of object that is going to be serialized and sent back to 
 * the client.
 */
public abstract class InstantPlacesGenericResource<T> extends ServerResource {
	static Logger log = Logger.getLogger("InteractionManagerApplication"); 
	
	
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
	
	protected String contentType;
	
	public InstantPlacesGenericResource() {
		resource = null;
	}
	
	/**
	 * Inits the resource and tries to read the callback and failurecallback function
	 * names. 
	 * 
	 * Subclasses should override this method and call super.doInit() 
	 * to make sure that the callbacks are properly parsed, and setResource() to
	 * set the resource that is going to be serialized.
	 */
	@Override
	public void doInit() {
		contentType = this.getQuery().getFirstValue("output", "JSONP");
		log.info("Using content-type: " + contentType);
		callback = this.getQuery().getFirstValue("callback", "defaultCallback");
		failureCallback = this.getQuery().getFirstValue("failurecallback",
				"defaultFailureCallback");
				
		Logger.getLogger("InteractionManagerApplication").info("Using callback: " + callback + " and failureCallback: " +failureCallback);
	}
	
	@Override
	protected List<Variant> getVariants(Method method) {
		ArrayList<Variant> l = new ArrayList<Variant>();
		
		
		return l;
	}

	
	/**
	 * Sets the resource that is going to be serialized.
	 * 
	 * @param resource
	 */
	public void setResource(T resource) {
		this.resource = resource;
	}
	
	@Override
	protected Representation get() {
		log.warning("Received request for full representation");
		return get(null);
	}
	
	@Override
	protected Representation get(Variant variant) {
		
		//return this.getConverterService().toRepresentation(this.resource, variant, this);

		//variant.getMediaType().
		/*if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			return representAsJSONP();
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				return representAsJSON();
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return representAsXML();
		} else if (variant.isCompatible(VARIANT_HTML)) {
			return representAsHTML();
		}*/
		if (this.contentType.equalsIgnoreCase("JSONP")) {
			return representAsJSONP();
		} else if (this.contentType.equalsIgnoreCase("JSON")) {
			return representAsJSON();
		} else if (this.contentType.equalsIgnoreCase("XML")) {
			return representAsXML();
		} else if (this.contentType.equalsIgnoreCase("HTML")) {
			return representAsHTML();
		} 
		return null;
		
	}

	public Representation representAsHTML() {
		return new StringRepresentation(this.resource.toString()); 
	}
	

	public Representation representAsJSONP() {
		// System.out.println(this.getReference());
		Logger.getLogger("InteractionManagerApplication").info("Representing as JSONP");
		
		JacksonRepresentation<T> jr = new JacksonRepresentation<T>(this.resource);
		
		String s = "";
		try {
			s = callback + "(" + jr.getText() + ")";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new StringRepresentation(s);
	}
		
	public Representation representAsXML() {
		Logger.getLogger("InteractionManagerApplication").info("Representing as XML" );
		JaxbRepresentation<T> jr = new JaxbRepresentation<T>(this.resource);
		
		return jr;

	}
	
	public Representation representAsJSON() {
		Logger.getLogger("InteractionManagerApplication").info("Representing as JSON" );
		JacksonRepresentation<T> jr = new JacksonRepresentation<T>(this.resource);
		
		return jr;

	}
	


}
