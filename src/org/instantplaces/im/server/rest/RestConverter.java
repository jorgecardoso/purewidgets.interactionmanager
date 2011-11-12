/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.WidgetDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class RestConverter {

	public static WidgetArrayListREST widgetArrayListFromDso(List<WidgetDao> widgetDSOList) {
		
		ArrayList<WidgetRest> widgetListREST = new ArrayList<WidgetRest>();
		for ( WidgetDao wDSO : widgetDSOList ) {
			WidgetRest wREST = RestConverter.getWidgetRest(wDSO);
			widgetListREST.add(wREST);
		}
		
		WidgetArrayListREST widgetArrayList= new WidgetArrayListREST();
		widgetArrayList.widgets = widgetListREST;
		return widgetArrayList;
	}

	/**
	 * Converts a WidgetDSO object to a WidgetREST object.
	 * 
	 * @param widgetDao
	 * @return
	 */
	public static WidgetRest getWidgetRest(WidgetDao widgetDao) {
		WidgetRest  widgetRest = new WidgetRest();
	
		if ( null != widgetDao.getWidgetOptions() ) {
			for (WidgetOptionDao option : widgetDao.getWidgetOptions()) {
				widgetRest.addWidgetOption( RestConverter.widgetOptionFromDSO(option) );
			}
		}
		
		widgetRest.setPlaceId (widgetDao.getApplicationKey().getParent().getName() );
		widgetRest.setApplicationId( widgetDao.getApplicationKey().getName() );
		widgetRest.setWidgetId(widgetDao.getWidgetId());
		widgetRest.setControlType(widgetDao.getControlType());
		widgetRest.setVolatileWidget(widgetDao.isVolatileWidget());
		widgetRest.setShortDescription(widgetDao.getShortDescription());
		widgetRest.setLongDescription(widgetDao.getLongDescription());
		widgetRest.setContentUrl(widgetDao.getContentUrl());
		widgetRest.setUserResponse(widgetDao.getUserResponse());
		
		return widgetRest; 
	}

	/**
	 * Converts a WidgetOptionDSO object to a WidgetOptionREST object
	 * 
	 * @param widgetOptionDSO
	 * @return
	 */
	public static WidgetOptionRest widgetOptionFromDSO(WidgetOptionDao widgetOptionDSO) {
		WidgetOptionRest woREST = new WidgetOptionRest();
		woREST.setWidgetOptionId(widgetOptionDSO.getWidgetOptionId());
		woREST.setReferenceCode(widgetOptionDSO.getReferenceCode());
		woREST.setSuggestedReferenceCode(widgetOptionDSO.getSuggestedReferenceCode());
		woREST.setLongDescription(widgetOptionDSO.getLongDescripton());
		woREST.setShortDescription(widgetOptionDSO.getShortDescription());
		
		
		return woREST;
	}

	/**
	 * Converts a ApplicationDSO object to a ApplicationREST object.
	 * 
	 * @param widgetDSO
	 * @return
	 */
	public static ApplicationREST applicationFromDSO(ApplicationDao applicationDSO) {
		Log.get().debug("Converting to REST " + applicationDSO.toString());
		ApplicationREST  a = new ApplicationREST();
	
		a.setPlaceId( applicationDSO.getPlaceKey().getName() );
		a.setApplicationId( applicationDSO.getApplicationId() );
		
		
		/*for ( WidgetDSO widget : applicationDSO.getWidgets() ) {
			a.addWidget( widgetRestFromDso( widget ));
		}*/
		
		a.setLastRequestTimestamp(applicationDSO.getLastRequestTimestamp());
		
		Log.get().debug("Converted: " + a.toString());
		return a; 
	}

	public static ApplicationArrayListREST applicationArrayListFromDSO(List<ApplicationDao> applicationDSOList) {
		
		ArrayList<ApplicationREST> applicationListREST = new ArrayList<ApplicationREST>();
		for ( ApplicationDao aDSO : applicationDSOList ) {
			ApplicationREST aREST = applicationFromDSO(aDSO);
			applicationListREST.add(aREST);
		}
		
		ApplicationArrayListREST applicationArrayList= new ApplicationArrayListREST();
		applicationArrayList.applications = applicationListREST;
		return applicationArrayList;
	}
	
	public static WidgetInputArrayListREST widgetInputArrayListFromDso(List<WidgetInputDao> widgetInputDSOList) {
		
		ArrayList<WidgetInputREST> widgetListREST = new ArrayList<WidgetInputREST>();
		for ( WidgetInputDao aDSO : widgetInputDSOList ) {
			WidgetInputREST aREST = widgetInputFromDSO(aDSO);
			widgetListREST.add(aREST);
		}
		
		WidgetInputArrayListREST widgetInputArrayList= new WidgetInputArrayListREST();
		widgetInputArrayList.inputs = widgetListREST;
		return widgetInputArrayList;
	}


	public static WidgetInputREST widgetInputFromDSO(WidgetInputDao wiDso) {
		
		
		WidgetInputREST  w = new WidgetInputREST();
	
		
		w.setWidgetOptionId( wiDso.getWidgetOptionKey().getName() );
		w.setWidgetId( wiDso.getWidgetOptionKey().getParent().getName() );
		
		if ( null == wiDso.getParameters() ) {
			w.setParameters(new String[0]);
		} else {
			w.setParameters(wiDso.getParameters());
		}
		w.setPersona(wiDso.getPersona());
		w.setTimeStamp(""+wiDso.getTimeStamp());
		w.setInputMechanism(wiDso.getInputMechanism());
		w.setDelivered(wiDso.isDelivered());
		
	
		return w; 
	}

}
