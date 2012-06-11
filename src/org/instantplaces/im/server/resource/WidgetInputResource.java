package org.instantplaces.im.server.resource;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ChannelMapDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.DaoConverter;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.WidgetDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;
import org.instantplaces.im.server.rest.RestConverter;
import org.instantplaces.im.server.rest.WidgetInputListRest;
import org.instantplaces.im.server.rest.WidgetInputRest;
import org.instantplaces.im.server.rest.WidgetListRest;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.googlecode.objectify.Key;

public class WidgetInputResource extends GenericResource {

	@Override
	protected Object doDelete() {
		String errorMessage =  "Delete not allowed. Sorry, only GET  methods allowed for this resource.";
		
		Log.get().error(errorMessage);

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}
	
	
	@Override
	protected Object doPost(Object incoming) {
		WidgetInputRest receivedWidgetInputRest = (WidgetInputRest) incoming;
		
		Dao.beginTransaction();
		WidgetOptionDao widgetOptionDao = Dao.getWidgetOption(this.placeId, this.appId, this.widgetId, receivedWidgetInputRest.getWidgetOptionId());
		
		
		if ( null != widgetOptionDao ) {
			WidgetInputDao widgetInputDao = DaoConverter.getWidgetInputDao(widgetOptionDao, receivedWidgetInputRest);
	
			widgetInputDao.setTimeStamp(System.currentTimeMillis());
			Dao.put(widgetInputDao);
			
			Dao.commitOrRollbackTransaction();
			
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.put("place/"+this.placeId+"/application/"+this.appId+"/lastinputtimestamp", widgetInputDao.getTimeStamp());
			
			this.sendInputThroughChannel(widgetInputDao);
			/*
			 * Log the input to get statistics
			 */
			WidgetInputResource.logInputStatistics(widgetInputDao);
		} else {
			Log.get().warn("WidgetOption does not exist.");
			Dao.commitOrRollbackTransaction();
		}
		
		return null;
	}
	
	/**
	 * Logs the interaction by creating a task that will log the data to a spreadsheet.
	 */
	public static void logInputStatistics(WidgetInputDao widgetInputDao) {
		WidgetInputRest widgetInputRest = RestConverter.getWidgetInput(widgetInputDao);
		Representation representation = representAsJSON(widgetInputRest);
		
		try {
			Queue queue = QueueFactory.getQueue("statistics");
			queue.add(withUrl("/task/log-input-statistics").countdownMillis(500).method(Method.POST).payload( representation.getText() ));
			
		} catch (TaskAlreadyExistsException taee) {
			Log.get().warn("Task already exists: " + taee.getMessage());
		} catch (Exception e) {
			Log.get().error("Could not submit task: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected Object doPut(Object incoming) {
		String errorMessage =  "Put not allowed. Sorry, only GET  methods allowed for this resource.";
		
		Log.get().error(errorMessage);

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}

	
	private WidgetInputListRest getInputForAllWidgets(long from) {
		/*
		 * Return input newer than from
		 */
		if ( from >= 0 ) {
			
			/*
			 * Fetch the widgets from the data store.
			 */
			Dao.beginTransaction();
			List<WidgetInputDao> widgetInputs = Dao.getWidgetInputs(this.placeId, this.appId, from); 
					//DsoFetcher.getWidgetInputFromDSO(this.pm, this.placeId, this.appId, from);
			Dao.commitOrRollbackTransaction();
			
			/*
			 * Put the last timestamp in Memcache, 
			 */
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			long largestTimestamp = 0;
			for ( WidgetInputDao widgetInputDao : widgetInputs ) {
				if ( widgetInputDao.getTimeStamp() > largestTimestamp ) {
					largestTimestamp = widgetInputDao.getTimeStamp();
				}
			}
			syncCache.put("place/"+this.placeId+"/application/"+this.appId+"/lastinputtimestamp", largestTimestamp);
			
			
			return RestConverter.getWidgetInputList(widgetInputs);
		/*
		 * Return the last input to every widget
		 */
		} else {
			Dao.beginTransaction();
			List<WidgetDao> widgets = Dao.getWidgets(this.placeId, this.appId);
					//DsoFetcher.getWidgetsFromDSO(this.pm, this.placeId, this.appId);
			ArrayList<WidgetInputDao> inputs = new ArrayList<WidgetInputDao>();
			
			for ( WidgetDao w : widgets ) {
				WidgetInputDao input = Dao.getLastWidgetInput(this.placeId, this.appId, w.getWidgetId()); 
						//DsoFetcher.getLastWidgetInputFromDSO(this.pm, this.placeId, this.appId, w.getWidgetId());
				if ( null != input ) {
					inputs.add(input);
				}
			}
			Dao.commitOrRollbackTransaction();
			return RestConverter.getWidgetInputList(inputs);
		}
	}
	
	private void sendInputThroughChannel(WidgetInputDao widgetInputDao) {
		sendInputThroughChannel(widgetInputDao, this.placeId, this.appId);
	}
	
	public static void sendInputThroughChannel(WidgetInputDao widgetInputDao, String placeId, String appId) {
		Dao.beginTransaction();
		
		ChannelMapDao channelMap = Dao.getChannelMap(placeId, appId);
		
		Dao.commitOrRollbackTransaction();
		
		if ( null == channelMap ) {
			return;
		}
		
		if ( null != channelMap.getClientIds() ) {
			
			WidgetInputListRest widgetInputListRest = new WidgetInputListRest();
			ArrayList<WidgetInputRest> widgetInputList = new ArrayList<WidgetInputRest>();
			widgetInputList.add(RestConverter.getWidgetInput(widgetInputDao));
			widgetInputListRest.setInputs(widgetInputList);
			
			String json = null;
			try {
				json = GenericResource.representAsJSON(widgetInputListRest).getText();
				Log.get().debug("Sending json to client: " + json);
			} catch (IOException e) {
				Log.get().error("Could not get json representation for widgetinput" + widgetInputListRest);
				e.printStackTrace();
			}
			
			for ( String clientId : channelMap.getClientIds() ) {
				Log.get().debug("Sending input to " + clientId);
				ChannelService channelService = ChannelServiceFactory.getChannelService();
			     
				 
			    channelService.sendMessage(new ChannelMessage(clientId, json));
			}
		}
		
	}
	
	@Override
	protected Object doGet() {
		WidgetInputListRest toReturn = new WidgetInputListRest();
		
		
		//InputRequest.getPresences();
		
		String fromParameter = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("from", "");
		long from = -1;
		
		if ( !fromParameter.equals("") ) { // if something was specified in the query try to parse it
			try {
				from = Long.parseLong(fromParameter);
			} catch (Exception e) {
				String errorMessage =  "Sorry, 'from' query parameter must be an integer value.";
		
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorMessage);
			}
		} 
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		Long lastTimeStamp = (Long)syncCache.get("place/"+this.placeId+"/application/"+this.appId+"/lastinputtimestamp");
		
		if ( null != lastTimeStamp ) {
			if (from >= lastTimeStamp.longValue() ) {
				return RestConverter.getWidgetInputList(new ArrayList<WidgetInputDao>());
			}
		}
		
		return 	getInputForAllWidgets(from);

	}

	@Override
	protected Class getResourceClass() {
		return WidgetInputRest.class;
	}


}
