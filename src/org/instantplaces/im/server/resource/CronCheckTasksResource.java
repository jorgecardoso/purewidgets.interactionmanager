package org.instantplaces.im.server.resource;

import org.instantplaces.im.server.Log;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

public class CronCheckTasksResource extends ServerResource{
	@Override
	public void doInit() {
		Log.get().debug("Cron: deleting inactive applications.");
	}
	
	@Get
	public Representation runCron() {
		try {
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/task/fetch-input").countdownMillis(15*1000).method(Method.GET));
			queue.add(withUrl("/task/fetch-input").countdownMillis(30*1000).method(Method.GET));
			queue.add(withUrl("/task/fetch-input").countdownMillis(45*1000).method(Method.GET));
			queue.add(withUrl("/task/fetch-input").countdownMillis(60*1000).method(Method.GET));
		} catch (TaskAlreadyExistsException taee) {
			
		} catch (Exception e) {
			Log.get().error("Could not submit task: " + e.getMessage());
		}
		return null;
	}
}
