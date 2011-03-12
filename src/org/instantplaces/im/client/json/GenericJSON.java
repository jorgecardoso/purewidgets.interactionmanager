package org.instantplaces.im.client.json;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class GenericJSON extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	  protected GenericJSON() { }
	  
	  public static native <T> T getNew() /*-{
	  	return new Object();
	  }-*/;
	  
	  public static native <T> T fromJSON(String json) /*-{
	  	return eval('('+json+')');
	  }-*/;
	  
	public final native String toJSON() /*-{
	  return JSON.stringify(this);
	}-*/;
}
