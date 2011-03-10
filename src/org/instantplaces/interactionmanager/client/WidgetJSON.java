package org.instantplaces.interactionmanager.client;

import org.instantplaces.interactionmanager.shared.Application;
import org.instantplaces.interactionmanager.shared.Widget;
import org.instantplaces.interactionmanager.shared.WidgetOption;

import com.google.gwt.core.client.JavaScriptObject;

public class WidgetJSON extends JavaScriptObject implements Widget{

	// Overlay types always have protected, zero-arg ctors
	  protected WidgetJSON() { }
	  
	 public static native WidgetJSON getNew() /*-{
	  	return new Object();
	  }-*/;
	 
		public final native String toJSON() /*-{
		  return JSON.stringify(this);
		}-*/;	 
	  
	@Override
	public final native void setApplicationId(String appId)  /*-{ 
		this.applicationId = appId; 
	}-*/;

	@Override
	public final native String getApplicationId() /*-{ 
		return this.applicationId; 
	}-*/;

	@Override
	public final native void setId(String id)  /*-{ 
		this.id = id; 
	}-*/;

	@Override
	public final native String getId() /*-{ 
		return this.id; 
	}-*/;

	
}
