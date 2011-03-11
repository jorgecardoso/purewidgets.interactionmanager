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
public abstract class InstantPlacesGenericResource extends ServerResource {
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
	
	public InstantPlacesGenericResource() {
		
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
	
	protected abstract Object doPost(Object incoming);
	protected abstract Object doPut(Object incoming);
	protected abstract Object doGet();
	protected abstract Class getResourceClass();


	
	@Get("html")
	public Representation returnAsHTML() {
		Object object = doGet();
		pm.close();
		return this.representAsHTML(object);
		
	}
	
	@Get("xml")
	public Representation returnAsXML() {
		Object object = doGet();
		pm.close();
		return this.representAsXML(object);
	}
	
	@Get("json")
	public Representation returnAsJSON() {
		Object object = doGet();
		pm.close();
		return this.representAsJSON(object);
	}
	
	@Get("jsonp")
	public Representation returnAsJSONP() {
		Object object = doGet();
		pm.close();
		return this.representAsJSONP(object);
	}
	
	@Put("json")
	public Representation acceptItemToPut(Representation entity) { 

		JacksonRepresentation jr = new JacksonRepresentation(entity, this.getResourceClass());
		jr.getObjectMapper().getDeserializationConfig().addHandler(new DeserializationProblemHandler() {
			public boolean handleUnknownProperty(DeserializationContext ctxt, JsonDeserializer<?> deserializer, java.lang.Object bean, java.lang.String propertyName) {
				log.warning("Ignoring : " + propertyName);
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
		Object toClient = doPut(object);
		if (toClient instanceof ErrorREST) {
			this.setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		}
		pm.close();
		return representAsJSON(toClient);
	}
	
	@Post("json")
	public Representation acceptItem(Representation entity) { 
		
		JacksonRepresentation jr = new JacksonRepresentation(entity, this.getResourceClass());
		jr.getObjectMapper().getDeserializationConfig().addHandler(new DeserializationProblemHandler() {
			public boolean handleUnknownProperty(DeserializationContext ctxt, JsonDeserializer<?> deserializer, java.lang.Object bean, java.lang.String propertyName) {
				log.warning("Ignoring : " + propertyName);
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
		//finally {
		pm.close();
		//}
		return representAsJSON(doPost(object));
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
	
	
	public PlaceDSO getPlaceDO( String placeId ) {
		
		
		Query query = pm.newQuery(PlaceDSO.class);
	    query.setFilter("id == idParam");
	    query.declareParameters("String idParam");

	    
	    try {
	        List<Object> results = (List<Object>) query.execute(placeId);
	        if (!results.isEmpty()) {
	        	PlaceDSO place = (PlaceDSO)results.get(0);
	        	//ApplicationDO [] apps = place.getApplications();
	        	//for ()
	        	return place;
	        } 
	    } finally {
	        query.closeAll();
	        //pm.close();
	    }
	    //log.info("Place not found");
	    return null;
	}
	
	public ApplicationDSO[] getApplicationsDO( String placeId ) {
		PlaceDSO place = getPlaceDO(placeId);
		if ( place == null ) {
			return null;
		}
		//log.info("Place: " + place.toString());
		return place.getApplications();
	}	
	
	public ApplicationDSO getApplicationDO( String placeId, String applicationId ) {
		ApplicationDSO[] applications = getApplicationsDO(placeId);
		if ( applications == null ) {
			return null;
		}
		for ( ApplicationDSO app : applications ) {
			log.info("APP: "  + app.toString());
			if (app.getId().equals(applicationId)) {
				return app;
			}
		}
		return null;
	}	
	
	public WidgetDSO[] getWidgetsDO(String placeId, String applicationId) {
		ApplicationDSO application = getApplicationDO(placeId, applicationId);
		if (application == null) {
			return null;
		}
		return application.getWidgets();
	}
	
	public WidgetDSO getWidgetDO(String placeId, String applicationId, String widgetId) {
		WidgetDSO widgets[] = getWidgetsDO(placeId, applicationId);
		
		if ( widgets == null ) {
			return null;
		}
		for ( WidgetDSO widget : widgets ) {
			if (widget.getId().equals(widgetId)) {
				return widget;
			}
		}
		return null;
	}
/*
	public Object getDO(Class c, String id ) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(c);
	    query.setFilter("id == idParam");
	    query.declareParameters("String idParam");

	    try {
	        List<Object> results = (List<Object>) query.execute(id);
	        if (!results.isEmpty()) {
	            return results.get(0);
	        } else {
	            return null;
	        }
	    } finally {
	        query.closeAll();
	    }
	}*/
}
