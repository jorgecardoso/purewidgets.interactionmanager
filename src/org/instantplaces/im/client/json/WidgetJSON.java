package org.instantplaces.im.client.json;

import org.instantplaces.im.shared.Application;
import org.instantplaces.im.shared.Widget;
import org.instantplaces.im.shared.WidgetOption;

import com.google.gwt.core.client.JavaScriptObject;

public class WidgetJSON extends GenericJSON implements Widget {

 
	// Overlay types always have protected, zero-arg ctors
   protected WidgetJSON() { 
	   
   }


   public final String getBaseURL() {
	   return  "/place/" + this.getPlaceId() + "/application/" + this.getApplicationId() + 
	   	"/widget";
   }
   
   public final String getURL() {
	   return  this.getBaseURL() + "/" +this.getId() + "?output=json";
   }
   
	@Override
	public final native void setPlaceId(String placeId)  /*-{ 
		this.placeId = placeId; 
	}-*/;

	@Override
	public final native String getPlaceId() /*-{ 
		return this.placeId; 
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

	@Override
	public final native WidgetOption[] getWidgetOptions() /*-{
		return this.widgetOptions;
	}-*/;


	@Override
	public final native void addWidgetOption(WidgetOption widgetOption) /*-{
		if (typeof(this.widgetOptions) == "undefined") {
			this.widgetOptions = new Array();
		}
		this.widgetOptions.push(widgetOption);
	}-*/;

	
}
