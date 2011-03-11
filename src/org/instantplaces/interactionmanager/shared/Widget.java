package org.instantplaces.interactionmanager.shared;

import java.util.ArrayList;

public interface Widget {
	public void setPlaceId(String placeId);
	public String getPlaceId();
	
	
	public void setApplicationId(String appId);
	public String getApplicationId();
	
	
	public void setId(String id);
	public String getId();
	
	public WidgetOption[] getWidgetOptions();
	public void addWidgetOption(WidgetOption widgetOption);
	
	//public void addOptionID
	/*
	public void addOption(WidgetOption option);
	public WidgetOption[] getOptions();
	*/
	//public void set
}
