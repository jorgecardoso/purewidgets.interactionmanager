package org.instantplaces.im.server.dao;

import org.instantplaces.im.server.rest.WidgetOptionREST;
import org.instantplaces.im.server.rest.WidgetREST;

public class DaoConverter {

	public static WidgetOptionDao widgetOptionDSOfromRest(WidgetDao parent, WidgetOptionREST widgetOptionREST) {
		WidgetOptionDao woDSO = new WidgetOptionDao(parent, widgetOptionREST.getWidgetOptionId());

		woDSO.setSuggestedReferenceCode(widgetOptionREST.getSuggestedReferenceCode());
		woDSO.setReferenceCode(widgetOptionREST.getReferenceCode());
		woDSO.setLongDescripton(widgetOptionREST.getLongDescription());
		woDSO.setShortDescription(widgetOptionREST.getShortDescription());
		
		return woDSO;
	}

	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetREST
	 * @return
	 */
	public static WidgetDao widgetDSOfromRest(ApplicationDao parent, WidgetREST widgetREST) {
		WidgetDao wDSO = new WidgetDao(parent, widgetREST.getWidgetId(), widgetREST.getControlType(), widgetREST.getShortDescription(), widgetREST.getLongDescription());
		
		wDSO.setVolatileWidget(widgetREST.isVolatileWidget());
		
		for (WidgetOptionREST woRest : widgetREST.getWidgetOptions()) {
			wDSO.addWidgetOption(DaoConverter.widgetOptionDSOfromRest(wDSO, woRest));
		}
		
		return wDSO;
	}

}
