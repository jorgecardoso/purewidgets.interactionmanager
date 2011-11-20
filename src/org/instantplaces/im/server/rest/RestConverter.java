/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.WidgetDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class RestConverter {

	public static PlaceRest getPlaceRest(PlaceDao placeDao) {
		PlaceRest placeRest = new PlaceRest();
		placeRest.setPlaceId(placeDao.getPlaceId());
		
		return placeRest;
		
	}
	
	public static PlaceListRest getPlaceListRest(List<PlaceDao> placeListDao) {
		
		
		ArrayList<PlaceRest> placesRest = new ArrayList<PlaceRest>();
		
		for ( PlaceDao placeDao : placeListDao ) {
			placesRest.add( RestConverter.getPlaceRest(placeDao) );
		}
		
		PlaceListRest placeListRest = new PlaceListRest();
		placeListRest.setPlaces(placesRest);
		return placeListRest;
	}
	
	public static WidgetListRest widgetArrayListFromDso(List<WidgetDao> widgetDSOList) {
		
		ArrayList<WidgetRest> widgetListREST = new ArrayList<WidgetRest>();
		for ( WidgetDao wDSO : widgetDSOList ) {
			WidgetRest wREST = RestConverter.getWidgetRest(wDSO);
			widgetListREST.add(wREST);
		}
		
		WidgetListRest widgetArrayList= new WidgetListRest();
		widgetArrayList.setWidgets(widgetListREST);
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
	public static ApplicationRest applicationFromDSO(ApplicationDao applicationDSO) {
		Log.get().debug("Converting to REST " + applicationDSO.toString());
		ApplicationRest  a = new ApplicationRest();
	
		a.setPlaceId( applicationDSO.getPlaceKey().getName() );
		a.setApplicationId( applicationDSO.getApplicationId() );
		
		
		/*for ( WidgetDSO widget : applicationDSO.getWidgets() ) {
			a.addWidget( widgetRestFromDso( widget ));
		}*/
		
		a.setLastRequestTimestamp(applicationDSO.getLastRequestTimestamp());
		
		Log.get().debug("Converted: " + a.toString());
		return a; 
	}

	public static ApplicationListRest applicationArrayListFromDSO(List<ApplicationDao> applicationDSOList) {
		
		ArrayList<ApplicationRest> applicationListREST = new ArrayList<ApplicationRest>();
		for ( ApplicationDao aDSO : applicationDSOList ) {
			ApplicationRest aREST = applicationFromDSO(aDSO);
			applicationListREST.add(aREST);
		}
		
		ApplicationListRest applicationArrayList= new ApplicationListRest();
		applicationArrayList.setApplications(applicationListREST);
		return applicationArrayList;
	}
	
	public static WidgetInputListRest getWidgetInputList(List<WidgetInputDao> widgetInputDaoList) {
		
		ArrayList<WidgetInputRest> widgetListRest = new ArrayList<WidgetInputRest>();
		for ( WidgetInputDao widgetInputDao : widgetInputDaoList ) {
			widgetListRest.add( getWidgetInput(widgetInputDao) );
		}
		
		WidgetInputListRest widgetInputArrayList= new WidgetInputListRest(widgetListRest);
	
		return widgetInputArrayList;
	}


	public static WidgetInputRest getWidgetInput(WidgetInputDao widgetInputDao) {
		
		
		WidgetInputRest  widgetInputRest = new WidgetInputRest();
	
		
		widgetInputRest.setWidgetOptionId( widgetInputDao.getWidgetOptionKey().getName() );
		widgetInputRest.setWidgetId( widgetInputDao.getWidgetOptionKey().getParent().getName() );
		
		if ( null == widgetInputDao.getParameters() ) {
			widgetInputRest.setParameters(new String[0]);
		} else {
			widgetInputRest.setParameters(widgetInputDao.getParameters());
		}
		widgetInputRest.setPersona(widgetInputDao.getPersona());
		widgetInputRest.setTimeStamp(""+widgetInputDao.getTimeStamp());
		widgetInputRest.setInputMechanism(widgetInputDao.getInputMechanism());
		widgetInputRest.setDelivered(widgetInputDao.isDelivered());
		widgetInputRest.setAge( (int)(System.currentTimeMillis()-widgetInputDao.getTimeStamp()) );
	
		return widgetInputRest; 
	}

}
