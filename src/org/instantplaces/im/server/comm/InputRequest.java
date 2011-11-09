package org.instantplaces.im.server.comm;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;
import org.instantplaces.im.server.dao.DAO;
import org.instantplaces.im.server.dao.DsoFetcher;
import org.instantplaces.im.server.dao.PlaceDAO;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetInputDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Key;


/**
 * Requests input for a place from the Instant Places main service and
 * deserializes it.
 * 
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class InputRequest {
	private static final String WIDGET_COMMAND_NAME = "TAG";
	private static final String COMMAND_NAME_SEPARATOR = ".";
	private static final String PARAM_SEPARATOR = ":";

	// TODO: Ask for input for each place
	// TODO: Ask Bruno for a way to ask for all input regardless of place or
	// domain, i.e., ask all input for an application
	private static final String url = "http://193.137.8.107/instantplacesservice/instantplacesservice.svc/domain/dsi/place/jorge/presences/commands";

	private static final String IDENTITY_URL = "http://api2.instantplaces.org/InstantPlacesService/InstantPlacesService.svc/domain/dsi/place/jorge/identities/";


	private static long minInterval = 15 * 1000; // 15 seconds

	/**
	 * Maps identities to names
	 */
	private static HashMap<String, String> identityMap = new HashMap<String, String>();

	public static void getPresences() {
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Long lastRun = (Long) syncCache.get("lastrun");
		
		Long current = new Long(System.currentTimeMillis());
		if ( null != lastRun ) {
			Log.get().warn("Could not retrieve 'lastRun' from memcache.");
			if ( (current.longValue() - lastRun.longValue()) < minInterval ) {
				return;
			}
		}
		
		
		
		ArrayList<String> lastCommands = (ArrayList<String>) syncCache.get("lastcommands");
		if ( null == lastCommands ) {
			Log.get().warn("Could not retrieve 'lastcommands' from memcache.");
			lastCommands = new ArrayList<String>();
		}
		ArrayList<String> currentCommands;
		
		
	
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		
		// builder.setIgnoringElementContentWhitespace(true);
		try {
			InputStream is = getNoCache(url);
			Document doc = builder.build(is);

			Element root = doc.getRootElement();
			Log.get().debug(root.toString() + root.getChildText("placeId"));

			Element usedCommands = root.getChild("usedCommands");
			Log.get().debug(usedCommands.toString());

			currentCommands = new ArrayList<String>();

			for (Element usedCommand : (List<Element>) usedCommands
					.getChildren()) {
				if (usedCommand.getChildText("name").equalsIgnoreCase(
						WIDGET_COMMAND_NAME)) {
					Log.get().debug(
							usedCommand.toString() + " " + WIDGET_COMMAND_NAME);

					Element usedCommandsInfo = usedCommand
							.getChild("usedCommandsInfo");

					for (Element usedCommandInfo : (List<Element>) usedCommandsInfo
							.getChildren("usedCommandInfo")) {
						Log.get().debug(usedCommandInfo.toString());

						String identity = usedCommandInfo
								.getChildText("identityId");

						String parameters[] = usedCommandInfo
								.getChild("command")
								.getChildText("firstArgument")
								.split(PARAM_SEPARATOR);

						String refCode = "";

						if (parameters != null && parameters.length > 0) {
							refCode = parameters[0];
							String tmp[] = new String[parameters.length - 1];
							for (int i = 0; i < tmp.length; i++) {
								tmp[i] = parameters[i + 1];
							}
							parameters = tmp;

						}
						Log.get().debug(
								"Identity: " + identity + " refCode:" + refCode
										+ "  parameter:"
										+ Arrays.toString(parameters));

						String cmd = identity + refCode
								+ Arrays.toString(parameters);
						currentCommands.add(cmd);

						if (!(lastCommands.contains(cmd))) {
							/*
							 * new command, so save it.
							 */
							saveInput(identity, refCode, parameters);
						} else {
							Log.get().debug(
									"Command already exists... skiping.");
						}

					}
				}

			}

			/*
			 * remove from last commands the commands that disappeared in the
			 * current list
			 */

			Iterator<String> it = lastCommands.iterator();
			while (it.hasNext()) {
				String next = it.next();
				if (!(currentCommands.contains(next))) {
					it.remove();
				}
			}

			/*
			 * add current commands to the lastcommands list
			 */
			for (String s : currentCommands) {
				lastCommands.add(s);
			}

			syncCache.put("lastcommands", lastCommands);
		} catch (JDOMException e) {
			e.printStackTrace();
			Log.get().error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.get().error(e.getMessage());
		}

	}

	private static void saveInput(String identity, String refCode,
			String[] parameters) {

		String name = resolveIdentity(identity);

		WidgetOptionDAO widgetOption = null; 
		
		
		List<Key<PlaceDAO>> placeKeys = DAO.getPlaceKeys();
		
		DAO.beginTransaction();
		
		for ( Key<PlaceDAO> placeKey : placeKeys ) {
			ArrayList<WidgetOptionDAO> options = DAO.getWidgetOption(placeKey.getName());
			
			for ( WidgetOptionDAO option : options ) {
				if ( option.getReferenceCode().equals(refCode)) {
					widgetOption = option;
					break;
				}
			}
			if ( null != widgetOption ) {
				break;
			}
		}

		//= DsoFetcher.getWidgetOptionDSOByReferenceCode(pm, refCode);

		// TODO: save statistics in PlaceDSO here. Save input to existing
		// widgets and to non-existing widgets
		if (widgetOption == null) {
			Log.get().debug("No widgets are using this reference code.");
			return;
		} else {
			Log.get().debug("Saving input for " + widgetOption.getWidgetOptionId());

			WidgetInputDAO input = new WidgetInputDAO(widgetOption, System.currentTimeMillis(), parameters, name );
			DAO.putWidgetInput(input);
			
			if ( !DAO.commitOrRollbackTransaction() ) {
		
				Log.get().error("Could not save input to datastore");
			}
		}

	}

	private static String resolveIdentity(String identity) {
		Log.get().debug("Resolving identity: " + identity);
		String name = identityMap.get(identity);

		if (null == name) {
			Log.get().debug("Identity not found in cache. Asking server");
			SAXBuilder builder = new SAXBuilder();
			builder.setValidation(false);
			try {
				InputStream is = getNoCache(IDENTITY_URL + identity);
				Document doc = builder.build(is);

				Element root = doc.getRootElement();
				Log.get().debug(root.toString());
				
				Element identityName = root.getChild("identityName");
				Log.get().debug(identityName.toString());
				name = identityName.getText();

				identityMap.put(identity, name);

			} catch (JDOMException e) {
				name = identity;
				e.printStackTrace();
				Log.get().error(e.getMessage());
			} catch (IOException e) {
				name = identity;
				e.printStackTrace();
				Log.get().error(e.getMessage());
			}
		} else {
			Log.get().debug("Identity found in cache, returning: " + name);
		}
		return name;

	}

	/**
	 * Makes a connection to the specified URL and returns the associated
	 * InputStream. This method makes sure that the connection does not use the
	 * web cache.
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
