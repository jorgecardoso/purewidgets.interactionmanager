package org.instantplaces.im.shared;


public interface WidgetInput {
	
	public void setWidgetId(String widgetId);
	public String getWidgetId();
	
	public void setWidgetOptionId(String widgetOptionId);
	public String getWidgetOptionId();
	
	public void setTimeStamp(String timeStamp);
	public String getTimeStamp();
	
	public void setParameters(String []parameters);
	public String[] getParameters();
	
	public void setPersona(String persona);
	public String getPersona();
}
