/**
 * 
 */
package org.instantplaces.im.server.rest.representation.json;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ApplicationListRest {

	private ArrayList<ApplicationRest> applications;
	
	private String placeId;
	
	public ApplicationListRest() {
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Applications: ");
		for (ApplicationRest a : getApplications()) {
			sb.append(a.toString());
		}
		return sb.toString();
	}

	/**
	 * @return the applications
	 */
	public ArrayList<ApplicationRest> getApplications() {
		return applications;
	}

	/**
	 * @param applications the applications to set
	 */
	public void setApplications(ArrayList<ApplicationRest> applications) {
		this.applications = applications;
	}

	/**
	 * @return the placeId
	 */
	public String getPlaceId() {
		return placeId;
	}

	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
}
