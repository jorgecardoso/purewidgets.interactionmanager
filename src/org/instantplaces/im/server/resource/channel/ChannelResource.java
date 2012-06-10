/**
 * 
 */
package org.instantplaces.im.server.resource.channel;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ChannelMapDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.resource.GenericResource;
import org.instantplaces.im.server.rest.ChannelTokenRest;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

/**
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class ChannelResource extends GenericResource {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.instantplaces.im.server.resource.GenericResource#doGet()
	 */
	@Get
	public Object doGet() {
		
		 ChannelService channelService = ChannelServiceFactory.getChannelService();

		 Dao.beginTransaction();
		 ChannelMapDao channelMap = Dao.getChannelMap(this.placeId, this.appId);
		 
		 if ( null == channelMap ) {
			 Log.get().debug("Could not read channel map for " + this.placeId + "-" + this.appId + " from datastore, creating new...");
			 channelMap = new ChannelMapDao(this.placeId, this.appId);
		 }
		 
		 String clientId = channelMap.getNextClientId();
		 String token = channelService.createChannel(clientId, 24*60-1);

		 Log.get().debug("Created channel token for: " + this.placeId + "-" + this.appId);
		 
		 Dao.put(channelMap);
		 
		 Dao.commitOrRollbackTransaction();
		 
		 return new ChannelTokenRest(token);
	}

	/* (non-Javadoc)
	 * @see org.instantplaces.im.server.resource.GenericResource#getResourceClass()
	 */
	@Override
	protected Class getResourceClass() {
		return ChannelResource.class;
	}

	@Override
	protected Object doPost(Object incoming) {
		return null;
	}

	@Override
	protected Object doPut(Object incoming) {
		return null;
	}

	@Override
	protected Object doDelete() {
		return null;
	}
}
