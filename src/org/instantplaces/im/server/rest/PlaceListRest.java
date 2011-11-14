/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class PlaceListRest {
	private ArrayList<PlaceRest> places;
	
	public PlaceListRest() {
	}

	/**
	 * @return the places
	 */
	public ArrayList<PlaceRest> getPlaces() {
		return places;
	}

	/**
	 * @param places the places to set
	 */
	public void setPlaces(ArrayList<PlaceRest> places) {
		this.places = places;
	}
}
