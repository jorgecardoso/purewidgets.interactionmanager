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

		// TODO: Log the widget input to the google spreadsheet
		return null;
	}
}
