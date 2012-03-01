/**
 * 
 */
package org.instantplaces.im.server.resource.task;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

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

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

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
		SpreadsheetService service = new SpreadsheetService("Interaction Manager");
		try {
			service.setUserCredentials("jorgediablu@gmail.com", "ocpjvlucjbkbyddy");
		} catch (AuthenticationException e) {
			Log.get().error("Could not authenticate: " + e.getMessage());
			e.printStackTrace();
		} 
		FeedURLFactory factory = FeedURLFactory.getDefault();
		
		try {
			URL url = factory.getListFeedUrl("0AkwLiKlm4gz1dEFmalRCTjVvYWY5dnFzV1VuakdBYmc", "1", "private", "full");
			
			ListEntry entry = new ListEntry();
			
			entry.getCustomElements().setValueLocal("userid", wir.getPersona());
			entry.getCustomElements().setValueLocal("place", wir.getPlaceId());
			entry.getCustomElements().setValueLocal("application", wir.getApplicationId());
			entry.getCustomElements().setValueLocal("widget", wir.getWidgetId());
			entry.getCustomElements().setValueLocal("option", wir.getWidgetOptionId());
			entry.getCustomElements().setValueLocal("inputmechanism", wir.getInputMechanism());
			entry.getCustomElements().setValueLocal("timestamp", wir.getTimeStamp());
			entry.getCustomElements().setValueLocal("data", Arrays.toString(wir.getParameters()));
			

			service.insert(url, entry);
		} catch (IOException e) {
			Log.get().error("IO Exception: " + e.getMessage() );
			e.printStackTrace();
		} catch (ServiceException e) {
			Log.get().error("Service Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		return null;
	}
}
