package org.instantplaces.im.server.comm;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
						
						String parameter = usedCommandInfo.getChild("command").getChildText("firstArgument");
						String identity = usedCommandInfo.getChildText("identityId");
						
						Log.get().debug("Identity: " + identity + "  parameter:" +parameter);
						
					}
				}
				
			}
			/*
			List <Element>namedChildren = presenceItems.getChildren("presence");
			log.info(namedChildren.toString());
			
			for (Element e : namedChildren) {
				String url = e.getChild("deviceNameUrl").getText();
				log.info(url);
				presenceURLs.add(url);
			}
			is.close();
			
			for (String url : presenceURLs) {
				is = getNoCache(url);
				doc = builder.build(is);
				String deviceName = doc.getRootElement().getChild("name").getText();
				String initTime = doc.getRootElement().getChild("initTime").getText();
				
				Identity iden = new Identity(deviceName);
				iden.setFirstSeen(initTime);
				if (iden.getRawIdentity() != "") {
					identities.add(iden);
				}
				is.close();
			}
			*/
		} catch (JDOMException e) {
			e.printStackTrace();
			Log.get().error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.get().error(e.getMessage());
		}
		
		
		//return identities;
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
