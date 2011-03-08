package org.instantplaces.interactionmanager.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Contact {
	@XmlAttribute
	public String name;
	
	@XmlAttribute
	public int age;
	
	@XmlAttribute
	public int height;

	/**
	 * Needed because of the JAXB annotations...
	 */
	public Contact() {

	}

	public Contact(String name, int age) {
		this.name = name;
		this.age = age;
		this.height = 10;
	}

	

}
