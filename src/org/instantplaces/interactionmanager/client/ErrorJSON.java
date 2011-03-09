package org.instantplaces.interactionmanager.client;

import org.instantplaces.interactionmanager.client.ErrorJSON;

import com.google.gwt.core.client.JavaScriptObject;

public class ErrorJSON extends JavaScriptObject  {
	// Overlay types always have protected, zero-arg ctors
	  protected ErrorJSON() { }
	  
		public final native String getErrorMessage() /*-{ return this.errorMessage; }-*/;
	 
		 public static native ErrorJSON fromJSON(String json) /*-{
		  	return eval('('+json+')');
		  }-*/;
}
