/**
 * 
 */
package org.instantplaces.im.server.rest;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
//@JsonIgnoreProperties({ "widgets" }) // @JsonIgnore seems to not work correctly, so we duplicate
public class PlaceRest {
	

	private	String placeId;


	public PlaceRest() {
	}	
	

	public void setPlaceId(String placeId) {
		this.placeId = placeId;	
	}

	
	public String getPlaceId() {
		return placeId;
	}	
}