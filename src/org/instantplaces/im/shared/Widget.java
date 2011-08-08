package org.instantplaces.im.shared;



public interface Widget {
	
	public void setPlaceId(String placeId);
	public String getPlaceId();
	
	
	public void setApplicationId(String appId);
	public String getApplicationId();
	
	
	public void setWidgetId(String widgetId);
	public String getWidgetId();
	
	public boolean isVolatileWidget(); 
	public void setVolatileWidget(boolean isVolatileWidget); 
	
	public WidgetOption[] getWidgetOptions();
	public void addWidgetOption(WidgetOption widgetOption);
	
	
}
