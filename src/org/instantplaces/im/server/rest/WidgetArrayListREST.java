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
		StringBuilder sb = new StringBuilder();
		for (WidgetREST w : widgets) {
			sb.append(w.toString());
		}
		return sb.toString();
	}
}
