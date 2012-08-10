/**
 * 
 */
package org.instantplaces.im.server.rest.representation.json;

import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.logging.Log;
import org.instantplaces.im.server.dao.ApplicationDaot;
import org.instantplaces.im.server.dao.PlaceDaot;
import org.instantplaces.im.server.dao.WidgetDaot;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDaot;
import org.instantplaces.im.server.dao.WidgetParameterDao;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class RestConverter {

	public static PlaceRest getPlaceRest(PlaceDaot placeDao) {
		PlaceRest placeRest = new PlaceRest();
		placeRest.setPlaceId(placeDao.getPlaceId());
		placeRest.setPlaceName(placeDao.getPlaceName());
		placeRest.setPlaceReferenceCode(placeDao.getPlaceReferenceCode());
		placeRest.setPlaceEmailAddress(placeDao.getPlaceEmailAddress());
		placeRest.setPlaceInteractionUrl(placeDao.getPlaceInteractionUrl());
		placeRest.setPlacePhoneNumber(placeDao.getPlacePhoneNumber());
		
		return placeRest;
		
	}
	
	public static PlaceListRest getPlaceListRest(List<PlaceDaot> placeListDao) {
		
		
		ArrayList<PlaceRest> placesRest = new ArrayList<PlaceRest>();
		
		for ( PlaceDaot placeDao : placeListDao ) {
			placesRest.add( RestConverter.getPlaceRest(placeDao) );
		}
		
		PlaceListRest placeListRest = new PlaceListRest();
		placeListRest.setPlaces(placesRest);
		return placeListRest;
	}
	
	public static WidgetListRest widgetArrayListFromDso(List<WidgetDaot> widgetDSOList) {
		
		ArrayList<WidgetRest> widgetListREST = new ArrayList<WidgetRest>();
		for ( WidgetDaot wDSO : widgetDSOList ) {
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
	public static WidgetRest getWidgetRest(WidgetDaot widgetDao) {
		WidgetRest  widgetRest = new WidgetRest();
	
		if ( null != widgetDao.getWidgetOptions() ) {
			for (WidgetOptionDaot option : widgetDao.getWidgetOptions()) {
				widgetRest.addWidgetOption( RestConverter.widgetOptionFromDSO(option) );
			}
		}
		
		if ( null != widgetDao.getWidgetParameters() ) {
			for (WidgetParameterDao parameter : widgetDao.getWidgetParameters()) {
				widgetRest.addWidgetParameter( RestConverter.widgetParameterFromDSO(parameter) );
			}
		}
		
		widgetRest.setPlaceId (widgetDao.getApplicationKey().getParent().getName() );
		widgetRest.setApplicationId( widgetDao.getApplicationKey().getName() );
		widgetRest.setWidgetId(widgetDao.getWidgetId());
		widgetRest.setControlType(widgetDao.getControlType());
		
		widgetRest.setShortDescription(widgetDao.getShortDescription());
		widgetRest.setLongDescription(widgetDao.getLongDescription());
		
		
		return widgetRest; 
	}
	/**
	 * Converts a WidgetParameterDSO object to a WidgetParameterREST object
	 * 
	 * @param widgetOptionDSO
	 * @return
	 */
	public static WidgetParameterRest widgetParameterFromDSO(WidgetParameterDao widgetParameterDSO) {
		WidgetParameterRest wpREST = new WidgetParameterRest();
		wpREST.setName(widgetParameterDSO.getName());
		wpREST.setValue(widgetParameterDSO.getValue());
		
		
		return wpREST;
	}
	
	/**
	 * Converts a WidgetOptionDSO object to a WidgetOptionREST object
	 * 
	 * @param widgetOptionDSO
	 * @return
	 */
	public static WidgetOptionRest widgetOptionFromDSO(WidgetOptionDaot widgetOptionDSO) {
		WidgetOptionRest woREST = new WidgetOptionRest();
		woREST.setWidgetOptionId(widgetOptionDSO.getWidgetOptionId());
		woREST.setReferenceCode(widgetOptionDSO.getReferenceCode());
		woREST.setSuggestedReferenceCode(widgetOptionDSO.getSuggestedReferenceCode());
		woREST.setLongDescription(widgetOptionDSO.getLongDescripton());
		woREST.setShortDescription(widgetOptionDSO.getShortDescription());
		woREST.setIconUrl(widgetOptionDSO.getIconUrl());
		
		return woREST;
	}

	/**
	 * Converts a ApplicationDSO object to a ApplicationREST object.
	 * 
	 * @param widgetDSO
	 * @return
	 */
	public static ApplicationRest applicationFromDSO(ApplicationDaot applicationDao) {
		Log.debugFinest(RestConverter.class.getName(), "Converting ApplicationDao to ApplicationRest " + applicationDao.toString());
		ApplicationRest  a = new ApplicationRest();
	
		a.setPlaceId( applicationDao.getPlaceKey().getName() );
		a.setApplicationName( applicationDao.getApplicationName() );
		a.setApplicationId( applicationDao.getApplicationId() );
		a.setApplicationBaseUrl(applicationDao.getApplicationBaseUrl());
		
		
		/*for ( WidgetDSO widget : applicationDSO.getWidgets() ) {
			a.addWidget( widgetRestFromDso( widget ));
		}*/
		
		a.setLastRequestTimestamp(applicationDao.getLastRequestTimestamp());
		
		return a; 
	}

	public static ApplicationListRest applicationArrayListFromDSO(List<ApplicationDaot> applicationDSOList) {
		
		ArrayList<ApplicationRest> applicationListREST = new ArrayList<ApplicationRest>();
		for ( ApplicationDaot aDSO : applicationDSOList ) {
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
	
		widgetInputRest.setPlaceId(widgetInputDao.getWidgetOptionKey().getParent().getParent().getParent().getName());
		widgetInputRest.setApplicationId(widgetInputDao.getWidgetOptionKey().getParent().getParent().getName());
		widgetInputRest.setWidgetId( widgetInputDao.getWidgetOptionKey().getParent().getName() );
		
		widgetInputRest.setWidgetOptionId( widgetInputDao.getWidgetOptionKey().getName() );
		
		
		if ( null == widgetInputDao.getParameters() ) {
			widgetInputRest.setParameters(new String[0]);
		} else {
			widgetInputRest.setParameters(widgetInputDao.getParameters());
		}
		widgetInputRest.setUserId(widgetInputDao.getUserId());
		widgetInputRest.setNickname(widgetInputDao.getNickname());
		widgetInputRest.setTimeStamp(""+widgetInputDao.getTimeStamp());
		widgetInputRest.setInputMechanism(widgetInputDao.getInputMechanism());
		widgetInputRest.setDelivered(widgetInputDao.isDelivered());
		widgetInputRest.setAge( (int)(System.currentTimeMillis()-widgetInputDao.getTimeStamp()) );
		//widgetInputRest.getReferenceCode(widgetInputDao.ge
		return widgetInputRest; 
	}

}
