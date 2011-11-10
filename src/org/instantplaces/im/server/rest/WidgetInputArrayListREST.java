package org.instantplaces.im.server.rest;

import java.util.ArrayList;


public class WidgetInputArrayListREST {


	public ArrayList<WidgetInputREST> inputs;
	
	public WidgetInputArrayListREST() {
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (WidgetInputREST w : inputs) {
			sb.append(w.toString());
		}
		return sb.toString();
	}
}
