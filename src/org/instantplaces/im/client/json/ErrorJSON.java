package org.instantplaces.im.client.json;

import org.instantplaces.im.client.json.ErrorJSON;

import com.google.gwt.core.client.JavaScriptObject;

public class ErrorJSON extends GenericJSON  {
	// Overlay types always have protected, zero-arg ctors
	protected ErrorJSON() { }
	  
	public final native String getErrorMessage() /*-{ return this.errorMessage; }-*/;
	 
}
