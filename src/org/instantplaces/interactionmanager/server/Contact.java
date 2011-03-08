package org.instantplaces.interactionmanager.server;


public class Contact {

	 public String name;
	 public int age;
	 public int height;
		
	public Contact(String name, int age) {
		this.name = name;
		this.age = age;
		height = 10;
	}
	
	public String getName() {
		return name;
	}
	public int getAge() {
		return age;
	}
	public int getHeight() {
		return height;
	}
	
}
