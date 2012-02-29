/**
 * 
 */
package org.instantplaces.im.server.resource.task;

import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.WidgetInputRest;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class TaskLogInputStatistics extends ServerResource {

	@Post
	public Representation doPost(Representation incoming) {
		
		String json = null;
		try {
			json = incoming.getText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.get().debug("Logging input: " + json);
		
		
		ObjectMapper mapper = new ObjectMapper();
		WidgetInputRest wir = null;
		try {
			 wir = mapper.readValue(json, WidgetInputRest.class);
		} catch (JsonParseException e) {
			Log.get().error("Json parsing exception: " + e.getMessage());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			Log.get().error("Json mapping exception: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.get().error("IO Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		
		return null;
	}

	/**
	 * Deserializes JSON objects using JacksonRepresentation. 
	 * This ignores any unknown object properties. It determines
	 * the target object class by calling getResourceClass().
	 * @param <T>
	 * 
	 * @param entity The object to be deserialized.
	 * @return 
	 * @return A Java Object with the deserialized JSON object.
	 */
	private <T> T deserializeJSON(Representation entity, Class<T> c) {
		JacksonRepresentation<T> jr = new JacksonRepresentation<T>(entity, c);
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
		
		return jr.getObject();
	}

}
