package org.instantplaces.im.server.rest.representation.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;



@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class WidgetParameterRest {
	

	private String name;
	

	private String value;
	
	
	
	public WidgetParameterRest() {
		
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}



	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	


}
