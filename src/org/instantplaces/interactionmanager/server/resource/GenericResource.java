package org.instantplaces.interactionmanager.server.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.instantplaces.interactionmanager.server.Log;
import org.instantplaces.interactionmanager.server.PMF;
import org.instantplaces.interactionmanager.server.dso.ApplicationDSO;
import org.instantplaces.interactionmanager.server.dso.PlaceDSO;
import org.instantplaces.interactionmanager.server.dso.WidgetDSO;
import org.instantplaces.interactionmanager.server.rest.ErrorREST;
import org.restlet.Response;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * This class provides generic functions for resources. It provides subclasses with
 * automatic generation of XML, JSON and JSONP formats for GET requests.
 * Objects are serialized automatically to XML based on JAXP annotations. 
 * 
 * @author Jorge C. S. Cardoso
 *
 * @param <?> The type of object that is going to be serialized and sent back to 
 * the client.
 */
public abstract class GenericResource extends ServerResource {
	protected Logger log = Logger.getLogger("InteractionManagerApplication"); 
	
	
	/**
	 * The accepted types.
	 */
	protected static enum ContentType {JSONP, JSON, XML, HTML};

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
	
	/**
	 * The name of the data format to be sent back.
	 * We're bypassing Restlet's automatic content-negotiation and conversion.
	 */
	protected String contentType;
	
	protected PersistenceManager pm; 
	
	public GenericResource() {
		
	}
	
	/**
	 * Inits the resource and tries to read the callback and failurecallback function
	 * names. 
	 * 
	 * Subclasses should override this method and call super.doInit() 
	 * to make sure that the callbacks are properly parsed.
	 */
	@Override
	public void doInit() {
		/*
		 * JDO Queries only retrieve child objects when they are accessed so
		 * we keep an instance of PersistanceManager throughout the whole request and
		 * release it at the end (see doRelease())
		 */
		pm = PMF.get().getPersistenceManager();
		/*
		 * Read the user specified content-type. 
		 */
		contentType = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("output", "");
		
		/*
		 * Read the user specified callback function name (used for JSONP only)
		 */
		callback = this.getQuery().getFirstValue("callback", "defaultCallback");
		failureCallback = this.getQuery().getFirstValue("failurecallback",
				"defaultFailureCallback");
				
		log.info("Client specified content-type: " + contentType);
		if (contentType.equalsIgnoreCase(ContentType.JSONP.name())) {
			log.info("Using callback: " + callback + " and failureCallback: " +failureCallback);
		}
		
		/*
		 * Check if the user specified content-type (if any) matches any of the 
		 * accepted ones.
		 */
		for (ContentType t: ContentType.values()) {
			if (t.name().equalsIgnoreCase(contentType)) {
				return;
			}
		}
		
		/*
		 * If not, return the error message
		 */
		String errorMessage =  "Sorry, you have to specify a valid content-type.";
		errorMessage += "Valid types are: " + Arrays.toString(ContentType.values());
		//this.setStatus(status)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorMessage);
	}
	
	@Override
	public void doRelease() {
		pm.close();
	}
	
	protected abstract Object doPost(Object incoming);
	protected abstract Object doPut(Object incoming);
	protected abstract Object doGet();
	protected abstract Class getResourceClass();

	// GET Methods
	@Get("html")
	public Representation returnAsHTML() {
		Object object = doGet();
		return this.representAsHTML(object);
	}
	
	@Get("xml")
	public Representation returnAsXML() {
		Object object = doGet();
		return this.representAsXML(object);
	}
	
	@Get("json")
	public Representation returnAsJSON() {
		Object object = doGet();
		return this.representAsJSON(object);
	}
	
	@Get("jsonp")
	public Representation returnAsJSONP() {
		Object object = doGet();
		return this.representAsJSONP(object);
	}
	
	// PUT Methods
	@Put("json")
	public Representation acceptItemToPut(Representation entity) { 
		Object object = deserializeJSON(entity);
		
		Object toClient = doPut(object);
		
		if (toClient instanceof ErrorREST) {
			this.setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		}
		return representAsJSON(toClient);
	}
	
	// POST Methods
	@Post("json")
	public Representation acceptItem(Representation entity) { 
		Object object = deserializeJSON(entity);
		
		Object toClient = doPost(object);
		
		if (toClient instanceof ErrorREST) {
			this.setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		}
		return representAsJSON(toClient);
	}

	/**
	 * Deserializes JSON objects using JacksonRepresentation. 
	 * This ignores any unknown object properties. It determines
	 * the target object class by calling getResourceClass().
	 * 
	 * @param entity The object to be deserialized.
	 * @return A Java Object with the deserialized JSON object.
	 */
	private Object deserializeJSON(Representation entity) {
		JacksonRepresentation jr = new JacksonRepresentation(entity, this.getResourceClass());
		jr.getObjectMapper().getDeserializationConfig().addHandler(new DeserializationProblemHandler() {
			public boolean handleUnknownProperty(DeserializationContext ctxt, JsonDeserializer<?> deserializer, java.lang.Object bean, java.lang.String propertyName) {
				if (propertyName.equalsIgnoreCase("__gwt_ObjectId")) {
					Log.get().debug("Ignoring __gwt_ObjectId property.");
				} else {
					Log.get().warn("Ignoring : " + propertyName);
				}
				
				try {
					ctxt.getParser().skipChildren();
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		
		Object object = jr.getObject();
		return object;
	}
	
	
	
	
	public Representation representAsHTML(Object object) {
		log.info("Representing as HTML");
		StringRepresentation sr = new StringRepresentation(object.toString());
		sr.setMediaType(MediaType.TEXT_HTML);
		return sr;
	}
	

	public Representation representAsJSONP(Object object) {
		log.info("Representing as JSONP");
		
		String s = "";
		try {
			JacksonRepresentation jr = new JacksonRepresentation(object);
			
			
			s = callback + "(" + jr.getText() + ")";
		} catch (Exception e) {
			log.severe("Oops: " + e.getMessage());			
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Sorry an internal error has occurred.");
		}
		return new StringRepresentation(s);
	}
		

	public Representation representAsXML(Object object) {
		log.info("Representing as XML" );
		JaxbRepresentation jr = null;
		jr = new JaxbRepresentation(object);
		return jr;
	}
	

	public Representation representAsJSON(Object object) {
		log.info("Representing as JSON" );
		JacksonRepresentation jr = new JacksonRepresentation(object);
		return jr;
	}
	

}