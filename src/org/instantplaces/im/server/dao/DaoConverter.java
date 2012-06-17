package org.instantplaces.im.server.dao;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.representation.json.ApplicationRest;
import org.instantplaces.im.server.rest.representation.json.WidgetInputRest;
import org.instantplaces.im.server.rest.representation.json.WidgetOptionRest;
import org.instantplaces.im.server.rest.representation.json.WidgetRest;

public class DaoConverter {

	
	public static WidgetInputDao getWidgetInputDao(WidgetOptionDao parent, WidgetInputRest widgetInputRest) {
		
		long timeStamp = 0;
		try {
			timeStamp = Long.parseLong(widgetInputRest.getTimeStamp());
		} catch (NumberFormatException nfe) {
			Log.get().warn("Could not parse timestamp: " + widgetInputRest.getTimeStamp());
		}
		
		WidgetInputDao widgetInputDao = new WidgetInputDao(parent.getKey(), timeStamp, widgetInputRest.getParameters(), widgetInputRest.getPersona());
		widgetInputDao.setInputMechanism(widgetInputRest.getInputMechanism());
		widgetInputDao.setUserIdentifier(widgetInputRest.getUserIdentifier());
		return widgetInputDao;
	}
 	
	
	public static WidgetOptionDao getWidgetOptionDao(WidgetDao parent, WidgetOptionRest widgetOptionRest) {
		WidgetOptionDao widgetOptionDao = new WidgetOptionDao(parent.getKey(), widgetOptionRest.getWidgetOptionId());

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
		WidgetDao widgetDao = new WidgetDao(parent.getKey(), widgetRest.getWidgetId(), widgetRest.getControlType(), widgetRest.getShortDescription(), widgetRest.getLongDescription());
		
		widgetDao.setVolatileWidget(widgetRest.isVolatileWidget());
		widgetDao.setContentUrl(widgetRest.getContentUrl());
		widgetDao.setUserResponse(widgetRest.getUserResponse());
		
		for (WidgetOptionRest woRest : widgetRest.getWidgetOptions()) {
			widgetDao.addWidgetOption(DaoConverter.getWidgetOptionDao(widgetDao, woRest));
		}
		
		return widgetDao;
	}

	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetRest
	 * @return
	 */
	public static ApplicationDao getApplicationDao(PlaceDao parent, ApplicationRest applicationRest) {
		ApplicationDao applicationDao = new ApplicationDao(parent, applicationRest.getApplicationId());
		
		applicationDao.setIconBaseUrl(applicationRest.getIconBaseUrl());
		applicationDao.setLastRequestTimestamp(applicationRest.getLastRequestTimestamp());
		
		return applicationDao;
	}

}
