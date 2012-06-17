package org.instantplaces.im.server.rest.representation.json;

import java.util.ArrayList;


public class WidgetInputListRest {


	private ArrayList<WidgetInputRest> inputs;
	
	public WidgetInputListRest() {
	}
	
	public WidgetInputListRest(ArrayList<WidgetInputRest> inputs) {
		this.inputs = inputs;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (WidgetInputRest w : inputs) {
			sb.append(w.toString());
		}
		return sb.toString();
	}

	/**
	 * @return the inputs
	 */
	public ArrayList<WidgetInputRest> getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(ArrayList<WidgetInputRest> inputs) {
		this.inputs = inputs;
	}
}
