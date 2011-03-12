package org.instantplaces.im.shared;



public interface Widget {
	public void setPlaceId(String placeId);
	public String getPlaceId();
	
	
	public void setApplicationId(String appId);
	public String getApplicationId();
	
	
	public void setId(String id);
	public String getId();
	
	public WidgetOption[] getWidgetOptions();
	public void addWidgetOption(WidgetOption widgetOption);
	
	
}
