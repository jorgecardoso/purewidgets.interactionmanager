package org.instantplaces.im.server.rest.representation.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class InputResponseRest {

	private PlaceRest place;
	private ApplicationRest application;
	private WidgetRest widget;
	
	public InputResponseRest(PlaceRest placeRest, ApplicationRest applicationRest,
			WidgetRest widgetRest) {
		this.place = placeRest;
		this.application = applicationRest;
		this.widget = widgetRest;
				
	}

	/**
	 * @return the place
	 */
	public PlaceRest getPlace() {
		return place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(PlaceRest place) {
		this.place = place;
	}

	/**
	 * @return the application
	 */
	public ApplicationRest getApplication() {
		return application;
	}

	/**
	 * @param application the application to set
	 */
	public void setApplication(ApplicationRest application) {
		this.application = application;
	}

	/**
	 * @return the widget
	 */
	public WidgetRest getWidget() {
		return widget;
	}

	/**
	 * @param widget the widget to set
	 */
	public void setWidget(WidgetRest widget) {
		this.widget = widget;
	}

}
