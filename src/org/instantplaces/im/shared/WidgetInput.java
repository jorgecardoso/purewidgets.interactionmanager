package org.instantplaces.im.shared;


public interface WidgetInput {
	
	public void setWidgetId(String widgetId);
	public String getWidgetId();
	
	public void setReferenceCode(String referenceCode);
	public String getReferenceCode();
	
	public void setTimeStamp(Long timeStamp);
	public Long getTimeStamp();
	
	public void setParameters(String []parameters);
	public String[] getParameters();
	
	public void setPersona(String persona);
	public String getPersona();
}
