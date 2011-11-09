/**
 * 
 */
package org.instantplaces.im.server.resource;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.ArrayList;
import java.util.Iterator;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.comm.InputRequest;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.DsoFetcher;
import org.instantplaces.im.server.rest.ApplicationArrayListREST;
import org.instantplaces.im.server.rest.ApplicationREST;
import org.instantplaces.im.server.rest.RestConverter;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * @author "Jorge C. S. Cardoso"
 * 
 */
public class TaskFetchInputResource extends ServerResource {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.instantplaces.im.server.resource.GenericResource#doGet()
	 */
	@Get
	public Object doGet() {
		try {
			InputRequest.getPresences();

		} catch (Exception e) {
			Log.get().error("Could not fetch input: " + e.getMessage());
		}
//
//		try {
//			Queue queue = QueueFactory.getDefaultQueue();
//			queue.deleteTask("FetchInput");
//			queue.add(withUrl("/task/fetch-input").taskName("FetchInput")
//					.countdownMillis(15 * 1000).method(Method.GET));
//		} catch (TaskAlreadyExistsException taee) {
//			Log.get().error("Could not resubmit task " + taee.getMessage());
//		} catch (Exception e) {
//			Log.get().error("Could not submit task: " + e.getMessage());
//		}

		return "";

	}

}
