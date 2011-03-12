package org.instantplaces.im.shared;

public interface Place {
	
	public void setPlaceID(String placeID);
	public String getPlaceID();
	
	public void addApplication(Application app); 
	public Application[] getApplications();
}
