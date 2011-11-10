/**
 * 
 */
package org.instantplaces.im.server.resource;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.comm.InputRequest;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

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

		return "";

	}

}
