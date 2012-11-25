package org.instantplaces.im.server.rest.resource;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.logging.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.ChannelMapDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.DaoConverter;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.WidgetDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;
import org.instantplaces.im.server.rest.representation.json.InputResponseRest;
import org.instantplaces.im.server.rest.representation.json.RestConverter;
import org.instantplaces.im.server.rest.representation.json.WidgetInputListRest;
import org.instantplaces.im.server.rest.representation.json.WidgetInputRest;
import org.instantplaces.im.server.rest.representation.json.WidgetListRest;
import org.instantplaces.im.server.rest.representation.json.WidgetRest;
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
		
		handleInput(receivedWidgetInputRest, this.placeId, this.appId, this.widgetId);
		
		Dao.beginTransaction();
		PlaceDao placeDao = Dao.getPlace(this.placeId);
		ApplicationDao applicationDao = Dao.getApplication(this.placeId, this.appId);
		WidgetDao widgetDao = Dao.getWidget(this.placeId, this.appId, this.widgetId);
		Dao.commitOrRollbackTransaction();
		
		InputResponseRest inputResponseRest;
		inputResponseRest = new InputResponseRest(RestConverter.getPlaceRest(placeDao), 
												RestConverter.applicationFromDSO(applicationDao),
												RestConverter.getWidgetRest(widgetDao));
		
		return inputResponseRest;
	}
	

	
	public static  void saveInput(String placeReferenceCode, String userId, String userName, String refCode,
			String[] parameters, String interactionMechanism) {

		/*
		 * TODO: if place reference code is not provided, try to find the application anyway
		 */
		List<PlaceDao> places = Dao.getPlaces();
		
		for ( PlaceDao place : places ) {
			if ( place.getPlaceReferenceCode().equalsIgnoreCase(placeReferenceCode) ) {
				Dao.beginTransaction();
				WidgetOptionDao widgetOption = null; 
				
				List<WidgetOptionDao> options = Dao.getWidgetOptions(place.getPlaceId());
				
				for ( WidgetOptionDao option : options ) {
					if ( option.getReferenceCode().equals(refCode)) {
						widgetOption = option;
						break;
					}
				}
				
					
				if (widgetOption == null) {
					Log.debug(WidgetInputResource.class.getName(), "No widgets are using this reference code.");
					Dao.commitOrRollbackTransaction();
				} else {
					
					/*
					 * Create the widgetinputrest representation to use the widgetinputresource to save and forward the input
					 */
					WidgetInputRest widgetInputRest = new WidgetInputRest();
					
					widgetInputRest.setPlaceId(place.getPlaceId());
					widgetInputRest.setApplicationId(widgetOption.getWidgetKey().getParent().getName());
					widgetInputRest.setWidgetId(widgetOption.getWidgetKey().getName());
					widgetInputRest.setWidgetOptionId(widgetOption.getWidgetOptionId());
					widgetInputRest.setInputMechanism(interactionMechanism);
					widgetInputRest.setNickname(userName);
					widgetInputRest.setReferenceCode(widgetOption.getReferenceCode());
					widgetInputRest.setUserId(userId);
					widgetInputRest.setParameters(parameters);
					
					
					Dao.commitOrRollbackTransaction();
					
					WidgetInputResource.handleInput(widgetInputRest, widgetInputRest.getPlaceId(), widgetInputRest.getApplicationId(), widgetInputRest.getWidgetId());
				}
				
				
			}
			
		}



	}
	
	public static void handleInput(WidgetInputRest receivedWidgetInputRest, String placeId, String appId, String widgetId) {
		
		String referenceCode = receivedWidgetInputRest.getReferenceCode();
		
		
		if ( null == referenceCode ) {
			Log.get().warn("Could not get a valid reference code.");
			return;
		}
		
		Dao.beginTransaction();
		List<WidgetOptionDao> widgetOptionDaoList = Dao.getWidgetOptionsByReferenceCode(placeId, referenceCode);
		
		
		if ( null != widgetOptionDaoList ) {
			
			ArrayList<WidgetInputDao> inputList = new ArrayList<WidgetInputDao>();
			
			Log.get().debug("Forwarding input to " + widgetOptionDaoList.size() + " widget options");
			for ( WidgetOptionDao widgetOptionDao : widgetOptionDaoList ) {
			
				WidgetInputDao widgetInputDao = DaoConverter.getWidgetInputDao(widgetOptionDao, receivedWidgetInputRest);
	
				widgetInputDao.setTimeStamp( System.currentTimeMillis() );
				Dao.put( widgetInputDao );
				
				inputList.add(widgetInputDao);
			
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
				syncCache.put("place/"+placeId+"/application/"+widgetOptionDao.getWidgetKey().getParent().getName()+"/lastinputtimestamp", widgetInputDao.getTimeStamp());
			
				
				/*
				 * Log the input to get statistics
				 */
				WidgetInputResource.logInputStatistics(widgetInputDao);
			}
			Dao.commitOrRollbackTransaction();
			
			/*
			 * Send the input through the channel for each app
			 */
			for ( WidgetInputDao widgetInputDao : inputList ) {
				notifyServerApp(placeId, appId);
				/*
				 * Try to send the input through the channel to the application (client)
				 */
				sendInputThroughChannel(widgetInputDao, placeId, widgetInputDao.getWidgetOptionKey().getParent().getParent().getName());
			}
			
		} else {
			Log.get().warn("WidgetOption does not exist.");
			Dao.commitOrRollbackTransaction();
		}
	}
	
	
	private static void notifyServerApp(String placeId, String applicationId) {
		try {
			Queue queue = QueueFactory.getQueue("input");
			
			String url = "/task/notify-server-app?placeid="+placeId+"&appid="+applicationId;
			
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			Boolean inQueue = ((Boolean) syncCache.get(url));
			if ( null == inQueue || !inQueue.booleanValue() ) {
				Log.get().debug("Adding task: " + url);
				queue.add(withUrl(url).countdownMillis(30000).method(Method.GET));
				syncCache.put(url, new Boolean(true));
			} else {
				Log.get().debug("Task already exists: " + url);
			}
			
		} catch (TaskAlreadyExistsException taee) {
			Log.get().warn("Task already exists: " + taee.getMessage());
		} catch (Exception e) {
			Log.get().error("Could not submit task: " + e.getMessage());
			e.printStackTrace();
		}
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
		if (this.getMethod().equals(Method.POST)) {
			return InputResponseRest.class;
		} else {
			return WidgetInputRest.class;
		} 
	}
}
