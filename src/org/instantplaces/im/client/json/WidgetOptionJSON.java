package org.instantplaces.im.client.json;

import org.instantplaces.im.shared.Widget;
import org.instantplaces.im.shared.WidgetOption;

public class WidgetOptionJSON extends GenericJSON implements WidgetOption {

	// Overlay types always have protected, zero-arg ctors
	protected WidgetOptionJSON() { 
	}
	
	
//	@Override
//	public final native void setWidgetId(String widgetId) /*-{
//		this.widgetId = widgetId;
//	}-*/;
//
//	@Override
//	public final native String getWidgetId() /*-{
//		return this.widgetId;
//	}-*/;

	@Override
	public final native void setId(String id) /*-{
		this.id = id;
	}-*/;

	@Override
	public final native String getId() /*-{
		return this.id;
	}-*/;

	@Override
	public final native void setSuggestedReferenceCode(String refCode) /*-{
		this.suggestedReferenceCode = refCode;
	}-*/;

	@Override
	public final native String getSuggestedReferenceCode() /*-{
		return this.suggestedReferenceCode;
	}-*/;

	@Override
	public final native void setReferenceCode(String refCode) /*-{
		this.referenceCode = refCode;
	}-*/;

	@Override
	public final native String getReferenceCode() /*-{
		return this.referenceCode;
	}-*/;

}
