package org.instantplaces.im.server.dao;

import java.io.Serializable;


import com.googlecode.objectify.annotation.Unindexed;

public class WidgetParameterDao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Unindexed
	private String name;

	@Unindexed
	private String value;

	


	public WidgetParameterDao(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@SuppressWarnings("unused")
	private WidgetParameterDao() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object widgetParameterDao) {
		if ( !(widgetParameterDao instanceof WidgetParameterDao) ) {
			return false;
		}
		
		WidgetParameterDao w = (WidgetParameterDao)widgetParameterDao;
		return this.name.equals(w.getName()) && this.value.equals(w.getValue());
	}

}
