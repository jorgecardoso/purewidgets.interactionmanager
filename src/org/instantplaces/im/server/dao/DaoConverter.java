package org.instantplaces.im.server.dao;

import java.util.ArrayList;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.representation.json.ApplicationRest;
import org.instantplaces.im.server.rest.representation.json.WidgetInputRest;
import org.instantplaces.im.server.rest.representation.json.WidgetOptionRest;
import org.instantplaces.im.server.rest.representation.json.WidgetParameterRest;
import org.instantplaces.im.server.rest.representation.json.WidgetRest;

public class DaoConverter {

	
	public static WidgetInputDao getWidgetInputDao(WidgetOptionDao parent, WidgetInputRest widgetInputRest) {
		
		long timeStamp = 0;
		try {
			timeStamp = Long.parseLong(widgetInputRest.getTimeStamp());
		} catch (NumberFormatException nfe) {
			Log.get().warn("Could not parse timestamp: " + widgetInputRest.getTimeStamp());
		}
		
		WidgetInputDao widgetInputDao = new WidgetInputDao(parent.getKey(), timeStamp, widgetInputRest.getParameters(), widgetInputRest.getUserId(), widgetInputRest.getNickname());
		widgetInputDao.setInputMechanism(widgetInputRest.getInputMechanism());
		
		return widgetInputDao;
	}
 	
	
	public static WidgetOptionDao getWidgetOptionDao(WidgetDao parent, WidgetOptionRest widgetOptionRest) {
		WidgetOptionDao widgetOptionDao = new WidgetOptionDao(parent.getKey(), widgetOptionRest.getWidgetOptionId());

		widgetOptionDao.setSuggestedReferenceCode(widgetOptionRest.getSuggestedReferenceCode());
		widgetOptionDao.setReferenceCode(widgetOptionRest.getReferenceCode());
		widgetOptionDao.setLongDescripton(widgetOptionRest.getLongDescription());
		widgetOptionDao.setShortDescription(widgetOptionRest.getShortDescription());
		widgetOptionDao.setIconUrl(widgetOptionRest.getIconUrl());
		
		return widgetOptionDao;
	}
	
	
	public static ArrayList<WidgetParameterDao> getWidgetParameterDao(ArrayList<WidgetParameterRest> widgetParameterRest) {
		ArrayList<WidgetParameterDao> parameters = new ArrayList<WidgetParameterDao>();
		
		for ( WidgetParameterRest parameterRest : widgetParameterRest ) {
			parameters.add( DaoConverter.getWidgetParameterDao(parameterRest) );
		}
			
		return parameters;
	}
	
	public static WidgetParameterDao getWidgetParameterDao(WidgetParameterRest widgetParameterRest) {
		WidgetParameterDao widgetParameterDao = new WidgetParameterDao(widgetParameterRest.getName(), widgetParameterRest.getValue());
		
		return widgetParameterDao;
	}

	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetRest
	 * @return
	 */
	public static WidgetDao getWidgetDao(ApplicationDao parent, WidgetRest widgetRest) {
		WidgetDao widgetDao = new WidgetDao(parent.getKey(), widgetRest.getWidgetId(), widgetRest.getControlType(), widgetRest.getShortDescription(), widgetRest.getLongDescription());
		
	
		
		for (WidgetOptionRest woRest : widgetRest.getWidgetOptions()) {
			widgetDao.addWidgetOption(DaoConverter.getWidgetOptionDao(widgetDao, woRest));
		}
		
		for (WidgetParameterRest wpRest : widgetRest.getWidgetParameters()) {
			widgetDao.addWidgetParameter(DaoConverter.getWidgetParameterDao(wpRest));
		}
		
		return widgetDao;
	}

	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetRest
	 * @return
	 */
	public static ApplicationDao getApplicationDao(PlaceDaoTmp parent, ApplicationRest applicationRest) {
		ApplicationDao applicationDao = new ApplicationDao(parent, applicationRest.getApplicationId());
		
		applicationDao.setApplicationBaseUrl(applicationRest.getApplicationBaseUrl());
		applicationDao.setLastRequestTimestamp(applicationRest.getLastRequestTimestamp());
		
		return applicationDao;
	}

}
