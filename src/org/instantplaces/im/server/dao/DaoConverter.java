package org.instantplaces.im.server.dao;

import org.instantplaces.im.server.rest.WidgetOptionRest;
import org.instantplaces.im.server.rest.WidgetRest;

public class DaoConverter {

	public static WidgetOptionDao getWidgetOptionDao(WidgetDao parent, WidgetOptionRest widgetOptionRest) {
		WidgetOptionDao widgetOptionDao = new WidgetOptionDao(parent, widgetOptionRest.getWidgetOptionId());

		widgetOptionDao.setSuggestedReferenceCode(widgetOptionRest.getSuggestedReferenceCode());
		widgetOptionDao.setReferenceCode(widgetOptionRest.getReferenceCode());
		widgetOptionDao.setLongDescripton(widgetOptionRest.getLongDescription());
		widgetOptionDao.setShortDescription(widgetOptionRest.getShortDescription());
		
		return widgetOptionDao;
	}

	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetRest
	 * @return
	 */
	public static WidgetDao getWidgetDao(ApplicationDao parent, WidgetRest widgetRest) {
		WidgetDao widgetDao = new WidgetDao(parent, widgetRest.getWidgetId(), widgetRest.getControlType(), widgetRest.getShortDescription(), widgetRest.getLongDescription());
		
		widgetDao.setVolatileWidget(widgetRest.isVolatileWidget());
		widgetDao.setContentUrl(widgetRest.getContentUrl());
		widgetDao.setUserResponse(widgetRest.getUserResponse());
		
		for (WidgetOptionRest woRest : widgetRest.getWidgetOptions()) {
			widgetDao.addWidgetOption(DaoConverter.getWidgetOptionDao(widgetDao, woRest));
		}
		
		return widgetDao;
	}

}
