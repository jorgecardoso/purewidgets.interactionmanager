package org.instantplaces.im.server.comm;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetInputDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.jdo.PersistenceManager;

/**
 * Requests input for a place from the Instant Places main service 
 * and deserializes it.
 * 
 * @author "Jorge C. S. Cardoso"
 *
 */
public class InputRequest {
	private static final String WIDGET_COMMAND_NAME = "TAG";
	private static final String COMMAND_NAME_SEPARATOR = ".";
	private static final String PARAM_SEPARATOR = ":";
	
	private static final String url ="http://193.137.8.107/instantplacesservice/instantplacesservice.svc/domain/dsi/place/jorge/presences/commands";
	
	public  static void getPresences() {
		
		
 		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		//builder.setIgnoringElementContentWhitespace(true);
		try {
			InputStream is = getNoCache(url);
			Document doc = builder.build(is);
			
			Element root = doc.getRootElement();
			Log.get().debug(root.toString() + root.getChildText("placeId"));
			
			Element usedCommands = root.getChild("usedCommands");
			Log.get().debug(usedCommands.toString());
			
			for (Element usedCommand : (List<Element>)usedCommands.getChildren()) {
				if (usedCommand.getChildText("name").equalsIgnoreCase(WIDGET_COMMAND_NAME)) {
					Log.get().debug(usedCommand.toString() + " " + WIDGET_COMMAND_NAME);
					
					Element usedCommandsInfo =usedCommand.getChild("usedCommandsInfo");
		
					for (Element usedCommandInfo : (List<Element>)usedCommandsInfo.getChildren("usedCommandInfo")) {
						Log.get().debug(usedCommandInfo.toString());
						
						String identity = usedCommandInfo.getChildText("identityId");
						
						String parameters[] = usedCommandInfo.getChild("command").getChildText("firstArgument").split(PARAM_SEPARATOR);
						
						String refCode = "";
						
						if (parameters != null && parameters.length > 0) {
							refCode = parameters[0];
							String tmp[] = new String[parameters.length-1];
							for (int i = 0; i < tmp.length; i++) {
								tmp[i] = parameters[i+1];
							}
							parameters = tmp;
												
						}
						Log.get().debug("Identity: " + identity + " refCode:"+ refCode +"  parameter:" +Arrays.toString(parameters));
						
						saveInput(identity, refCode, parameters);
						
					}
				}
				
			}
		
		} catch (JDOMException e) {
			e.printStackTrace();
			Log.get().error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.get().error(e.getMessage());
		}
		
		
		//return identities;
	}	
	
	
	private static void saveInput(String identity, String refCode,
			String[] parameters) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		WidgetOptionDSO widgetOption = WidgetOptionDSO.getWidgetOptionDSOByReferenceCode(pm, refCode);
		
		if ( widgetOption == null ) {
			Log.get().debug("No widgets are using this reference code.");
			return;
		} else {
			Log.get().debug("Saving input for " + widgetOption.toString());
			
			WidgetInputDSO input = new WidgetInputDSO();
			input.setPersona(identity);
		
			input.setParameters(parameters);
			input.setWidgetOptionDSO(widgetOption);
			input.setTimeStamp("");
			widgetOption.addWidgetInput(input);
			try {
				pm.close();
			} catch(Exception e) {
				Log.get().error("Could not save input to datastore");
				Log.get().equals(e.getMessage());
				e.printStackTrace();
			} finally {
				
			}
		}
		
		
		
	}


	/**
	 * Makes a connection to the specified URL and returns the associated 
	 * InputStream. This method makes sure that the connection does not use 
	 * the web cache.
	 * 
	 * @param urlString
	 * @return
	 */
	private static InputStream getNoCache(String urlString) {
		try {
		java.net.URL url = new java.net.URL(urlString);
		HttpURLConnection con;
		// con = (HttpURLConnection)u.openConnection(proxy);
		con = (HttpURLConnection) url.openConnection();

		con.setUseCaches(false);
		con.setRequestProperty("Cache-control", "max-age=0");
		
			con.connect();
			InputStream is = con.getInputStream();
			return is;
			
		
		} catch (Exception e) {
			Log.get().error(e.getMessage());
			
		}
		return null;
	}
}
