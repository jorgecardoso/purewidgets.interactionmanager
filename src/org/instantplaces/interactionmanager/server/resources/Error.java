package org.instantplaces.interactionmanager.server.resources;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.client.ContactJSON;

@XmlRootElement
public class Error {
	
	@XmlAttribute
	public String errorMessage;
	
	@XmlElement
	public Object object; 
	
	public Error() {
		
	}
	
	public Error(Object object, String message) {
		this.object = object;
		this.errorMessage = message;
	}
	
	 
}
