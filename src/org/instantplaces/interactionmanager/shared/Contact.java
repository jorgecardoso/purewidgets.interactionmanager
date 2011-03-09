package org.instantplaces.interactionmanager.shared;

public interface Contact {
	public String getEmail();
	public String getName();
	public int getAge();
	public int getHeight();
	
	public void setEmail(String email);
	public void setName(String name);
	public void setAge(int age);
	public void setHeight(int height);
}
