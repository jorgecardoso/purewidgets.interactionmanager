package org.instantplaces.im.server.rest;

import java.util.ArrayList;



public class WidgetArrayListREST {

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
