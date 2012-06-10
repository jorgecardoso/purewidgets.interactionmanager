/**
 * 
 */
package org.instantplaces.im.server.servlets;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ChannelMapDao;
import org.instantplaces.im.server.dao.Dao;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class ChannelDisconnected extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		
		String clientId = presence.clientId();
		
		Log.get().info("Client " + clientId + " disconnected.");
		
		try {
			Log.get().debug("Adding task to remove clientid");
			Queue queue = QueueFactory.getQueue("datastore");
			queue.add(withUrl("/task/remove-channel-clientid?clientId="+ clientId).method(Method.GET));
			
		} catch (TaskAlreadyExistsException taee) {
			Log.get().warn("Task already exists: " + taee.getMessage());
		} catch (Exception e) {
			Log.get().error("Could not submit task: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
