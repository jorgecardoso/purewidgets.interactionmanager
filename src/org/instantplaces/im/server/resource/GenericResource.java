package org.instantplaces.im.server.resource;

import java.io.IOException;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializationProblemHandler;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.rest.ErrorREST;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
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
	 * The placeId extracted from the request URL
	 */
	protected String placeId;
	
	/**
	 * The appId extracted from the request URL
	 */
	protected String appId;
	
	/**
	 * The widgetId extracted from the request URL
	 */
	protected String widgetId;
	
	
	/**
	 * The id of the application making the request.
	 */
	protected String requestingAppId;
	
	protected ApplicationDao applicationDAO;
	
	protected ApplicationDao requestingApplicationDAO;
	
	
	public GenericResource() {
		//pm = null;
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
		 * Extract the parameters from the URL (including the ones from
		 * the query part) and put them on object fields
		 */
		extractURLParameters();

		if ( this.requestingAppId.equals("") ) {
			/*
			 * We need an appid...
			 */
			String errorMessage =  "Sorry, you have to specify a valid application id using 'appid' query parameter.";
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorMessage);
		}
		
		/*
		 * Update the requesting app's last request timestamp
		 */
		if ( null != this.placeId ) {
			Dao.beginTransaction();
			this.requestingApplicationDAO = Dao.getApplication (this.placeId, this.requestingAppId);
			
			if ( null != this.requestingApplicationDAO ) {
				this.requestingApplicationDAO.setLastRequestTimestamp(System.currentTimeMillis());
				Dao.put(this.requestingApplicationDAO);
			} 
			
			if ( !Dao.commitOrRollbackTransaction() ) {
				Log.get().warn("Could not update timestamp of application: " + this.requestingAppId);
			}
		}
			
	}

	

	/**
	 * Extracts the placeId, applicationId and widgetId from the request URL 
	 * Also extracts the output, callback and failureCallback parameters from the query.
	 * The extracted parameters are put in object variables.
	 */
	private void extractURLParameters() {
		
		this.requestingAppId = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("appid", "");
		
		
		
		this.placeId = (String)this.getRequestAttributes().get("placeid");
		this.appId = (String)getRequestAttributes().get("appid");
		this.widgetId = (String)getRequestAttributes().get("widgetid");
		
		
		Log.get().debug("Client request query parameters: placeid: " + placeId + " appid: " + appId + " widgetId: " + widgetId);
	}
	
	@Override
	public void doRelease() {
		Log.get().debug("Returning status" +  this.getResponse().getStatus().toString());	
	}

	

	
	protected abstract Object doPost(Object incoming);
	protected abstract Object doPut(Object incoming);
	protected abstract Object doGet();
	protected abstract Object doDelete();
	
	@SuppressWarnings("rawtypes")
	protected abstract Class getResourceClass();
	
	// DELETE Methods
	@Override
	@Delete
	public Representation delete() {
		Object object = doDelete();
		
		return this.representAsJSON(object);
	}

	
	@Get
	public Representation returnAsJSON() {
		Object object = doGet();
		return this.representAsJSON(object);
	}
	
	
	// PUT Methods
	@Put
	public Representation acceptItemToPut(Representation entity) { 
		Object object = deserializeJSON(entity);
		
		Object toClient = doPut(object);
		
		if (toClient instanceof ErrorREST) {
			this.setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
		}
		return representAsJSON(toClient);
	}
	
	// POST Methods
	@Post
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
			@Override
			public boolean handleUnknownProperty(DeserializationContext ctxt, JsonDeserializer<?> deserializer, java.lang.Object bean, java.lang.String propertyName) {
				if (propertyName.equalsIgnoreCase("__gwt_ObjectId")) {
					//Log.get().debug("Ignoring __gwt_ObjectId property.");
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
	

	public Representation representAsJSON(Object object) {
		Log.get().info("Returning JSON representation of resource");
		
		JacksonRepresentation jr = new JacksonRepresentation(object);
		
		return jr;
	}
	

}
