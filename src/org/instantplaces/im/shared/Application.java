package org.instantplaces.im.shared;

public interface Application {
	
	public void setApplicationId(String appId);
	public String getApplicationId();
	
	
	public void setPlace(Place place);
	public Place getPlace();
	
	
	public void addWidget(Widget widget); 
	public Widget[] getWidgets();
}
