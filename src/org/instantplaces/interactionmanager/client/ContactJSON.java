package org.instantplaces.interactionmanager.client;

import org.instantplaces.interactionmanager.shared.Contact;

import com.google.gwt.core.client.JavaScriptObject;

public class ContactJSON extends JavaScriptObject implements Contact {
	
	// Overlay types always have protected, zero-arg ctors
	  protected ContactJSON() { }

	  
	  // Typically, methods on overlay types are JSNI
	  public final native String getEmail() /*-{ return this.email; }-*/;
	  public final native String getName() /*-{ return this.name; }-*/;
	  public final native int getAge()  /*-{ return this.age;  }-*/;
	  public final native int getHeight()  /*-{ return this.height;  }-*/;
	
	  public static native ContactJSON getNew() /*-{
	  	return new Object();
	  }-*/;
	  
	  public static native ContactJSON fromJSON(String json) /*-{
	  	return eval('('+json+')');
	  }-*/;


		@Override
		public  final native void setEmail(String email) /*-{
			this.email = email;
		}-*/;
		
	@Override
	public  final native void setName(String name) /*-{
		this.name = name;
	}-*/;

	@Override
	public  final native void setAge(int age) /*-{
		this.age = age;
	}-*/;

	@Override
	public  final native void setHeight(int height) /*-{
		this.height = height;
	}-*/;
	
	public final native String toJSON() /*-{
	  return JSON.stringify(this);
	}-*/;
}
