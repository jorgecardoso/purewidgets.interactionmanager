package org.instantplaces.im.client.json;

import org.instantplaces.im.shared.WidgetInput;

public class WidgetInputJSON extends GenericJSON implements WidgetInput {

	// Overlay types always have protected, zero-arg ctors
   protected WidgetInputJSON() { 
		   
   }
	   
	@Override
	public final native void setWidgetId(String widgetId) /*-{ 
		this.widgetId = widgetId; 
	}-*/;

	@Override
	public final native String getWidgetId() /*-{ 
		return this.widgetId;
	}-*/;

	@Override
	public final native void setWidgetOptionId(String widgetOptionId) /*-{ 
		this.widgetOptionId = widgetOptionId;
	}-*/;

	@Override
	public final native String getWidgetOptionId() /*-{ 
		return this.widgetOptionId;
	}-*/;

	@Override
	public final native void setTimeStamp(String timeStamp) /*-{ 
		this.timeStamp = timeStamp;
	}-*/;
	
	@Override
	public final native String getTimeStamp() /*-{ 
		return this.timeStamp;
	}-*/;

	@Override
	public final native void setParameters(String[] parameters) /*-{ 
		this.parameters = parameters;
	}-*/;

	@Override
	public final native String[] getParameters() /*-{ 
		return this.parameters;
	}-*/;

	@Override
	public final native void setPersona(String persona) /*-{ 
		this.persona = persona;
	}-*/;

	@Override
	public final native String getPersona() /*-{ 
		return this.persona;
	}-*/;

}
