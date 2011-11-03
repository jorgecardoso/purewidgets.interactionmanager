package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="widgets")
public class WidgetArrayListREST {

	@XmlElement(name = "widget")
	public ArrayList<WidgetREST> widgets;
	
	public WidgetArrayListREST() {
		
	}
	
	@Override
	public String toString() {
		if ( null != widgets ) {
			return "WidgetArrayListRest ( " + widgets.size() + " widgets )";
		} else {
			return "WidgetArrayListRest";
		}
	}
}
