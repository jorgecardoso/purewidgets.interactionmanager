package org.instantplaces.im.server.rest.resource.task;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ChannelMapDao;
import org.instantplaces.im.server.dao.DaoTmp;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class TaskRemoveChannelClientId  extends ServerResource {
	@Get
	public Object doGet() {
		
		String clientId = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("clientId", "");
				
	
		String placeApplication = ChannelMapDao.getPlaceApplicationString(clientId);
		DaoTmp.beginTransaction();
		ChannelMapDao channelMap = DaoTmp.getChannelMap(placeApplication);
		if ( null != channelMap ) {
			Log.get().warn("Removing clientId from channel map");
			channelMap.remove(clientId);
			DaoTmp.put(channelMap);
		}
		
		
		if ( !DaoTmp.commitOrRollbackTransaction() ) {
			Log.get().warn("Could not commit transaction, trying again");
			try {
				Queue queue = QueueFactory.getQueue("datastore");
				queue.add(withUrl("/task/remove-channel-clientid?clientId="+ clientId).method(Method.GET));
				
			} catch (TaskAlreadyExistsException taee) {
				Log.get().warn("Task already exists: " + taee.getMessage());
			} catch (Exception e) {
				Log.get().error("Could not submit task: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return "";
	}
}
