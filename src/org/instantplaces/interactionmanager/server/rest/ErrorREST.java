package org.instantplaces.interactionmanager.server.rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.client.json.ContactJSON;

@XmlRootElement
public class ErrorREST {
	
	@XmlAttribute
	public String errorMessage;
	
	@XmlElement
	public Object object; 
	
	public ErrorREST() {
		
	}
	
	public ErrorREST(Object object, String message) {
		this.object = object;
		this.errorMessage = message;
	}
	
	 
}
