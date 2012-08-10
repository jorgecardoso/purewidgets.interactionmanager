package org.instantplaces.im.server.rest.resource.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDaot;
import org.instantplaces.im.server.dao.Dao;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


public class TaskNotifyServerApp extends ServerResource {

	@Get
	public Object doGet() {
		
		String placeId = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("placeid", "");
				
		String applicationId = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("appid", "");
		
		
		String originalUrl = "/task/notify-server-app?placeid="+placeId+"&appid="+applicationId;
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put(originalUrl, new Boolean(false));
		
		Log.get().debug("Executing task: " + originalUrl);
		
		Dao.beginTransaction();
		ApplicationDaot applicationDao = Dao.getApplication(placeId, applicationId);
		
		String baseUrl = applicationDao.getApplicationServerNotificationUrl();
		if ( null !=  baseUrl && baseUrl.length() > 0 ) {
			
			baseUrl +="?placeid=" + placeId  + "&appid="+applicationId;
			this.doMethod("GET", null, baseUrl);
		} else {
			Log.get().debug("No notification URL.");
		}
		Dao.commitOrRollbackTransaction();
		return "";
	}
	

	
	/**
	 * Makes a connection to the specified URL and returns the associated 
	 * InputStream. This method makes sure that the connection does not use 
	 * cache.
	 * 
	 * @param method The HTTP method to use
	 * @param data The payload data
	 * @param urlString The URL to invoke.
	 * 
	 * @return The server's response
	 * @throws HttpServerException In case of a IOException or server response other than 200 Ok.
	 */
	private String doMethod(String method, String data, String urlString)  {
		Log.get().debug("Calling " + method + " " + urlString );
		Log.get().debug("Request Body: " + data);
		System.setProperty("sun.net.http.retryPos", "false");
		try {
			java.net.URL url = new java.net.URL(urlString);
			HttpURLConnection con;
		
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(1000*30);
			if ( null != method ) {
				con.setRequestMethod(method);
			}
			if ( null != data ) {
				con.setDoOutput(true);
			}
			con.addRequestProperty("Content-type", "application/json");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setUseCaches(false);
			con.setRequestProperty("Cache-control", "max-age=0");

			
			con.connect();
			
			/*
			 * Write the data out.
			 */
			if ( null != data ) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8" ));
				bw.write(data);
				bw.flush();
			}
			
			/*
			 * Read the response.
			 */
			BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8" ));
		
			StringBuilder builder = new StringBuilder();
			String line;
			do {
				line = bf.readLine();
				if (null != line) {
					builder.append(line);
				}
			} while (null != line);
	

			
			

			Log.get().debug("Response Body:" + builder.toString() );
			return builder.toString();
			
		
		
		} catch (IOException e) {
			Log.get().error("IO Error. ", e);
			e.printStackTrace();
		}
		return "";
	}
}
