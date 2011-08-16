package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.shared.WidgetOption;


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
	
	public static WidgetArrayListREST fromDSO(ArrayList<WidgetDSO> widgetDSOList) {
		
		ArrayList<WidgetREST> widgetListREST = new ArrayList<WidgetREST>();
		for ( WidgetDSO wDSO : widgetDSOList ) {
			WidgetREST wREST = wDSO.toREST();
			widgetListREST.add(wREST);
		}
		
		WidgetArrayListREST widgetArrayList= new WidgetArrayListREST();
		widgetArrayList.widgets = widgetListREST;
		return widgetArrayList;
	}
}
