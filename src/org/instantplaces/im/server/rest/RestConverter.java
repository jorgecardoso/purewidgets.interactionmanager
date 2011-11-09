/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.ApplicationDAO;
import org.instantplaces.im.server.dao.WidgetDAO;
import org.instantplaces.im.server.dao.WidgetInputDAO;
import org.instantplaces.im.server.dao.WidgetOptionDAO;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class RestConverter {

	public static WidgetArrayListREST widgetArrayListFromDso(ArrayList<WidgetDAO> widgetDSOList) {
		
		ArrayList<WidgetREST> widgetListREST = new ArrayList<WidgetREST>();
		for ( WidgetDAO wDSO : widgetDSOList ) {
			WidgetREST wREST = RestConverter.widgetRestFromDso(wDSO);
			widgetListREST.add(wREST);
		}
		
		WidgetArrayListREST widgetArrayList= new WidgetArrayListREST();
		widgetArrayList.widgets = widgetListREST;
		return widgetArrayList;
	}

	/**
	 * Converts a WidgetDSO object to a WidgetREST object.
	 * 
	 * @param widgetDSO
	 * @return
	 */
	public static WidgetREST widgetRestFromDso(WidgetDAO widgetDSO) {
		WidgetREST  w = new WidgetREST();
	
		if ( null != widgetDSO.getWidgetOptions() ) {
			for (WidgetOptionDAO option : widgetDSO.getWidgetOptions()) {
				w.addWidgetOption( RestConverter.widgetOptionFromDSO(option) );
			}
		}
		
		w.setPlaceId (widgetDSO.getApplicationKey().getParent().getName() );
		w.setApplicationId( widgetDSO.getApplicationKey().getName() );
		w.setWidgetId(widgetDSO.getWidgetId());
		w.setControlType(widgetDSO.getControlType());
		w.setVolatileWidget(widgetDSO.isVolatileWidget());
		w.setShortDescription(widgetDSO.getShortDescription());
		w.setLongDescription(widgetDSO.getLongDescription());
		return w; 
	}

	/**
	 * Converts a WidgetOptionDSO object to a WidgetOptionREST object
	 * 
	 * @param widgetOptionDSO
	 * @return
	 */
	public static WidgetOptionREST widgetOptionFromDSO(WidgetOptionDAO widgetOptionDSO) {
		WidgetOptionREST woREST = new WidgetOptionREST();
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
	public static ApplicationREST applicationFromDSO(ApplicationDAO applicationDSO) {
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

	public static ApplicationArrayListREST applicationArrayListFromDSO(ArrayList<ApplicationDAO> applicationDSOList) {
		
		ArrayList<ApplicationREST> applicationListREST = new ArrayList<ApplicationREST>();
		for ( ApplicationDAO aDSO : applicationDSOList ) {
			ApplicationREST aREST = applicationFromDSO(aDSO);
			applicationListREST.add(aREST);
		}
		
		ApplicationArrayListREST applicationArrayList= new ApplicationArrayListREST();
		applicationArrayList.applications = applicationListREST;
		return applicationArrayList;
	}
	
	public static WidgetInputArrayListREST widgetInputArrayListFromDso(ArrayList<WidgetInputDAO> widgetInputDSOList) {
		
		ArrayList<WidgetInputREST> widgetListREST = new ArrayList<WidgetInputREST>();
		for ( WidgetInputDAO aDSO : widgetInputDSOList ) {
			WidgetInputREST aREST = widgetInputFromDSO(aDSO);
			widgetListREST.add(aREST);
		}
		
		WidgetInputArrayListREST widgetInputArrayList= new WidgetInputArrayListREST();
		widgetInputArrayList.inputs = widgetListREST;
		return widgetInputArrayList;
	}


	public static WidgetInputREST widgetInputFromDSO(WidgetInputDAO wiDso) {
		
		
		WidgetInputREST  w = new WidgetInputREST();
	
		
		w.setWidgetOptionId( wiDso.getWidgetOptionKey().getName() );
		w.setWidgetId( wiDso.getWidgetOptionKey().getParent().getName() );
		
		w.setParameters(wiDso.getParameters());
		w.setPersona(wiDso.getPersona());
		w.setTimeStamp(""+wiDso.getTimeStamp());
		w.setInputMechanism(wiDso.getInputMechanism());
		w.setDelivered(wiDso.isDelivered());
		
	
		return w; 
	}

}
