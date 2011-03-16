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
	public final native void setReferenceCode(String referenceCode) /*-{ 
		this.referenceCode = referenceCode;
	}-*/;

	@Override
	public final native String getReferenceCode() /*-{ 
		return this.referenceCode;
	}-*/;

	@Override
	public final native void setTimeStamp(Long timeStamp) /*-{ 
		this.timeStamp = timeStamp;
	}-*/;
	
	@Override
	public final native Long getTimeStamp() /*-{ 
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
