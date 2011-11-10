package org.instantplaces.im.server.rest;


public class ErrorREST {
	

	public String errorMessage;

	public Object object; 
	
	public ErrorREST() {
		
	}
	
	public ErrorREST(Object object, String message) {
		this.object = object;
		this.errorMessage = message;
	}
	
	 
}
