package org.instantplaces.im.server.dao;

import org.instantplaces.im.server.rest.WidgetOptionREST;
import org.instantplaces.im.server.rest.WidgetREST;

public class DaoConverter {

	public static WidgetOptionDAO widgetOptionDSOfromRest(WidgetDAO parent, WidgetOptionREST widgetOptionREST) {
		WidgetOptionDAO woDSO = new WidgetOptionDAO(parent, widgetOptionREST.getWidgetOptionId());

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
	public static WidgetDAO widgetDSOfromRest(ApplicationDAO parent, WidgetREST widgetREST) {
		WidgetDAO wDSO = new WidgetDAO(parent, widgetREST.getWidgetId(), widgetREST.getControlType(), widgetREST.getShortDescription(), widgetREST.getLongDescription());
		
		wDSO.setVolatileWidget(widgetREST.isVolatileWidget());
		
		for (WidgetOptionREST woRest : widgetREST.getWidgetOptions()) {
			wDSO.addWidgetOption(DaoConverter.widgetOptionDSOfromRest(wDSO, woRest));
		}
		
		return wDSO;
	}

}
