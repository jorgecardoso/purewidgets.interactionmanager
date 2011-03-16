package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="widgetinputs")
public class WidgetInputArrayListREST {

	@XmlElement(name = "input")
	public ArrayList<WidgetInputREST> inputs;
	
	public WidgetInputArrayListREST() {
		
	}
	
	@Override
	public String toString() {
		return null;
	}
	//TODO: make a toString representation
}
