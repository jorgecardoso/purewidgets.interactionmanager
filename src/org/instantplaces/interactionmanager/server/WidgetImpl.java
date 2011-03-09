package org.instantplaces.interactionmanager.server;

import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.shared.Widget;

@XmlRootElement
public class WidgetImpl implements Widget {

	String id;
	
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	
}
