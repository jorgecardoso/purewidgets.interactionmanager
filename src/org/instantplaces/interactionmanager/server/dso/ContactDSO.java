package org.instantplaces.interactionmanager.server.dso;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.shared.Contact;

import com.google.appengine.api.datastore.Key;




@PersistenceCapable
@XmlRootElement
public class ContactDSO implements Contact  {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

    @Persistent
    @Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
    private String email;
	
	@Persistent
	@XmlAttribute
	private String name;
	
	@Persistent
	@XmlAttribute
	private int age;
	
	@Persistent
	@XmlAttribute
	private int height;


	/**
	 * Needed because of the JAXB annotations
	 */
	
	public ContactDSO() {
		
	}

	public ContactDSO(String name, int age) {
		this.name = name;
		this.age = age;
		this.height = 10;

	}
	
	public void copyFrom(ContactDSO other) {
		this.setEmail(other.getEmail());
		this.setName(other.getName());
		this.setAge(other.getAge());
		this.setHeight(other.getHeight());
	}

	public String toHTML() {
		return "Name: " + this.name + "<br> Age: " + this.age + "<br> Height: " + this.height;
	}
	
	@Override
    public String getEmail() {
        return this.email;
    }

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public void setAge(int age) {
		this.age = age;		
	}

	@Override
	public void setHeight(int height) {
		this.height = height;		
	}
	
	@Override
	public void setEmail(String email) {
		this.email = email;
	}


}
